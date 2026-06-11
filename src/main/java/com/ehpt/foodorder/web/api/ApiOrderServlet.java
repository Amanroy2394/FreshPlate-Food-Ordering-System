package com.ehpt.foodorder.web.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ehpt.foodorder.db.Db;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiOrderServlet extends ApiBaseServlet {

  public record OrderDto(int id, String createdAt, String status, double total, String deliveryAddress) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Integer userId = getUserId(req);
    if (userId == null) {
      sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    String pathInfo = idPathSegment(req);
    try {
      if (pathInfo == null) {
        listOrders(resp, userId);
        return;
      }
      if ("place".equals(pathInfo)) {
        sendJsonError(resp, HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Use POST to place orders");
        return;
      }
      getOrder(resp, userId, pathInfo);
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Integer userId = getUserId(req);
    if (userId == null) {
      sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    if (!"/api/orders/place" .equals(req.getServletPath())) {
      sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Not found");
      return;
    }
    try {
      placeOrder(req, resp, userId);
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void listOrders(HttpServletResponse resp, int userId) throws ServletException, IOException {
    List<OrderDto> orders = new ArrayList<>();
    String sql = "SELECT id, created_at, status, total, delivery_address FROM orders WHERE user_id=? ORDER BY id DESC";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, userId);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          orders.add(new OrderDto(
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
    sendJson(resp, Map.of("ok", true, "orders", orders));
  }

  private void getOrder(HttpServletResponse resp, int userId, String pathInfo) throws ServletException, IOException {
    int orderId;
    try {
      orderId = Integer.parseInt(pathInfo);
    } catch (NumberFormatException e) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid order id");
      return;
    }

    String sql = "SELECT id, created_at, status, total, delivery_address FROM orders WHERE id=? AND user_id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, orderId);
      ps.setInt(2, userId);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Order not found");
          return;
        }
        sendJson(resp, Map.of("ok", true, "order", new OrderDto(
            rs.getInt("id"),
            rs.getString("created_at"),
            rs.getString("status"),
            rs.getDouble("total"),
            rs.getString("delivery_address")
        )));
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void placeOrder(HttpServletRequest req, HttpServletResponse resp, int userId) throws ServletException, IOException {
    Map<String, Object> body = parseJsonBody(req);
    String address = textParam(req, body, "address");
    if (address == null || address.isBlank()) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing delivery address");
      return;
    }

    try (Connection c = Db.getConnection()) {
      c.setAutoCommit(false);
      try {
        String cartSql =
            "SELECT fi.id as item_id, fi.name, fi.price, ci.qty " +
            "FROM cart_items ci JOIN food_items fi ON fi.id = ci.item_id " +
            "WHERE ci.user_id = ? FOR UPDATE";
        record CartItem(int itemId, String name, double price, int qty) {}
        List<CartItem> cart = new ArrayList<>();
        double total = 0.0;
        try (PreparedStatement ps = c.prepareStatement(cartSql)) {
          ps.setInt(1, userId);
          try (ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
              int qty = rs.getInt("qty");
              double price = rs.getDouble("price");
              cart.add(new CartItem(
                  rs.getInt("item_id"),
                  rs.getString("name"),
                  price,
                  qty
              ));
              total += price * qty;
            }
          }
        }

        if (cart.isEmpty()) {
          c.rollback();
          sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Cart is empty");
          return;
        }

        int orderId;
        try (PreparedStatement ps = c.prepareStatement(
            "INSERT INTO orders(user_id,total,status,delivery_address) VALUES (?,?,?,?)",
            Statement.RETURN_GENERATED_KEYS
        )) {
          ps.setInt(1, userId);
          ps.setDouble(2, total);
          ps.setString(3, "PLACED");
          ps.setString(4, address.trim());
          ps.executeUpdate();
          try (ResultSet keys = ps.getGeneratedKeys()) {
            if (!keys.next()) {
              throw new ServletException("Could not create order");
            }
            orderId = keys.getInt(1);
          }
        }

        try (PreparedStatement ps = c.prepareStatement(
            "INSERT INTO order_items(order_id,item_id,item_name,item_price,qty,line_total) VALUES (?,?,?,?,?,?)"
        )) {
          for (CartItem item : cart) {
            ps.setInt(1, orderId);
            ps.setInt(2, item.itemId());
            ps.setString(3, item.name());
            ps.setDouble(4, item.price());
            ps.setInt(5, item.qty());
            ps.setDouble(6, item.price() * item.qty());
            ps.addBatch();
          }
          ps.executeBatch();
        }

        try (PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id=?")) {
          ps.setInt(1, userId);
          ps.executeUpdate();
        }

        c.commit();
        sendJson(resp, Map.of("ok", true, "orderId", orderId, "total", total));
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
