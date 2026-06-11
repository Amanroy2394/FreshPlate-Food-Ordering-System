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
import java.sql.Statement;

/**
 * Project 1 (detection) — intentionally vulnerable endpoints, isolated from main flows.
 * <ul>
 *   <li>SQLi: POST {@code /vuln/sqli-login}</li>
 *   <li>XSS: GET {@code /vuln/xss-echo} → {@code vuln/xss.jsp} (reflects {@code input} / {@code q})</li>
 *   <li>Brute force: POST {@code /vuln/bruteforce-login}</li>
 *   <li>Weak session / hijack demos: {@code /vuln/weak-session}, {@code /vuln/weak-session-access}, {@code /vuln/session-fixation}</li>
 * </ul>
 */
@WebServlet(urlPatterns = {
    "/vuln/sqli-login",
    "/vuln/xss-echo",
    "/vuln/weak-session",
    "/vuln/weak-session-access",
    "/vuln/bruteforce",
    "/vuln/bruteforce-login",
    "/vuln/session-fixation"
})
public class VulnServlet extends HttpServlet {

  @Override
  protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    if ("/vuln/xss-echo".equals(path)) {
      req.getRequestDispatcher("/vuln/xss.jsp").forward(req, resp);
      return;
    }
    if ("/vuln/sqli-login".equals(path)) {
      req.getRequestDispatcher("/vuln/sqli.jsp").forward(req, resp);
      return;
    }
    if ("/vuln/weak-session".equals(path)) {
      // Deliberately weak cookie flags (Project 1 demo). Project 2 will fix this.
      resp.setHeader("Set-Cookie", "DEMOSESSION=demo123; Path=/");
      req.getRequestDispatcher("/vuln/weak-session.jsp").forward(req, resp);
      return;
    }
    if ("/vuln/bruteforce".equals(path)) {
      req.getRequestDispatcher("/vuln/bruteforce.jsp").forward(req, resp);
      return;
    }
    if ("/vuln/session-fixation".equals(path)) {
      req.getRequestDispatcher("/vuln/session-fixation.jsp").forward(req, resp);
      return;
    }
    if ("/vuln/weak-session-access".equals(path)) {
      // Vulnerable access: trusts a weak client-side cookie.
      req.getRequestDispatcher("/vuln/weak-access.jsp").forward(req, resp);
      return;
    }
    resp.sendError(404);
  }

  @Override
  protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
    String path = req.getServletPath();
    if ("/vuln/sqli-login".equals(path)) {
      String email = req.getParameter("email");
      String password = req.getParameter("password");

      // INTENTIONALLY VULNERABLE (SQL Injection) for Project 1 demo:
      String sql = "SELECT id,name,email FROM users WHERE email='" + email + "' AND password='" + password + "'";

      try (Connection c = Db.getConnection();
           Statement st = c.createStatement();
           ResultSet rs = st.executeQuery(sql)) {
        if (rs.next()) {
          resp.sendRedirect(req.getContextPath() + "/vuln/sqli.jsp?ok=1&name=" + urlEnc(rs.getString("name")));
        } else {
          resp.sendRedirect(req.getContextPath() + "/vuln/sqli.jsp?err=1");
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }
      return;
    }

    if ("/vuln/bruteforce-login".equals(path)) {
      String email = req.getParameter("email");
      String password = req.getParameter("password");

      HttpSession s = req.getSession(true);
      Integer attempts = (Integer) s.getAttribute("bf_attempts");
      if (attempts == null) attempts = 0;
      attempts = attempts + 1;
      s.setAttribute("bf_attempts", attempts);

      // Brute force demo: NO rate limiting / NO lockout (Project 1 behavior).
      try (Connection c = Db.getConnection();
           PreparedStatement ps = c.prepareStatement(
               "SELECT id,name,email FROM users WHERE email=? AND password=?"
           )) {
        ps.setString(1, email);
        ps.setString(2, password);
        try (ResultSet rs = ps.executeQuery()) {
          if (rs.next()) {
            s.setAttribute("bf_ok_name", rs.getString("name"));
            resp.sendRedirect(req.getContextPath() + "/vuln/bruteforce.jsp?ok=1");
          } else {
            resp.sendRedirect(req.getContextPath() + "/vuln/bruteforce.jsp?err=1");
          }
        }
      } catch (Exception e) {
        throw new ServletException(e);
      }
      return;
    }

    resp.sendError(404);
  }

  private static String urlEnc(String s) {
    try {
      return java.net.URLEncoder.encode(s, java.nio.charset.StandardCharsets.UTF_8);
    } catch (Exception e) {
      return "";
    }
  }
}

