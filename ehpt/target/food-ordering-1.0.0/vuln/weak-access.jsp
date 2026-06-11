<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%
  String demoVal = null;
  Cookie[] cookies = request.getCookies();
  if (cookies != null) {
    for (Cookie c : cookies) {
      if ("DEMOSESSION".equals(c.getName())) {
        demoVal = c.getValue();
        break;
      }
    }
  }
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Weak Session Cookie Access (Project 1)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-2">Project 1 Demo: Session Hijacking (Cookie Trust)</h1>
              <p class="text-muted small mb-3">
                This simulates a vulnerable app that trusts a client-side cookie to grant access.
              </p>

              <% if ("demo123".equals(demoVal)) { %>
                <div class="alert alert-success">
                  Access GRANTED (vulnerable). Cookie <code>DEMOSESSION</code> = <b><%= demoVal %></b>
                </div>
                <div class="p-3 border rounded bg-white">
                  Secret action executed: <b>“Order History Data”</b> (demo content).
                </div>
              <% } else { %>
                <div class="alert alert-danger">
                  Access DENIED. Cookie missing or wrong. Your cookie value: <b><%= (demoVal == null ? "null" : demoVal) %></b>
                </div>
              <% } %>

              <div class="mt-3">
                <a class="btn btn-outline-danger btn-sm" href="<%= request.getContextPath() %>/vuln/weak-session">Re-set weak cookie</a>
                <a class="btn btn-outline-secondary btn-sm" href="<%= request.getContextPath() %>/index.jsp">Home</a>
              </div>

              <div class="mt-3 small text-muted">
                For screenshot: open DevTools -> Application -> Cookies -> set <code>DEMOSESSION=demo123</code> then refresh.
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>

