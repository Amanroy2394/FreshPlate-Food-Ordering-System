<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Admin Login - Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-6 col-lg-5">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-3">Admin Login</h1>
              <% if (request.getParameter("err") != null) { %>
                <div class="alert alert-danger">Invalid admin credentials</div>
              <% } %>
              <form method="post" action="<%= request.getContextPath() %>/admin/login">
                <div class="mb-3">
                  <label class="form-label">Username</label>
                  <input class="form-control" name="username" required />
                </div>
                <div class="mb-3">
                  <label class="form-label">Password</label>
                  <input class="form-control" type="password" name="password" required />
                </div>
                <button class="btn btn-dark w-100" type="submit">Login</button>
              </form>
              <div class="mt-3 d-flex justify-content-between">
                <a class="small" href="<%= request.getContextPath() %>/index.jsp">Home</a>
                <span class="small text-muted">Default: admin / admin123</span>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>

