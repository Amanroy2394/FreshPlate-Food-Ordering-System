package com.ehpt.foodorder.web.api;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.Map;

import com.ehpt.foodorder.db.Db;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class ApiAuthServlet extends ApiBaseServlet {

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String path = req.getServletPath();
    try {
      if ("/api/auth/register".equals(path)) {
        handleRegister(req, resp);
        return;
      }
      if ("/api/auth/login".equals(path)) {
        handleLogin(req, resp);
        return;
      }
      sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Not found");
    } catch (ServletException e) {
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, e.getMessage());
    }
  }

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    if ("/api/auth/logout".equals(req.getServletPath())) {
      HttpSession session = req.getSession(false);
      if (session != null) {
        session.invalidate();
      }
      sendJson(resp, Map.of("ok", true));
      return;
    }
    sendJsonError(resp, HttpServletResponse.SC_NOT_FOUND, "Not found");
  }

  private void handleRegister(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    Map<String, Object> body;
    try {
      body = parseJsonBody(req);
    } catch (IOException e) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
      return;
    }

    String name = textParam(req, body, "name");
    String email = textParam(req, body, "email");
    String password = textParam(req, body, "password");

    if (name == null || name.isBlank() || email == null || email.isBlank() || password == null || password.isBlank()) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing required fields");
      return;
    }

    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "INSERT INTO users(name,email,password) VALUES (?,?,?)",
             PreparedStatement.RETURN_GENERATED_KEYS
         )) {
      ps.setString(1, name.trim());
      ps.setString(2, email.trim());
      ps.setString(3, password);
      ps.executeUpdate();
      try (ResultSet keys = ps.getGeneratedKeys()) {
        if (keys.next()) {
          sendJson(resp, Map.of("ok", true, "userId", keys.getInt(1)));
          return;
        }
      }
      sendJsonError(resp, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Could not create user");
    } catch (SQLIntegrityConstraintViolationException e) {
      sendJsonError(resp, HttpServletResponse.SC_CONFLICT, "Email already registered");
    } catch (SQLException e) {
      throw new ServletException(e);
    }
  }

  private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
    Map<String, Object> body;
    try {
      body = parseJsonBody(req);
    } catch (IOException e) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Invalid request body");
      return;
    }

    String email = textParam(req, body, "email");
    String password = textParam(req, body, "password");

    if (email == null || email.isBlank() || password == null || password.isBlank()) {
      sendJsonError(resp, HttpServletResponse.SC_BAD_REQUEST, "Missing email or password");
      return;
    }

    try (Connection c = Db.getConnection();
         PreparedStatement ps = c.prepareStatement(
             "SELECT id,name,email FROM users WHERE email=? AND password=?"
         )) {
      ps.setString(1, email.trim());
      ps.setString(2, password);
      try (ResultSet rs = ps.executeQuery()) {
        if (!rs.next()) {
          sendJsonError(resp, HttpServletResponse.SC_UNAUTHORIZED, "Invalid credentials");
          return;
        }
        HttpSession session = req.getSession(true);
        session.setAttribute("userId", rs.getInt("id"));
        session.setAttribute("userName", rs.getString("name"));
        sendJson(resp, Map.of(
            "ok", true,
            "userId", rs.getInt("id"),
            "userName", rs.getString("name"),
            "email", rs.getString("email")
        ));
      }
    } catch (SQLException e) {
      throw new ServletException(e);
    }
  }
}
