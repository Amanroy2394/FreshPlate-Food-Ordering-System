<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  Integer attempts = (Integer) session.getAttribute("bf_attempts");
  if (attempts == null) attempts = 0;
  String ok = request.getParameter("ok");
  String err = request.getParameter("err");
  String okName = (String) session.getAttribute("bf_ok_name");
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Vulnerable Brute Force (Project 1)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-2">Project 1 Demo: Brute Force Login (No Lockout)</h1>
              <p class="text-muted small mb-3">This page is intentionally vulnerable. It counts attempts but does not block them.</p>

              <div class="alert alert-info">
                Current attempts in this session: <b><%= attempts %></b>
              </div>

              <% if (ok != null) { %>
                <div class="alert alert-success">Success! Welcome, <b><%= okName == null ? "user" : okName %></b></div>
              <% } %>
              <% if (err != null) { %>
                <div class="alert alert-danger">Login failed. Try again (unlimited attempts).</div>
              <% } %>

              <form method="post" action="<%= request.getContextPath() %>/vuln/bruteforce-login">
                <div class="mb-3">
                  <label class="form-label">Email</label>
                  <input class="form-control" name="email" placeholder="test@example.com" required />
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input class="form-control" type="password" name="password" placeholder="try different passwords" required />
                </div>
                <button class="btn btn-danger w-100" type="submit">Login (Vulnerable)</button>
              </form>

              <div class="mt-3 small text-muted">
                Try multiple passwords and take screenshots showing unlimited attempts.
              </div>
              <div class="mt-3">
                <a class="small" href="<%= request.getContextPath() %>/index.jsp">Home</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>

