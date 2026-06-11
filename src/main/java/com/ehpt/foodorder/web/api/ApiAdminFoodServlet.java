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

public class ApiAdminFoodServlet extends ApiBaseServlet {

  public record FoodDto(
      int id,
      String name,
      String category,
      String description,
      String imageUrl,
      double price,
      int discountPercent,
      boolean isAvailable,
      boolean isPopular
  ) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Integer adminId = getAdminId(req);
    if (adminId == null) {
      sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    String pathInfo = idPathSegment(req);
    try {
      if (pathInfo == null) {
        listFoods(resp);
        return;
      }
      getFood(resp, pathInfo);
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Integer adminId = getAdminId(req);
    if (adminId == null) {
      sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    try {
      Map<String, Object> body = parseJsonBody(req);
      String pathInfo = idPathSegment(req);
      if (pathInfo == null) {
        createFood(req, resp, body);
        return;
      }
      updateFood(req, resp, pathInfo, body);
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    Integer adminId = getAdminId(req);
    if (adminId == null) {
      sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
      return;
    }
    String pathInfo = idPathSegment(req);
    if (pathInfo == null) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing food id");
      return;
    }
    try {
      deleteFood(resp, pathInfo);
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void listFoods(HttpServletResponse resp) throws ServletException, IOException {
    List<FoodDto> items = new ArrayList<>();
    String sql = "SELECT id,name,category,description,price,discount_percent,image_url,is_available,is_popular FROM food_items ORDER BY id DESC";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
      while (rs.next()) {
        items.add(new FoodDto(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getString("image_url"),
            rs.getDouble("price"),
            rs.getInt("discount_percent"),
            rs.getInt("is_available") == 1,
            rs.getInt("is_popular") == 1
        ));
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
    sendJson(resp, Map.of("ok", true, "items", items));
  }

  private void getFood(HttpServletResponse resp, String pathInfo) throws ServletException, IOException {
    int id;
    try {
      id = Integer.parseInt(pathInfo);
    } catch (NumberFormatException e) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid food id");
      return;
    }
    String sql = "SELECT id,name,category,description,price,discount_percent,image_url,is_available,is_popular FROM food_items WHERE id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Food item not found");
          return;
        }
        sendJson(resp, Map.of("ok", true, "item", new FoodDto(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getString("image_url"),
            rs.getDouble("price"),
            rs.getInt("discount_percent"),
            rs.getInt("is_available") == 1,
            rs.getInt("is_popular") == 1
        )));
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void createFood(HttpServletRequest req, HttpServletResponse resp, Map<String, Object> body) throws ServletException, IOException {
    var dto = buildFoodDto(body);
    String sql = "INSERT INTO food_items(name,category,description,price,discount_percent,image_url,is_available,is_popular) VALUES (?,?,?,?,?,?,?,?)";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS)) {
      ps.setString(1, dto.name());
      ps.setString(2, dto.category());
      ps.setString(3, dto.description());
      ps.setDouble(4, dto.price());
      ps.setInt(5, dto.discountPercent());
      ps.setString(6, dto.imageUrl());
      ps.setInt(7, dto.isAvailable() ? 1 : 0);
      ps.setInt(8, dto.isPopular() ? 1 : 0);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (!keys.next()) {
          sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not save food item");
          return;
        }
        sendJson(resp, Map.of("ok", true, "id", keys.getInt(1)));
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void updateFood(HttpServletRequest req, HttpServletResponse resp, String pathInfo, Map<String, Object> body) throws ServletException, IOException {
    int id;
    try {
      id = Integer.parseInt(pathInfo);
    } catch (NumberFormatException e) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid food id");
      return;
    }
    var dto = buildFoodDto(body);
    String sql = "UPDATE food_items SET name=?,category=?,description=?,price=?,discount_percent=?,image_url=?,is_available=?,is_popular=? WHERE id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, dto.name());
      ps.setString(2, dto.category());
      ps.setString(3, dto.description());
      ps.setDouble(4, dto.price());
      ps.setInt(5, dto.discountPercent());
      ps.setString(6, dto.imageUrl());
      ps.setInt(7, dto.isAvailable() ? 1 : 0);
      ps.setInt(8, dto.isPopular() ? 1 : 0);
      ps.setInt(9, id);
      ps.executeUpdate();
      sendJson(resp, Map.of("ok", true, "id", id));
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private void deleteFood(HttpServletResponse resp, String pathInfo) throws ServletException, IOException {
    int id;
    try {
      id = Integer.parseInt(pathInfo);
    } catch (NumberFormatException e) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid food id");
      return;
    }
    String sql = "DELETE FROM food_items WHERE id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      ps.executeUpdate();
      sendJson(resp, Map.of("ok", true, "id", id));
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private FoodDto buildFoodDto(Map<String, Object> body) {
    String name = nvl(body.get("name"));
    String category = nvl(body.get("category"));
    String description = nvl(body.get("description"));
    String imageUrl = nvl(body.get("imageUrl"));
    double price = parseDouble(body.get("price"), 0.0);
    int discount = parseInt(body.get("discountPercent"), 0);
    boolean available = parseBoolean(body.get("isAvailable"));
    boolean popular = parseBoolean(body.get("isPopular"));
    if (discount < 0) discount = 0;
    if (discount > 90) discount = 90;
    if (name.isBlank()) {
      name = "Untitled item";
    }
    if (category.isBlank()) {
      category = "Uncategorized";
    }
    return new FoodDto(0, name, category, description, imageUrl, price, discount, available, popular);
  }

  private static String nvl(Object value) {
    if (value == null) return "";
    return value.toString().trim();
  }

  private static double parseDouble(Object value, double defaultValue) {
    if (value == null) {
      return defaultValue;
    }
    try {
      return Double.parseDouble(value.toString());
    } catch (Exception e) {
      return defaultValue;
    }
  }

  private static int parseInt(Object value, int defaultValue) {
    if (value == null) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(value.toString());
    } catch (Exception e) {
      return defaultValue;
    }
  }

  private static boolean parseBoolean(Object value) {
    if (value == null) {
      return false;
    }
    return "true".equalsIgnoreCase(value.toString().trim()) || "1".equals(value.toString().trim());
  }
}
