<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Create account — FreshPlate</title>
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
              <h1 class="h4 fw-bold mb-1">Create your account</h1>
              <p class="text-muted small mb-4">Save addresses, reorder favourites, and track deliveries.</p>

              <% String ok = request.getParameter("ok"); String err = request.getParameter("err"); %>
              <% if (ok != null) { %>
                <div class="alert alert-success small">You’re all set. Sign in to start ordering.</div>
              <% } %>
              <% if ("db".equals(err)) { %>
                <div class="alert alert-danger small">
                  <strong>Database sign-in failed.</strong> MySQL rejected the username/password the app uses.
                  <strong>Fastest fix:</strong> in the project folder (same level as <code>pom.xml</code>), copy <code>db.local.properties.example</code> to <code>db.local.properties</code>,
                  set <code>jdbc.password</code> to your real MySQL password, save, restart the server (no full rebuild needed).
                  Or set <code>EHPT_DB_PASSWORD</code> before <code>mvn jetty:run</code>. Ensure MySQL is running and <code>database.sql</code> was imported.
                </div>
              <% } %>
              <% if ("email".equals(err)) { %>
                <div class="alert alert-warning small">That email is already registered. <a href="<%= request.getContextPath() %>/login.jsp">Sign in</a> instead.</div>
              <% } %>

              <form method="post" action="<%= request.getContextPath() %>/auth/register">
                <div class="mb-3">
                  <label class="form-label small fw-semibold">Full name</label>
                  <input class="form-control form-control-lg" type="text" name="name" placeholder="Your name" required autocomplete="name" />
                </div>
                <div class="mb-3">
                  <label class="form-label small fw-semibold">Email</label>
                  <input class="form-control form-control-lg" type="email" name="email" placeholder="you@example.com" required autocomplete="email" />
                </div>
                <div class="mb-4">
                  <label class="form-label small fw-semibold">Password</label>
                  <input class="form-control form-control-lg" type="password" name="password" placeholder="Choose a password" required autocomplete="new-password" />
                </div>
                <button class="btn btn-brand btn-lg w-100 rounded-3" type="submit">Sign up</button>
              </form>

              <div class="d-flex justify-content-between align-items-center mt-4 small">
                <a class="text-decoration-none" href="<%= request.getContextPath() %>/login.jsp">Already have an account?</a>
                <a class="text-muted text-decoration-none" href="<%= request.getContextPath() %>/index.jsp">← Home</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
