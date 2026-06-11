<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Sign in — FreshPlate</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:wght@400;600;700&display=swap" rel="stylesheet">
    <style>
      body { font-family: "DM Sans", system-ui, sans-serif; background: #fff8f0; color: #1a1a2e; }
      .btn-brand { background: #e85d04; border: none; color: #fff; font-weight: 600; }
      .btn-brand:hover { background: #d00000; color: #fff; }
      .brand-mark { font-weight: 700; }
      .brand-mark span { color: #e85d04; }
    </style>
  </head>
  <body>
    <div class="container py-5">
      <div class="text-center mb-4">
        <a class="text-decoration-none brand-mark text-dark" href="<%= request.getContextPath() %>/index.jsp">Fresh<span>Plate</span></a>
      </div>
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow-sm border-0">
            <div class="card-body p-4 p-md-5">
              <h1 class="h4 fw-bold mb-1">Welcome back</h1>
              <p class="text-muted small mb-4">Sign in to order, pay, and track delivery.</p>

              <% String err = request.getParameter("err"); %>
              <% if ("db".equals(err)) { %>
                <div class="alert alert-danger small">
                  <strong>Cannot reach database.</strong> Create <code>db.local.properties</code> next to <code>pom.xml</code> (see <code>db.local.properties.example</code>), set <code>jdbc.password</code>, restart. Or set <code>EHPT_DB_PASSWORD</code> before starting the server.
                </div>
              <% } else if ("sqli".equals(err)) { %>
                <div class="alert alert-warning small">
                  <strong>Suspicious input blocked.</strong> This app rejects inputs that resemble SQL injection attempts.
                </div>
              <% } else if (err != null) { %>
                <div class="alert alert-danger small">We couldn’t sign you in. Check your email and password.</div>
              <% } %>

              <form method="post" action="<%= request.getContextPath() %>/auth/login">
                <div class="mb-3">
                  <label class="form-label small fw-semibold">Email</label>
                  <input class="form-control form-control-lg" type="text" name="email" placeholder="you@example.com" required autocomplete="username" />
                </div>
                <div class="mb-4">
                  <label class="form-label small fw-semibold">Password</label>
                  <input class="form-control form-control-lg" type="password" name="password" placeholder="••••••••" required autocomplete="current-password" />
                </div>
                <button class="btn btn-brand btn-lg w-100 rounded-3" type="submit">Sign in</button>
              </form>

              <div class="d-flex justify-content-between align-items-center mt-4 small">
                <a class="text-decoration-none" href="<%= request.getContextPath() %>/register.jsp">Create an account</a>
                <a class="text-muted text-decoration-none" href="<%= request.getContextPath() %>/index.jsp">← Home</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
