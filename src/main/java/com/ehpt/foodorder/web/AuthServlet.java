package com.ehpt.foodorder.web;

import com.ehpt.foodorder.db.Db;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLIntegrityConstraintViolationException;
import java.sql.SQLException;
import java.util.regex.Pattern;

public class AuthServlet extends HttpServlet {

  private static final Pattern SQLI_LIKE = Pattern.compile(
      "(?i)(\\bunion\\b|\\bselect\\b|\\binsert\\b|\\bupdate\\b|\\bdelete\\b|\\bdrop\\b|\\bor\\b\\s+\\d+\\s*=\\s*\\d+|--|/\\*|\\*/|;)"
  );

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
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
    } catch (SQLIntegrityConstraintViolationException e) {
      resp.sendRedirect(req.getContextPath() + "/register.jsp?err=email");
    } catch (SQLException e) {
      if (isAccessDenied(e)) {
        resp.sendRedirect(req.getContextPath() + "/register.jsp?err=db");
        return;
      }
      resp.sendError(500, e.getMessage());
    }
  }

  private void handleLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
    String email = req.getParameter("email");
    String password = req.getParameter("password");

    if (looksLikeSqlInjection(email) || looksLikeSqlInjection(password)) {
      getServletContext().log("Blocked suspicious login input for email=" + safeForLog(email));
      resp.sendRedirect(req.getContextPath() + "/login.jsp?err=sqli");
      return;
    }

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
    } catch (SQLException e) {
      if (isAccessDenied(e)) {
        resp.sendRedirect(req.getContextPath() + "/login.jsp?err=db");
        return;
      }
      resp.sendError(500, e.getMessage());
    }
  }

  private static boolean looksLikeSqlInjection(String s) {
    if (s == null) {
      return false;
    }
    String trimmed = s.trim();
    if (trimmed.isEmpty()) {
      return false;
    }
    // Fast-path: block common metacharacters + keywords used in SQLi payloads.
    return trimmed.indexOf('\'') >= 0 || trimmed.indexOf('"') >= 0 || SQLI_LIKE.matcher(trimmed).find();
  }

  private static String safeForLog(String s) {
    if (s == null) {
      return "<null>";
    }
    String t = s.replaceAll("[\\r\\n\\t]", " ").trim();
    if (t.length() > 200) {
      return t.substring(0, 200) + "...";
    }
    return t;
  }

  /** MySQL 1045 = ER_ACCESS_DENIED_ERROR; walk chained SQLExceptions. */
  private static boolean isAccessDenied(SQLException e) {
    for (SQLException cur = e; cur != null; cur = cur.getNextException()) {
      if (cur.getErrorCode() == 1045) {
        return true;
      }
      String m = cur.getMessage();
      if (m != null && (m.contains("Access denied") || m.contains("denied for user"))) {
        return true;
      }
    }
    return false;
  }
}
