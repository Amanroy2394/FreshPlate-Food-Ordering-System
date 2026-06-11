<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  Integer userId = (Integer) session.getAttribute("userId");
  if (userId == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  String userName = (String) session.getAttribute("userName");
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>User Home - EHPT Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <nav class="navbar navbar-expand-lg bg-white border-bottom">
      <div class="container">
        <a class="navbar-brand" href="<%= request.getContextPath() %>/user/home.jsp">Food Ordering</a>
        <div class="ms-auto d-flex align-items-center gap-3">
          <span class="small text-muted">Hi, <%= userName %></span>
          <a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/user/menu">Menu</a>
          <a class="btn btn-sm btn-outline-secondary" href="<%= request.getContextPath() %>/user/cart">Cart</a>
          <a class="btn btn-sm btn-outline-dark" href="<%= request.getContextPath() %>/user/orders">Orders</a>
          <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/auth/logout">Logout</a>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <div class="card shadow-sm">
        <div class="card-body">
          <h1 class="h5 mb-2">Welcome, <%= userName %></h1>
          <p class="text-muted mb-3">Browse food items, add to cart, and place an order.</p>
          <div class="d-flex flex-wrap gap-2">
            <a class="btn btn-primary" href="<%= request.getContextPath() %>/user/menu">Order food</a>
            <a class="btn btn-outline-secondary" href="<%= request.getContextPath() %>/user/cart">View cart</a>
            <a class="btn btn-outline-dark" href="<%= request.getContextPath() %>/user/orders">My orders</a>
          </div>
        </div>
      </div>
    </main>
  </body>
</html>
