<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Vulnerable SQLi Login (Project 1)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-2">Project 1 Demo: SQL Injection Login</h1>
              <p class="text-muted small mb-3">This page is intentionally vulnerable. Use only for EHPT demo.</p>

              <% if (request.getParameter("ok") != null) { %>
                <div class="alert alert-success">Login bypassed. Welcome, <%= request.getParameter("name") %></div>
              <% } %>
              <% if (request.getParameter("err") != null) { %>
                <div class="alert alert-danger">Login failed.</div>
              <% } %>

              <form method="post" action="<%= request.getContextPath() %>/vuln/sqli-login">
                <div class="mb-3">
                  <label class="form-label">Email</label>
                  <input class="form-control" name="email" placeholder="test@example.com" required />
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input class="form-control" name="password" placeholder="test123" required />
                </div>
                <button class="btn btn-danger w-100" type="submit">Login (Vulnerable)</button>
              </form>

              <hr class="my-4" />
              <div class="small">
                Try payload in email or password:
                <div class="mt-2 p-2 bg-light border rounded">
                  <code>email: ' OR 1=1 -- </code>
                </div>
                <div class="mt-2 p-2 bg-light border rounded">
                  <code>password: anything</code>
                </div>
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

