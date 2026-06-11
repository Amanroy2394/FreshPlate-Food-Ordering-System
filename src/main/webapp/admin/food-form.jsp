<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="com.ehpt.foodorder.web.AdminFoodServlet.FoodRow" %>
<%
  Integer adminId = (Integer) session.getAttribute("adminId");
  if (adminId == null) {
    response.sendRedirect(request.getContextPath() + "/admin/login.jsp");
    return;
  }
  String mode = (String) request.getAttribute("mode");
  FoodRow food = (FoodRow) request.getAttribute("food");
  boolean isEdit = "edit".equals(mode) && food != null;
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title><%= isEdit ? "Edit" : "Add" %> Food - Admin</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <nav class="navbar navbar-expand-lg bg-white border-bottom">
      <div class="container">
        <a class="navbar-brand" href="<%= request.getContextPath() %>/admin/foods">Admin Panel</a>
        <div class="ms-auto d-flex gap-2">
          <a class="btn btn-sm btn-outline-secondary" href="<%= request.getContextPath() %>/admin/foods">Back</a>
          <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/admin/logout">Logout</a>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <div class="row justify-content-center">
        <div class="col-md-10 col-lg-8">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-3"><%= isEdit ? "Edit food item" : "Add new food item" %></h1>

              <form method="post" action="<%= request.getContextPath() %>/admin/foods/save">
                <% if (isEdit) { %>
                  <input type="hidden" name="id" value="<%= food.id() %>"/>
                <% } %>

                <div class="row g-3">
                  <div class="col-md-6">
                    <label class="form-label">Name</label>
                    <input class="form-control" name="name" required value="<%= isEdit ? food.name() : "" %>"/>
                  </div>
                  <div class="col-md-6">
                    <label class="form-label">Category</label>
                    <input class="form-control" name="category" required value="<%= isEdit ? food.category() : "" %>" placeholder="Pizza, Rice, Beverages"/>
                  </div>
                  <div class="col-12">
                    <label class="form-label">Description</label>
                    <textarea class="form-control" name="description" rows="3" placeholder="Short tasty description..."><%= isEdit ? (food.description() == null ? "" : food.description()) : "" %></textarea>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Price (₹)</label>
                    <input class="form-control" type="number" step="0.01" min="0" name="price" required value="<%= isEdit ? String.format("%.2f", food.price()) : "" %>"/>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Discount (%)</label>
                    <input class="form-control" type="number" min="0" max="90" name="discountPercent" value="<%= isEdit ? food.discountPercent() : 0 %>"/>
                  </div>
                  <div class="col-md-4">
                    <label class="form-label">Image URL</label>
                    <input class="form-control" name="imageUrl" value="<%= isEdit ? (food.imageUrl() == null ? "" : food.imageUrl()) : "" %>" placeholder="https://..."/>
                  </div>
                  <div class="col-12">
                    <div class="form-check form-switch">
                      <input class="form-check-input" type="checkbox" role="switch" id="isAvailable" name="isAvailable" <%= (!isEdit || food.isAvailable()) ? "checked" : "" %>>
                      <label class="form-check-label" for="isAvailable">Available</label>
                    </div>
                    <div class="form-check form-switch">
                      <input class="form-check-input" type="checkbox" role="switch" id="isPopular" name="isPopular" <%= (isEdit && food.isPopular()) ? "checked" : "" %>>
                      <label class="form-check-label" for="isPopular">Mark as Popular</label>
                    </div>
                  </div>
                </div>

                <div class="d-flex justify-content-end gap-2 mt-4">
                  <a class="btn btn-outline-secondary" href="<%= request.getContextPath() %>/admin/foods">Cancel</a>
                  <button class="btn btn-dark" type="submit">Save</button>
                </div>
              </form>
            </div>
          </div>
        </div>
      </div>
    </main>
  </body>
</html>

