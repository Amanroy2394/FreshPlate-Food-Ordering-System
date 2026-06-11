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

public class ApiFoodServlet extends ApiBaseServlet {

  public record FoodDto(int id, String name, String category, String description, String imageUrl, double price, int discountPercent, boolean isPopular) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String pathInfo = idPathSegment(req);
    try {
      if (pathInfo == null) {
        listFoods(req, resp);
        return;
      }
      getFood(req, resp, pathInfo);
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  private void listFoods(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String q = req.getParameter("q");
    if (q == null) q = "";
    q = q.trim();

    String category = req.getParameter("category");
    if (category == null) category = "";
    category = category.trim();

    List<FoodDto> items = new ArrayList<>();
    String sql =
        "SELECT id,name,category,description,image_url,price,discount_percent,is_popular " +
        "FROM food_items WHERE is_available=1 " +
        "AND (name LIKE ? OR category LIKE ?) " +
        "AND (?='' OR category=?) " +
        "ORDER BY is_popular DESC, category, name";

    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      String like = "%" + q + "%";
      ps.setString(1, like);
      ps.setString(2, like);
      ps.setString(3, category);
      ps.setString(4, category);
      try (ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          items.add(new FoodDto(
              rs.getInt("id"),
              rs.getString("name"),
              rs.getString("category"),
              rs.getString("description"),
              rs.getString("image_url"),
              rs.getDouble("price"),
              rs.getInt("discount_percent"),
              rs.getInt("is_popular") == 1
          ));
        }
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }

    sendJson(resp, Map.of("ok", true, "items", items));
  }

  private void getFood(HttpServletRequest req, HttpServletResponse resp, String pathInfo) throws ServletException, IOException {
    int id;
    try {
      id = Integer.parseInt(pathInfo);
    } catch (NumberFormatException e) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid food id");
      return;
    }

    String sql = "SELECT id,name,category,description,image_url,price,discount_percent,is_popular FROM food_items WHERE id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Food item not found");
          return;
        }
        FoodDto item = new FoodDto(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getString("image_url"),
            rs.getDouble("price"),
            rs.getInt("discount_percent"),
            rs.getInt("is_popular") == 1
        );
        sendJson(resp, Map.of("ok", true, "item", item));
      }
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}
