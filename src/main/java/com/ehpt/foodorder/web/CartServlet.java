package com.ehpt.foodorder.web;

import com.ehpt.foodorder.db.Db;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class CartServlet extends HttpServlet {

  public record CartLine(int itemId, String name, String imageUrl, double price, int qty, double lineTotal) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer userId = (Integer) req.getSession().getAttribute("userId");
    if (userId == null) {
      resp.sendRedirect(req.getContextPath() + "/login.jsp");
      return;
    }

    List<CartLine> lines = new ArrayList<>();
    double total = 0.0;

    String sql =
        "SELECT fi.id as item_id, fi.name, fi.image_url, fi.price, ci.qty " +
        "FROM cart_items ci JOIN food_items fi ON fi.id = ci.item_id " +
        "WHERE ci.user_id = ? ORDER BY fi.name";

    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          int qty = rs.getInt("qty");
          double price = rs.getDouble("price");
          double lineTotal = price * qty;
          total += lineTotal;
          lines.add(new CartLine(
              rs.getInt("item_id"),
              rs.getString("name"),
              rs.getString("image_url"),
              price,
              qty,
              lineTotal
          ));
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    req.setAttribute("lines", lines);
    req.setAttribute("total", total);
    req.getRequestDispatcher("/user/cart.jsp").forward(req, resp);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer userId = (Integer) req.getSession().getAttribute("userId");
    if (userId == null) {
      resp.sendRedirect(req.getContextPath() + "/login.jsp");
      return;
    }

    String path = req.getServletPath();
    try {
      if ("/user/cart/add".equals(path)) {
        int itemId = Integer.parseInt(req.getParameter("itemId"));
        int qty = parseQty(req.getParameter("qty"));
        addToCart(userId, itemId, qty);
        resp.sendRedirect(req.getContextPath() + "/user/cart");
        return;
      }
      if ("/user/cart/update".equals(path)) {
        int itemId = Integer.parseInt(req.getParameter("itemId"));
        int qty = parseQty(req.getParameter("qty"));
        updateQty(userId, itemId, qty);
        resp.sendRedirect(req.getContextPath() + "/user/cart");
        return;
      }
      if ("/user/cart/remove".equals(path)) {
        int itemId = Integer.parseInt(req.getParameter("itemId"));
        remove(userId, itemId);
        resp.sendRedirect(req.getContextPath() + "/user/cart");
        return;
      }
      resp.sendError(404);
    } catch (IllegalArgumentException e) {
      resp.sendRedirect(req.getContextPath() + "/user/menu?err=cart");
    } catch (Exception e) {
      resp.sendRedirect(req.getContextPath() + "/user/menu?err=cart");
    }
  }

  private static int parseQty(String raw) {
    int q = 1;
    try {
      q = Integer.parseInt(raw);
    } catch (Exception ignored) {}
    if (q < 1) q = 1;
    if (q > 20) q = 20;
    return q;
  }

  private static void addToCart(int userId, int itemId, int qty) throws Exception {
    try (Connection c = Db.getConnection();
         PreparedStatement check = c.prepareStatement(
             "SELECT id FROM food_items WHERE id=? AND is_available=1"
         )) {
      check.setInt(1, itemId);
      try (ResultSet rs = check.executeQuery()) {
        if (!rs.next()) {
          throw new IllegalArgumentException("Item unavailable");
        }
      }
    }

    String sql =
        "INSERT INTO cart_items(user_id, item_id, qty) VALUES (?,?,?) " +
        "ON DUPLICATE KEY UPDATE qty = LEAST(20, qty + VALUES(qty))";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.setInt(2, itemId);
      ps.setInt(3, qty);
      ps.executeUpdate();
    }
  }

  private static void updateQty(int userId, int itemId, int qty) throws Exception {
    String sql = "UPDATE cart_items SET qty=? WHERE user_id=? AND item_id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, qty);
      ps.setInt(2, userId);
      ps.setInt(3, itemId);
      ps.executeUpdate();
    }
  }

  private static void remove(int userId, int itemId) throws Exception {
    String sql = "DELETE FROM cart_items WHERE user_id=? AND item_id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, userId);
      ps.setInt(2, itemId);
      ps.executeUpdate();
    }
  }
}

