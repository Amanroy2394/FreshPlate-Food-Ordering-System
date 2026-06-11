package com.ehpt.foodorder.web;

import com.ehpt.foodorder.db.Db;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

@WebServlet(urlPatterns = {"/auth/login", "/auth/register"})
public class AuthServlet extends HttpServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    if ("/auth/register".equals(path)) {
      handleRegister(req, resp);
      return;
    }
    if ("/auth/login".equals(path)) {
      handleLogin(req, resp);
      return;
    }
    resp.sendError(404);
  }

  private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String name = req.getParameter("name");
    String email = req.getParameter("email");
    String password = req.getParameter("password");

    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "INSERT INTO users(name,email,password) VALUES (?,?,?)"
         )) {
      ps.setString(1, name);
      ps.setString(2, email);
      ps.setString(3, password);
      ps.executeUpdate();
      resp.sendRedirect(req.getContextPath() + "/register.jsp?ok=1");
    } catch (Exception e) {
      resp.sendError(500, e.getMessage());
    }
  }

  private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String email = req.getParameter("email");
    String password = req.getParameter("password");

    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT id,name,email FROM users WHERE email=? AND password=?"
         )) {
      ps.setString(1, email);
      ps.setString(2, password);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          resp.sendRedirect(req.getContextPath() + "/login.jsp?err=1");
          return;
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", rs.getInt("id"));
        session.setAttribute("userName", rs.getString("name"));
        resp.sendRedirect(req.getContextPath() + "/user/home.jsp");
      }
    } catch (Exception e) {
      resp.sendError(500, e.getMessage());
    }
  }
}

