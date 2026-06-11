<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ehpt.foodorder.web.CartServlet.CartLine" %>
<%
  Integer userId = (Integer) session.getAttribute("userId");
  if (userId == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  List<CartLine> lines = (List<CartLine>) request.getAttribute("lines");
  Double total = (Double) request.getAttribute("total");
  if (total == null) total = 0.0;
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Cart - Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <nav class="navbar navbar-expand-lg bg-white border-bottom">
      <div class="container">
        <a class="navbar-brand" href="<%= request.getContextPath() %>/user/home.jsp">Food Ordering</a>
        <div class="ms-auto d-flex gap-2">
          <a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/user/menu">Menu</a>
          <a class="btn btn-sm btn-outline-dark" href="<%= request.getContextPath() %>/user/orders">Orders</a>
          <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/auth/logout">Logout</a>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <div class="d-flex align-items-end justify-content-between mb-3">
        <div>
          <h1 class="h4 mb-0">Your cart</h1>
          <div class="text-muted small">Update quantities or remove items</div>
        </div>
        <div class="text-end">
          <div class="text-muted small">Total</div>
          <div class="h5 mb-0">₹<%= String.format("%.2f", total) %></div>
        </div>
      </div>

      <% if (lines == null || lines.isEmpty()) { %>
        <div class="alert alert-info">Your cart is empty. <a href="<%= request.getContextPath() %>/user/menu">Browse menu</a></div>
      <% } else { %>
        <div class="card shadow-sm">
          <div class="table-responsive">
            <table class="table mb-0 align-middle">
              <thead class="table-light">
                <tr>
                  <th>Item</th>
                  <th style="width: 130px;">Price</th>
                  <th style="width: 220px;">Qty</th>
                  <th style="width: 130px;">Total</th>
                  <th style="width: 80px;"></th>
                </tr>
              </thead>
              <tbody>
                <% for (CartLine ln : lines) { %>
                  <tr>
                    <td class="fw-semibold">
                      <div class="d-flex align-items-center gap-2">
                        <% if (ln.imageUrl() != null && !ln.imageUrl().isBlank()) { %>
                          <img src="<%= ln.imageUrl() %>" style="width:44px;height:44px;object-fit:cover;border-radius:10px;" alt="img"/>
                        <% } %>
                        <%= ln.name() %>
                      </div>
                    </td>
                    <td>₹<%= String.format("%.2f", ln.price()) %></td>
                    <td>
                      <form class="d-flex gap-2" method="post" action="<%= request.getContextPath() %>/user/cart/update">
                        <input type="hidden" name="itemId" value="<%= ln.itemId() %>"/>
                        <input class="form-control" type="number" min="1" max="20" name="qty" value="<%= ln.qty() %>"/>
                        <button class="btn btn-outline-primary" type="submit">Update</button>
                      </form>
                    </td>
                    <td class="fw-semibold">₹<%= String.format("%.2f", ln.lineTotal()) %></td>
                    <td>
                      <form method="post" action="<%= request.getContextPath() %>/user/cart/remove">
                        <input type="hidden" name="itemId" value="<%= ln.itemId() %>"/>
                        <button class="btn btn-sm btn-outline-danger" type="submit">X</button>
                      </form>
                    </td>
                  </tr>
                <% } %>
              </tbody>
            </table>
          </div>
        </div>

        <div class="d-flex justify-content-end mt-3">
          <a class="btn btn-primary" href="<%= request.getContextPath() %>/user/checkout">Proceed to checkout</a>
        </div>
      <% } %>
    </main>
  </body>
</html>

