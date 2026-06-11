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

public class MenuServlet extends HttpServlet {

  public record FoodItem(int id, String name, String category, String description, String imageUrl, double price, int discountPercent, boolean isPopular) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    Integer userId = (Integer) req.getSession().getAttribute("userId");
    if (userId == null) {
      resp.sendRedirect(req.getContextPath() + "/login.jsp");
      return;
    }

    String q = req.getParameter("q");
    if (q == null) q = "";
    q = q.trim();

    String category = req.getParameter("category");
    if (category == null) category = "";
    category = category.trim();

    List<FoodItem> items = new ArrayList<>();

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
          items.add(new FoodItem(
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

    req.setAttribute("q", q);
    req.setAttribute("category", category);
    req.setAttribute("items", items);
    req.getRequestDispatcher("/user/menu.jsp").forward(req, resp);
  }
}

