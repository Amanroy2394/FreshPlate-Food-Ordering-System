package com.ehpt.foodorder.web;

import com.ehpt.foodorder.db.Db;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AdminAuthServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    if ("/admin/logout".equals(req.getServletPath())) {
      HttpSession s = req.getSession(false);
      if (s != null) s.invalidate();
      resp.sendRedirect(req.getContextPath() + "/admin/login.jsp");
      return;
    }
    resp.sendError(404);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    if (!"/admin/login".equals(req.getServletPath())) {
      resp.sendError(404);
      return;
    }

    String username = req.getParameter("username");
    String password = req.getParameter("password");

    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT id, username FROM admin_users WHERE username=? AND password=?"
         )) {
      ps.setString(1, username);
      ps.setString(2, password);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          resp.sendRedirect(req.getContextPath() + "/admin/login.jsp?err=1");
          return;
        }
        HttpSession s = req.getSession(true);
        s.setAttribute("adminId", rs.getInt("id"));
        s.setAttribute("adminUser", rs.getString("username"));
        resp.sendRedirect(req.getContextPath() + "/admin/foods");
      }
    } catch (SQLException e) {
      resp.sendRedirect(req.getContextPath() + "/admin/login.jsp?db=1");
    } catch (Exception e) {
      throw new ServletException(e);
    }
  }
}

