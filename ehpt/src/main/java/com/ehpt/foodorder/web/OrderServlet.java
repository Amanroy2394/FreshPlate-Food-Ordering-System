package com.ehpt.foodorder.web;

import com.ehpt.foodorder.db.Db;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

@WebServlet(urlPatterns = {"/user/checkout", "/user/orders"})
public class OrderServlet extends HttpServlet {

  public record OrderRow(int id, String createdAt, String status, double total, String address) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer userId = (Integer) req.getSession().getAttribute("userId");
    if (userId == null) {
      resp.sendRedirect(req.getContextPath() + "/login.jsp");
      return;
    }

    String path = req.getServletPath();
    if ("/user/checkout".equals(path)) {
      req.getRequestDispatcher("/user/checkout.jsp").forward(req, resp);
      return;
    }
    if ("/user/orders".equals(path)) {
      List<OrderRow> orders = new ArrayList<>();
      String sql = "SELECT id, created_at, status, total, delivery_address FROM orders WHERE user_id=? ORDER BY id DESC";
      try (Connection c = Db.getConnection();
           PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setInt(1, userId);
        try (ResultSet rs = ps.executeQuery()) {
          while (rs.next()) {
            orders.add(new OrderRow(
                rs.getInt("id"),
                rs.getString("created_at"),
                rs.getString("status"),
                rs.getDouble("total"),
                rs.getString("delivery_address")
            ));
          }
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }
      req.setAttribute("orders", orders);
      req.getRequestDispatcher("/user/orders.jsp").forward(req, resp);
      return;
    }

    resp.sendError(404);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer userId = (Integer) req.getSession().getAttribute("userId");
    if (userId == null) {
      resp.sendRedirect(req.getContextPath() + "/login.jsp");
      return;
    }

    String path = req.getServletPath();
    if (!"/user/checkout".equals(path)) {
      resp.sendError(404);
      return;
    }

    String address = req.getParameter("address");
    if (address == null) address = "";
    address = address.trim();
    if (address.isEmpty()) {
      resp.sendRedirect(req.getContextPath() + "/user/checkout?err=addr");
      return;
    }

    try (Connection c = Db.getConnection()) {
      c.setAutoCommit(false);
      try {
        // 1) Read cart snapshot
        record CartSnap(int itemId, String name, double price, int qty) {}
        List<CartSnap> cart = new ArrayList<>();
        double total = 0.0;

        String cartSql =
            "SELECT fi.id as item_id, fi.name, fi.price, ci.qty " +
            "FROM cart_items ci JOIN food_items fi ON fi.id = ci.item_id " +
            "WHERE ci.user_id = ? FOR UPDATE";

        try (PreparedStatement ps = c.prepareStatement(cartSql)) {
          ps.setInt(1, userId);
          try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
              int qty = rs.getInt("qty");
              double price = rs.getDouble("price");
              total += (price * qty);
              cart.add(new CartSnap(rs.getInt("item_id"), rs.getString("name"), price, qty));
            }
          }
        }

        if (cart.isEmpty()) {
          c.rollback();
          resp.sendRedirect(req.getContextPath() + "/user/cart?err=empty");
          return;
        }

        // 2) Create order
        int orderId;
        try (PreparedStatement ps = c.prepareStatement(
            "INSERT INTO orders(user_id,total,status,delivery_address) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS
        )) {
          ps.setInt(1, userId);
          ps.setDouble(2, total);
          ps.setString(3, "PLACED");
          ps.setString(4, address);
          ps.executeUpdate();
          try (ResultSet keys = ps.getGeneratedKeys()) {
            if (!keys.next()) throw new ServletException("Failed to create order");
            orderId = keys.getInt(1);
          }
        }

        // 3) Insert order items
        try (PreparedStatement ps = c.prepareStatement(
            "INSERT INTO order_items(order_id,item_id,item_name,item_price,qty,line_total) VALUES (?,?,?,?,?,?)"
        )) {
          for (CartSnap it : cart) {
            ps.setInt(1, orderId);
            ps.setInt(2, it.itemId());
            ps.setString(3, it.name());
            ps.setDouble(4, it.price());
            ps.setInt(5, it.qty());
            ps.setDouble(6, it.price() * it.qty());
            ps.addBatch();
          }
          ps.executeBatch();
        }

        // 4) Clear cart
        try (PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id=?")) {
          ps.setInt(1, userId);
          ps.executeUpdate();
        }

        c.commit();
        resp.sendRedirect(req.getContextPath() + "/user/orders?ok=1");
      } catch (Exception e) {
        c.rollback();
        throw e;
      } finally {
        c.setAutoCommit(true);
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}

