<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Login - EHPT Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-3">Login</h1>

              <% String err = request.getParameter("err"); %>
              <% if (err != null) { %>
                <div class="alert alert-danger">Invalid credentials</div>
              <% } %>

              <form method="post" action="auth/login">
                <div class="mb-3">
                  <label class="form-label">Email</label>
                  <input class="form-control" type="email" name="email" required />
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input class="form-control" type="password" name="password" required />
                </div>
                <button class="btn btn-primary w-100" type="submit">Login</button>
              </form>

              <div class="d-flex justify-content-between mt-3">
                <a class="small" href="register.jsp">Create account</a>
                <a class="small" href="index.jsp">Home</a>
              </div>
            </div>
          </div>
          <p class="text-muted small mt-3 mb-0">
            Note: we will intentionally make login vulnerable in Project 1 (SQLi & brute force), then secure it in Project 2.
          </p>
        </div>
      </div>
    </div>
  </body>
</html>
