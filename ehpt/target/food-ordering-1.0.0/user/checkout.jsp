<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  Integer userId = (Integer) session.getAttribute("userId");
  if (userId == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  String err = request.getParameter("err");
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Checkout - Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <nav class="navbar navbar-expand-lg bg-white border-bottom">
      <div class="container">
        <a class="navbar-brand" href="<%= request.getContextPath() %>/user/home.jsp">Food Ordering</a>
        <div class="ms-auto d-flex gap-2">
          <a class="btn btn-sm btn-outline-secondary" href="<%= request.getContextPath() %>/user/cart">Cart</a>
          <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/auth/logout">Logout</a>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-3">Delivery details</h1>
              <% if ("addr".equals(err)) { %>
                <div class="alert alert-danger">Please enter delivery address.</div>
              <% } %>
              <form method="post" action="<%= request.getContextPath() %>/user/checkout">
                <div class="mb-3">
                  <label class="form-label">Delivery address</label>
                  <textarea class="form-control" name="address" rows="3" placeholder="House no, area, landmark, city" required></textarea>
                </div>
                <button class="btn btn-primary w-100" type="submit">Place order</button>
              </form>
              <div class="mt-3">
                <a class="small" href="<%= request.getContextPath() %>/user/cart">Back to cart</a>
              </div>
            </div>
          </div>
          <p class="text-muted small mt-3 mb-0">Payment is simulated (no real gateway) to keep the project simple.</p>
        </div>
      </div>
    </main>
  </body>
</html>

