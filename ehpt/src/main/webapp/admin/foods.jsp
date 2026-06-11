<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ehpt.foodorder.web.AdminFoodServlet.FoodRow" %>
<%
  Integer adminId = (Integer) session.getAttribute("adminId");
  if (adminId == null) {
    response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
    return;
  }
  String adminUser = (String) session.getAttribute("adminUser");
  List<FoodRow> foods = (List<FoodRow>) request.getAttribute("foods");
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Manage Foods - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <nav class="navbar navbar-expand-lg bg-white border-bottom">
      <div class="container">
        <a class="navbar-brand" href="<%= request.getContextPath() %>/admin/foods">Admin Panel</a>
        <div class="ms-auto d-flex align-items-center gap-3">
          <span class="small text-muted">Admin: <%= adminUser %></span>
          <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/admin/logout">Logout</a>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <div class="d-flex flex-wrap justify-content-between align-items-end gap-2 mb-3">
        <div>
          <h1 class="h4 mb-0">Food items</h1>
          <div class="text-muted small">Add/edit menu like Swiggy/Zomato</div>
        </div>
        <a class="btn btn-dark" href="<%= request.getContextPath() %>/admin/foods/new">+ Add food item</a>
      </div>

      <% if (request.getParameter("ok") != null) { %>
        <div class="alert alert-success">Saved successfully.</div>
      <% } %>
      <% if (request.getParameter("del") != null) { %>
        <div class="alert alert-warning">Deleted successfully.</div>
      <% } %>

      <div class="card shadow-sm">
        <div class="table-responsive">
          <table class="table mb-0 align-middle">
            <thead class="table-light">
              <tr>
                <th>ID</th>
                <th>Item</th>
                <th>Category</th>
                <th>Price</th>
                <th>Offer</th>
                <th>Popular</th>
                <th>Available</th>
                <th style="width: 220px;"></th>
              </tr>
            </thead>
            <tbody>
              <% if (foods == null || foods.isEmpty()) { %>
                <tr><td colspan="8" class="text-center text-muted py-4">No food items found.</td></tr>
              <% } else { %>
                <% for (FoodRow f : foods) { %>
                  <tr>
                    <td class="text-muted">#<%= f.id() %></td>
                    <td class="fw-semibold"><%= f.name() %></td>
                    <td><span class="badge text-bg-light"><%= f.category() %></span></td>
                    <td>₹<%= String.format("%.2f", f.price()) %></td>
                    <td>
                      <% if (f.discountPercent() > 0) { %>
                        <span class="badge text-bg-success"><%= f.discountPercent() %>% OFF</span>
                      <% } else { %>
                        <span class="text-muted small">—</span>
                      <% } %>
                    </td>
                    <td>
                      <% if (f.isPopular()) { %>
                        <span class="badge text-bg-warning">Popular</span>
                      <% } else { %>
                        <span class="text-muted small">—</span>
                      <% } %>
                    </td>
                    <td>
                      <% if (f.isAvailable()) { %>
                        <span class="badge text-bg-primary">Yes</span>
                      <% } else { %>
                        <span class="badge text-bg-secondary">No</span>
                      <% } %>
                    </td>
                    <td class="text-end">
                      <a class="btn btn-sm btn-outline-primary" href="<%= request.getContextPath() %>/admin/foods/edit?id=<%= f.id() %>">Edit</a>
                      <form class="d-inline" method="post" action="<%= request.getContextPath() %>/admin/foods/delete" onsubmit="return confirm('Delete this item?');">
                        <input type="hidden" name="id" value="<%= f.id() %>"/>
                        <button class="btn btn-sm btn-outline-danger" type="submit">Delete</button>
                      </form>
                    </td>
                  </tr>
                <% } %>
              <% } %>
            </tbody>
          </table>
        </div>
      </div>
    </main>
  </body>
</html>

