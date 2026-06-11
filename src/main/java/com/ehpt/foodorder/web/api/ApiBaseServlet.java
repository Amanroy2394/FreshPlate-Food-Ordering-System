package com.ehpt.foodorder.web.api;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public abstract class ApiBaseServlet extends HttpServlet {

  protected final Gson gson = new GsonBuilder().serializeNulls().create();

  protected void sendJson(HttpServletResponse resp, Object body) throws IOException {
    resp.setStatus(HttpServletResponse.SC_OK);
    writeJson(resp, body);
  }

  protected void sendJsonError(HttpServletResponse resp, int status, String message) throws IOException {
    resp.setStatus(status);
    writeJson(resp, Map.of("ok", false, "error", message));
  }

  protected void writeJson(HttpServletResponse resp, Object body) throws IOException {
    resp.setContentType("application/json;charset=UTF-8");
    resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    resp.getWriter().write(gson.toJson(body));
  }

  protected Map<String, Object> parseJsonBody(HttpServletRequest req) throws IOException {
    String body = req.getReader().lines().collect(Collectors.joining());
    if (body == null || body.isBlank()) {
      return Collections.emptyMap();
    }
    try {
      Map<?, ?> parsed = gson.fromJson(body, Map.class);
      if (parsed == null) {
        return Collections.emptyMap();
      }
      Map<String, Object> result = new HashMap<>();
      for (Map.Entry<?, ?> entry : parsed.entrySet()) {
        if (entry.getKey() != null) {
          result.put(entry.getKey().toString(), entry.getValue());
        }
      }
      return result;
    } catch (Exception e) {
      return Collections.emptyMap();
    }
  }

  protected String textParam(HttpServletRequest req, Map<String, Object> body, String name) {
    String value = req.getParameter(name);
    if (value != null) {
      return value;
    }
    Object raw = body.get(name);
    return raw == null ? null : raw.toString();
  }

  protected int intParam(HttpServletRequest req, Map<String, Object> body, String name, int defaultValue) {
    String raw = textParam(req, body, name);
    if (raw == null || raw.isBlank()) {
      return defaultValue;
    }
    try {
      return Integer.parseInt(raw);
    } catch (NumberFormatException e) {
      return defaultValue;
    }
  }

  protected Integer getUserId(HttpServletRequest req) {
    var session = req.getSession(false);
    if (session == null) {
      return null;
    }
    Object userId = session.getAttribute("userId");
    return userId instanceof Integer ? (Integer) userId : null;
  }

  protected Integer getAdminId(HttpServletRequest req) {
    var session = req.getSession(false);
    if (session == null) {
      return null;
    }
    Object adminId = session.getAttribute("adminId");
    return adminId instanceof Integer ? (Integer) adminId : null;
  }

  protected String idPathSegment(HttpServletRequest req) {
    String pathInfo = req.getPathInfo();
    if (pathInfo == null || pathInfo.isBlank()) {
      return null;
    }
    if (pathInfo.startsWith("/")) {
      pathInfo = pathInfo.substring(1);
    }
    if (pathInfo.isBlank()) {
      return null;
    }
    return pathInfo;
  }
}
