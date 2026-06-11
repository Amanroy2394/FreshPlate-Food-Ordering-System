<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.Arrays" %>
<%
  jakarta.servlet.http.Cookie[] cookies = request.getCookies();
  String demoVal = null;
  if (cookies != null) {
    for (jakarta.servlet.http.Cookie c : cookies) {
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
    <title>Vulnerable Session Cookie (Project 1)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-2">Project 1 Demo: Weak Session Handling</h1>
              <p class="text-muted small mb-3">
                This demo sets a session-like cookie without secure flags. In Project 2, you will fix this.
              </p>

              <div class="alert alert-warning">
                Navigate to <b>/vuln/weak-session</b> and inspect cookies in DevTools.
                The cookie value is intentionally hardcoded as <code>demo123</code>.
              </div>

              <% if (demoVal != null) { %>
                <div class="alert alert-info">
                  You currently have cookie <code>DEMOSESSION</code> with value: <b><%= demoVal %></b>
                </div>
              <% } else { %>
                <div class="alert alert-secondary">
                  Cookie <code>DEMOSESSION</code> is not present yet (open this page by visiting the endpoint).
                </div>
              <% } %>

              <div class="mt-3">
                <div class="small text-muted mb-2">Why this is vulnerable:</div>
                <ul class="mb-0">
                  <li>Missing <code>HttpOnly</code> means JavaScript may access the cookie.</li>
                  <li>Missing <code>Secure</code> means it could be sent over non-HTTPS.</li>
                </ul>
              </div>

              <div class="mt-3">
                <a class="btn btn-outline-danger btn-sm" href="<%= request.getContextPath() %>/vuln/weak-session">Re-set weak cookie</a>
                <a class="small ms-2" href="<%= request.getContextPath() %>/index.jsp">Home</a>
              </div>
            </div>
          </div>
          <p class="text-muted small mt-3 mb-0">
            Screenshot tip: DevTools -> Application -> Cookies -> show <code>DEMOSESSION</code>.
          </p>
        </div>
      </div>
    </div>
  </body>
</html>

