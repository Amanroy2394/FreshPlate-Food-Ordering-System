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

public class AdminFoodServlet extends HttpServlet {

  public record FoodRow(
      int id,
      String name,
      String category,
      String description,
      double price,
      int discountPercent,
      String imageUrl,
      boolean isAvailable,
      boolean isPopular
  ) {}

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!isAdmin(req)) {
      resp.sendRedirect(req.getContextPath() + "/admin/login.jsp");
      return;
    }

    String path = req.getServletPath();
    if ("/admin/foods".equals(path)) {
      List<FoodRow> foods = new ArrayList<>();
      String sql = "SELECT id,name,category,description,price,discount_percent,image_url,is_available,is_popular FROM food_items ORDER BY id DESC";
      try (Connection c = Db.getConnection();
           PreparedStatement ps = c.prepareStatement(sql);
           ResultSet rs = ps.executeQuery()) {
        while (rs.next()) {
          foods.add(new FoodRow(
              rs.getInt("id"),
              rs.getString("name"),
              rs.getString("category"),
              rs.getString("description"),
              rs.getDouble("price"),
              rs.getInt("discount_percent"),
              rs.getString("image_url"),
              rs.getInt("is_available") == 1,
              rs.getInt("is_popular") == 1
          ));
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }
      req.setAttribute("foods", foods);
      req.getRequestDispatcher("/admin/foods.jsp").forward(req, resp);
      return;
    }

    if ("/admin/foods/new".equals(path)) {
      req.setAttribute("mode", "new");
      req.getRequestDispatcher("/admin/food-form.jsp").forward(req, resp);
      return;
    }

    if ("/admin/foods/edit".equals(path)) {
      int id = Integer.parseInt(req.getParameter("id"));
      try {
        FoodRow row = loadFood(id);
        if (row == null) {
          resp.sendError(404);
          return;
        }
        req.setAttribute("mode", "edit");
        req.setAttribute("food", row);
        req.getRequestDispatcher("/admin/food-form.jsp").forward(req, resp);
        return;
      } catch (Exception e) {
        throw new ServletException(e);
      }
    }

    resp.sendError(404);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    if (!isAdmin(req)) {
      resp.sendRedirect(req.getContextPath() + "/admin/login.jsp");
      return;
    }

    String path = req.getServletPath();
    try {
      if ("/admin/foods/save".equals(path)) {
        saveFood(req);
        resp.sendRedirect(req.getContextPath() + "/admin/foods?ok=1");
        return;
      }
      if ("/admin/foods/delete".equals(path)) {
        int id = Integer.parseInt(req.getParameter("id"));
        try (Connection c = Db.getConnection();
             PreparedStatement ps = c.prepareStatement("DELETE FROM food_items WHERE id=?")) {
          ps.setInt(1, id);
          ps.executeUpdate();
        }
        resp.sendRedirect(req.getContextPath() + "/admin/foods?del=1");
        return;
      }
      resp.sendError(404);
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }

  private static boolean isAdmin(HttpServletRequest req) {
    return req.getSession().getAttribute("adminId") != null;
  }

  private static FoodRow loadFood(int id) throws Exception {
    String sql = "SELECT id,name,category,description,price,discount_percent,image_url,is_available,is_popular FROM food_items WHERE id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setInt(1, id);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) return null;
        return new FoodRow(
            rs.getInt("id"),
            rs.getString("name"),
            rs.getString("category"),
            rs.getString("description"),
            rs.getDouble("price"),
            rs.getInt("discount_percent"),
            rs.getString("image_url"),
            rs.getInt("is_available") == 1,
            rs.getInt("is_popular") == 1
        );
      }
    }
  }

  private static void saveFood(HttpServletRequest req) throws Exception {
    String idRaw = req.getParameter("id");
    String name = nvl(req.getParameter("name"));
    String category = nvl(req.getParameter("category"));
    String description = nvl(req.getParameter("description"));
    double price = parseDouble(req.getParameter("price"), 0.0);
    int discount = parseInt(req.getParameter("discountPercent"), 0);
    String imageUrl = nvl(req.getParameter("imageUrl"));
    boolean available = "on".equals(req.getParameter("isAvailable"));
    boolean popular = "on".equals(req.getParameter("isPopular"));

    if (discount < 0) discount = 0;
    if (discount > 90) discount = 90;

    if (idRaw == null || idRaw.isBlank()) {
      String sql = "INSERT INTO food_items(name,category,description,price,discount_percent,image_url,is_available,is_popular) VALUES (?,?,?,?,?,?,?,?)";
      try (Connection c = Db.getConnection();
           PreparedStatement ps = c.prepareStatement(sql)) {
        ps.setString(1, name);
        ps.setString(2, category);
        ps.setString(3, description);
        ps.setDouble(4, price);
        ps.setInt(5, discount);
        ps.setString(6, imageUrl);
        ps.setInt(7, available ? 1 : 0);
        ps.setInt(8, popular ? 1 : 0);
        ps.executeUpdate();
      }
      return;
    }

    int id = Integer.parseInt(idRaw);
    String sql = "UPDATE food_items SET name=?,category=?,description=?,price=?,discount_percent=?,image_url=?,is_available=?,is_popular=? WHERE id=?";
    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(sql)) {
      ps.setString(1, name);
      ps.setString(2, category);
      ps.setString(3, description);
      ps.setDouble(4, price);
      ps.setInt(5, discount);
      ps.setString(6, imageUrl);
      ps.setInt(7, available ? 1 : 0);
      ps.setInt(8, popular ? 1 : 0);
      ps.setInt(9, id);
      ps.executeUpdate();
    }
  }

  private static String nvl(String s) {
    if (s == null) return "";
    return s.trim();
  }

  private static int parseInt(String s, int d) {
    try { return Integer.parseInt(s); } catch (Exception e) { return d; }
  }

  private static double parseDouble(String s, double d) {
    try { return Double.parseDouble(s); } catch (Exception e) { return d; }
  }
}

