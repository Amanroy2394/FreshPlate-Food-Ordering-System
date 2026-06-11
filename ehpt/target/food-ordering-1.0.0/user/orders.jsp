<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ehpt.foodorder.web.OrderServlet.OrderRow" %>
<%
  Integer userId = (Integer) session.getAttribute("userId");
  if (userId == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  List<OrderRow> orders = (List<OrderRow>) request.getAttribute("orders");
  String ok = request.getParameter("ok");
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>My Orders - Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <nav class="navbar navbar-expand-lg bg-white border-bottom">
      <div class="container">
        <a class="navbar-brand" href="<%= request.getContextPath() %>/user/home.jsp">Food Ordering</a>
        <div class="ms-auto d-flex gap-2">
          <a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/user/menu">Menu</a>
          <a class="btn btn-sm btn-outline-secondary" href="<%= request.getContextPath() %>/user/cart">Cart</a>
          <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/auth/logout">Logout</a>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <div class="d-flex align-items-end justify-content-between mb-3">
        <div>
          <h1 class="h4 mb-0">My orders</h1>
          <div class="text-muted small">Order history</div>
        </div>
      </div>

      <% if (ok != null) { %>
        <div class="alert alert-success">Order placed successfully.</div>
      <% } %>

      <% if (orders == null || orders.isEmpty()) { %>
        <div class="alert alert-info">No orders yet. <a href="<%= request.getContextPath() %>/user/menu">Order now</a></div>
      <% } else { %>
        <div class="card shadow-sm">
          <div class="table-responsive">
            <table class="table mb-0 align-middle">
              <thead class="table-light">
                <tr>
                  <th>Order ID</th>
                  <th>Date</th>
                  <th>Status</th>
                  <th>Total</th>
                  <th>Address</th>
                </tr>
              </thead>
              <tbody>
                <% for (OrderRow o : orders) { %>
                  <tr>
                    <td class="fw-semibold">#<%= o.id() %></td>
                    <td><%= o.createdAt() %></td>
                    <td><span class="badge text-bg-success"><%= o.status() %></span></td>
                    <td class="fw-semibold">₹<%= String.format("%.2f", o.total()) %></td>
                    <td class="text-muted small"><%= o.address() %></td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          </div>
        </div>
      <% } %>
    </main>
  </body>
</html>

