<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="java.util.List" %>
<%@ page import="com.ehpt.foodorder.web.MenuServlet.FoodItem" %>
<%
  Integer userId = (Integer) session.getAttribute("userId");
  if (userId == null) {
    response.sendRedirect(request.getContextPath() + "/login.jsp");
    return;
  }
  String q = (String) request.getAttribute("q");
  if (q == null) q = "";
  String category = (String) request.getAttribute("category");
  if (category == null) category = "";
  String cartErr = request.getParameter("err");
  List<FoodItem> items = (List<FoodItem>) request.getAttribute("items");
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Menu - Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <nav class="navbar navbar-expand-lg bg-white border-bottom">
      <div class="container">
        <a class="navbar-brand" href="<%= request.getContextPath() %>/user/home.jsp">Food Ordering</a>
        <div class="ms-auto d-flex gap-2">
          <a class="btn btn-sm btn-outline-secondary" href="<%= request.getContextPath() %>/user/cart">Cart</a>
          <a class="btn btn-sm btn-outline-dark" href="<%= request.getContextPath() %>/user/orders">Orders</a>
          <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/auth/logout">Logout</a>
        </div>
      </div>
    </nav>

    <main class="container py-4">
      <% if ("cart".equals(cartErr)) { %>
        <div class="alert alert-danger">Could not add item to cart. Please refresh and try again.</div>
      <% } %>
      <div class="d-flex flex-wrap align-items-center justify-content-between gap-2 mb-3">
        <div>
          <h1 class="h4 mb-0">Menu</h1>
          <div class="text-muted small">Search and add items to your cart</div>
        </div>
        <form class="d-flex flex-wrap gap-2" method="get" action="<%= request.getContextPath() %>/user/menu">
          <select class="form-select" name="category" style="min-width: 190px">
            <option value="" <%= category.isBlank() ? "selected" : "" %>>All categories</option>
            <option value="Pizza" <%= "Pizza".equals(category) ? "selected" : "" %>>Pizza</option>
            <option value="Wraps" <%= "Wraps".equals(category) ? "selected" : "" %>>Wraps</option>
            <option value="Rice" <%= "Rice".equals(category) ? "selected" : "" %>>Rice</option>
            <option value="Beverages" <%= "Beverages".equals(category) ? "selected" : "" %>>Beverages</option>
          </select>
          <input class="form-control" style="min-width: 260px" name="q" value="<%= q %>" placeholder="Search (pizza, biryani...)"/>
          <button class="btn btn-primary" type="submit">Filter</button>
        </form>
      </div>

      <div class="row g-3">
        <% if (items == null || items.isEmpty()) { %>
          <div class="col-12">
            <div class="alert alert-warning mb-0">No items found.</div>
          </div>
        <% } else { %>
          <% for (FoodItem it : items) { %>
            <div class="col-12 col-md-6 col-lg-4">
              <div class="card h-100 shadow-sm">
                <%
                  String img = it.imageUrl();
                  if (img == null || img.isBlank()) {
                    String cat = it.category() == null ? "" : it.category().trim().toLowerCase();
                    if ("pizza".equals(cat)) {
                      img = "https://images.unsplash.com/photo-1601924582975-7d84b3b3a1aa?auto=format&fit=crop&w=1200&q=60";
                    } else if ("fast food".equals(cat) || "snacks".equals(cat)) {
                      img = "https://images.unsplash.com/photo-1568901346375-23c9450c58cd?auto=format&fit=crop&w=1200&q=60";
                    } else if ("italian".equals(cat)) {
                      img = "https://images.unsplash.com/photo-1473093295043-cdd812d0e601?auto=format&fit=crop&w=1200&q=60";
                    } else if ("beverage".equals(cat) || "beverages".equals(cat)) {
                      img = "https://images.unsplash.com/photo-1517701604599-bb29b565090c?auto=format&fit=crop&w=1200&q=60";
                    } else if ("indian".equals(cat) || "rice".equals(cat)) {
                      img = "https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=1200&q=60";
                    } else {
                      img = "https://images.unsplash.com/photo-1498837167922-ddd27525d352?auto=format&fit=crop&w=1200&q=60";
                    }
                  }
                %>
                <img src="<%= img %>" class="card-img-top" style="height: 180px; object-fit: cover;" alt="food"/>
                <div class="card-body d-flex flex-column">
                  <div class="d-flex justify-content-between align-items-start gap-2">
                    <div>
                      <div class="fw-semibold d-flex align-items-center gap-2">
                        <span><%= it.name() %></span>
                        <% if (it.isPopular()) { %>
                          <span class="badge text-bg-warning">Popular</span>
                        <% } %>
                      </div>
                      <div class="text-muted small"><%= it.category() %></div>
                    </div>
                    <div class="text-end">
                      <% if (it.discountPercent() > 0) { %>
                        <div class="badge text-bg-success mb-1"><%= it.discountPercent() %>% OFF</div>
                      <% } %>
                      <div class="fw-semibold">₹<%= String.format("%.2f", it.price()) %></div>
                    </div>
                  </div>
                  <% if (it.description() != null && !it.description().isBlank()) { %>
                    <div class="text-muted small mt-2"><%= it.description() %></div>
                  <% } %>

                  <form class="mt-auto pt-3" method="post" action="<%= request.getContextPath() %>/user/cart/add">
                    <input type="hidden" name="itemId" value="<%= it.id() %>"/>
                    <div class="d-flex gap-2">
                      <input class="form-control" type="number" min="1" max="20" name="qty" value="1" />
                      <button class="btn btn-outline-primary w-100" type="submit">Add to cart</button>
                    </div>
                  </form>
                </div>
              </div>
            </div>
          <% } %>
        <% } %>
      </div>
    </main>
  </body>
</html>

