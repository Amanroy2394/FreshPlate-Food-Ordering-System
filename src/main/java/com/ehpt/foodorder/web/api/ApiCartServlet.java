package com.ehpt.foodorder.web.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ehpt.foodorder.db.Db;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class ApiCartServlet extends ApiBaseServlet {

  public record CartLine(int itemId, String name, String imageUrl, double price, int qty, double lineTotal) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Integer userId = getUserId(req);
    if (userId == null) {
      sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    try {
      sendCart(resp, userId);
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

    String path = req.getServletPath();
    try {
      Map<String, Object> body = parseJsonBody(req);
      if ("/api/cart/add".equals(path)) {
        addToCart(userId, req, body);
        sendCart(resp, userId);
        return;
      }
      if ("/api/cart/update".equals(path)) {
        updateCart(userId, req, body);
        sendCart(resp, userId);
        return;
      }
      if ("/api/cart/remove".equals(path)) {
        removeFromCart(userId, req, body);
        sendCart(resp, userId);
        return;
      }
      sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Not found");
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Integer userId = getUserId(req);
    if (userId == null) {
      sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    if (!"/api/cart/remove".equals(req.getServletPath())) {
      sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Not found");
      return;
    }
    try {
      removeFromCart(userId, req, Map.of());
      sendCart(resp, userId);
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void sendCart(HttpServletResponse resp, int userId) throws ServletException, IOException {
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

    sendJson(resp, Map.of("ok", true, "lines", lines, "total", total));
  }

  private void addToCart(int userId, HttpServletRequest req, Map<String, Object> body) throws ServletException {
    int itemId = intParam(req, body, "itemId", -1);
    int qty = intParam(req, body, "qty", 1);
    if (itemId < 1) {
      throw new ServletException("Missing or invalid itemId");
    }
    qty = Math.max(1, Math.min(20, qty));
    try (Connection c = Db.getConnection();
         PreparedStatement check = c.prepareStatement("SELECT id FROM food_items WHERE id=? AND is_available=1")) {
      check.setInt(1, itemId);
      try (ResultSet rs = check.executeQuery()) {
        if (!rs.next()) {
          throw new ServletException("Item unavailable");
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
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
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void updateCart(int userId, HttpServletRequest req, Map<String, Object> body) throws ServletException {
    int itemId = intParam(req, body, "itemId", -1);
    int qty = intParam(req, body, "qty", -1);
    if (itemId < 1 || qty < 1) {
      throw new ServletException("Missing or invalid itemId or qty");
    }
    qty = Math.max(1, Math.min(20, qty));
    String sql = "UPDATE cart_items SET qty=? WHERE user_id=? AND item_id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, qty);
      ps.setInt(2, userId);
      ps.setInt(3, itemId);
      ps.executeUpdate();
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void removeFromCart(int userId, HttpServletRequest req, Map<String, Object> body) throws ServletException {
    int itemId = -1;
    String pathId = idPathSegment(req);
    if (pathId != null) {
      try {
        itemId = Integer.parseInt(pathId);
      } catch (NumberFormatException ignored) {
        itemId = -1;
      }
    }
    if (itemId < 1) {
      itemId = intParam(req, body, "itemId", -1);
    }
    if (itemId < 1) {
      throw new ServletException("Missing or invalid itemId");
    }
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement("DELETE FROM cart_items WHERE user_id=? AND item_id=?")) {
      ps.setInt(1, userId);
      ps.setInt(2, itemId);
      ps.executeUpdate();
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
