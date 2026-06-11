<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%
  String ctx = request.getContextPath();
  Integer userId = (Integer) session.getAttribute("userId");
%>
<!doctype html>
<html lang="en">
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>FreshPlate — Food delivered fast</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link href="https://fonts.googleapis.com/css2?family=DM+Sans:ital,opsz,wght@0,9..40,400;0,9..40,600;0,9..40,700;1,9..40,400&display=swap" rel="stylesheet">
    <style>
      :root {
        --fp-orange: #e85d04;
        --fp-orange-dark: #d00000;
        --fp-cream: #fff8f0;
        --fp-ink: #1a1a2e;
      }
      body { font-family: "DM Sans", system-ui, sans-serif; color: var(--fp-ink); background: var(--fp-cream); }
      .navbar-landing { background: rgba(255,255,255,.92); backdrop-filter: blur(8px); border-bottom: 1px solid rgba(0,0,0,.06); }
      .brand-mark { font-weight: 700; letter-spacing: -.02em; color: var(--fp-ink); }
      .brand-mark span { color: var(--fp-orange); }
      .hero {
        background: linear-gradient(135deg, #fff5eb 0%, #ffe8d6 45%, #ffd7a8 100%);
        border-radius: 1.25rem;
        padding: 2.5rem 2rem;
        position: relative;
        overflow: hidden;
      }
      @media (min-width: 768px) {
        .hero { padding: 3.5rem 3rem; }
      }
      .hero::after {
        content: "";
        position: absolute; right: -20%; top: -30%; width: 55%; height: 160%;
        background: radial-gradient(circle, rgba(232,93,4,.12) 0%, transparent 70%);
        pointer-events: none;
      }
      .hero h1 { font-weight: 700; letter-spacing: -.03em; line-height: 1.15; }
      .search-bar { box-shadow: 0 8px 30px rgba(232,93,4,.15); border: none; }
      .btn-brand { background: var(--fp-orange); border: none; color: #fff; font-weight: 600; }
      .btn-brand:hover { background: var(--fp-orange-dark); color: #fff; }
      .category-tile {
        border-radius: 1rem; background: #fff; border: 1px solid rgba(0,0,0,.06);
        transition: transform .15s ease, box-shadow .15s ease;
      }
      .category-tile:hover { transform: translateY(-3px); box-shadow: 0 12px 28px rgba(0,0,0,.08); }
      .dish-card { border-radius: 1rem; overflow: hidden; border: none; box-shadow: 0 4px 20px rgba(0,0,0,.07); }
      .dish-card img { height: 160px; object-fit: cover; }
      .price-tag { color: var(--fp-orange-dark); font-weight: 700; }
      .footer-landing { border-top: 1px solid rgba(0,0,0,.06); font-size: .875rem; color: #6c757d; }
    </style>
  </head>
  <body>
    <nav class="navbar navbar-expand-lg navbar-landing sticky-top">
      <div class="container py-2">
        <a class="navbar-brand brand-mark mb-0" href="<%= ctx %>/index.jsp">Fresh<span>Plate</span></a>
        <button class="navbar-toggler" type="button" data-bs-toggle="collapse" data-bs-target="#navMain" aria-controls="navMain" aria-expanded="false" aria-label="Toggle navigation">
          <span class="navbar-toggler-icon"></span>
        </button>
        <div class="collapse navbar-collapse" id="navMain">
          <ul class="navbar-nav ms-auto align-items-lg-center gap-lg-2">
            <li class="nav-item"><a class="nav-link" href="<%= ctx %>/index.jsp#categories">Categories</a></li>
            <li class="nav-item"><a class="nav-link" href="<%= ctx %>/index.jsp#popular">Popular</a></li>
            <% if (userId != null) { %>
              <li class="nav-item"><a class="btn btn-sm btn-outline-dark" href="<%= ctx %>/user/menu">Menu</a></li>
              <li class="nav-item"><a class="btn btn-sm btn-brand" href="<%= ctx %>/user/home.jsp">My account</a></li>
            <% } else { %>
              <li class="nav-item"><a class="btn btn-sm btn-outline-secondary" href="<%= ctx %>/login.jsp">Sign in</a></li>
              <li class="nav-item"><a class="btn btn-sm btn-brand px-3" href="<%= ctx %>/register.jsp">Sign up</a></li>
            <% } %>
          </ul>
        </div>
      </div>
    </nav>

    <main class="container pb-5">
      <section class="hero mb-5 mt-3 mt-md-4">
        <div class="row align-items-center g-4">
          <div class="col-lg-6 position-relative" style="z-index: 1">
            <p class="text-uppercase small fw-semibold text-secondary mb-2 tracking-wide">Hungry? We’ve got you.</p>
            <h1 class="display-5 mb-3">Cravings delivered in minutes — hot, fresh, and right to your door.</h1>
            <p class="lead text-secondary mb-4 fs-6">Browse pizza, wraps, biryani, drinks, and more. Sign in to search the full menu, build your cart, and track orders.</p>
            <form class="mb-3" action="<%= userId != null ? ctx + "/user/menu" : ctx + "/login.jsp" %>" method="get">
              <div class="input-group input-group-lg search-bar rounded-4 overflow-hidden bg-white p-1">
                <span class="input-group-text border-0 bg-white ps-3"><span class="text-muted">🍽</span></span>
                <input type="search" class="form-control border-0 shadow-none" name="q" placeholder="Search pizza, biryani, coffee…" aria-label="Search food" />
                <button class="btn btn-brand rounded-3 px-4" type="submit">Find food</button>
              </div>
              <% if (userId == null) { %>
                <p class="small text-muted mb-0 mt-2">You’ll sign in next to see live menu results.</p>
              <% } %>
            </form>
            <div class="d-flex flex-wrap gap-2 align-items-center">
              <% if (userId != null) { %>
                <a class="btn btn-outline-dark" href="<%= ctx %>/user/cart">View cart</a>
                <a class="btn btn-link text-decoration-none p-0" href="<%= ctx %>/user/orders">Your orders →</a>
              <% } else { %>
                <a class="btn btn-outline-dark" href="<%= ctx %>/register.jsp">Create free account</a>
                <span class="text-muted small">Already a member? <a href="<%= ctx %>/login.jsp">Sign in</a></span>
              <% } %>
            </div>
          </div>
          <div class="col-lg-6 position-relative" style="z-index: 1">
            <div class="ratio ratio-4x3 rounded-4 overflow-hidden shadow">
              <img src="https://images.unsplash.com/photo-1504674900247-0877df9cc836?auto=format&fit=crop&w=1200&q=70" alt="Chef preparing food" class="object-fit-cover" />
            </div>
          </div>
        </div>
      </section>

      <section id="categories" class="mb-5">
        <div class="d-flex justify-content-between align-items-end mb-3">
          <div>
            <h2 class="h4 fw-bold mb-1">Order by category</h2>
            <p class="text-muted small mb-0">Tap a cuisine to jump into the menu after sign-in.</p>
          </div>
        </div>
        <div class="row g-3">
          <div class="col-6 col-md-3">
            <a href="<%= userId != null ? ctx + "/user/menu?category=Pizza" : ctx + "/login.jsp" %>" class="text-decoration-none text-dark">
              <div class="category-tile p-3 h-100 text-center">
                <div class="fs-2 mb-2">🍕</div>
                <div class="fw-semibold">Pizza</div>
                <div class="small text-muted">Cheesy classics</div>
              </div>
            </a>
          </div>
          <div class="col-6 col-md-3">
            <a href="<%= userId != null ? ctx + "/user/menu?category=Wraps" : ctx + "/login.jsp" %>" class="text-decoration-none text-dark">
              <div class="category-tile p-3 h-100 text-center">
                <div class="fs-2 mb-2">🌯</div>
                <div class="fw-semibold">Wraps</div>
                <div class="small text-muted">Rolls &amp; fillings</div>
              </div>
            </a>
          </div>
          <div class="col-6 col-md-3">
            <a href="<%= userId != null ? ctx + "/user/menu?category=Rice" : ctx + "/login.jsp" %>" class="text-decoration-none text-dark">
              <div class="category-tile p-3 h-100 text-center">
                <div class="fs-2 mb-2">🍛</div>
                <div class="fw-semibold">Rice</div>
                <div class="small text-muted">Biryani &amp; more</div>
              </div>
            </a>
          </div>
          <div class="col-6 col-md-3">
            <a href="<%= userId != null ? ctx + "/user/menu?category=Beverages" : ctx + "/login.jsp" %>" class="text-decoration-none text-dark">
              <div class="category-tile p-3 h-100 text-center">
                <div class="fs-2 mb-2">☕</div>
                <div class="fw-semibold">Drinks</div>
                <div class="small text-muted">Coffee &amp; chill</div>
              </div>
            </a>
          </div>
        </div>
      </section>

      <section id="popular" class="mb-5">
        <h2 class="h4 fw-bold mb-1">Popular near you</h2>
        <p class="text-muted small mb-4">Sample dishes from our kitchen — same items you’ll see in the app after login.</p>
        <div class="row g-4">
          <div class="col-md-6 col-lg-3">
            <div class="card dish-card h-100">
              <img src="https://images.unsplash.com/photo-1601924582975-7d84b3b3a1aa?auto=format&fit=crop&w=800&q=60" class="card-img-top" alt="Pizza" />
              <div class="card-body">
                <span class="badge text-bg-warning text-dark mb-2">Popular</span>
                <h3 class="h6 fw-bold mb-1">Margherita Pizza</h3>
                <p class="small text-muted mb-2">Classic cheese pizza with fresh basil.</p>
                <div class="d-flex justify-content-between align-items-center">
                  <span class="price-tag">₹199 <span class="small text-success fw-normal">20% off</span></span>
                  <a class="btn btn-sm btn-outline-dark" href="<%= userId != null ? ctx + "/user/menu" : ctx + "/login.jsp" %>">Order</a>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-6 col-lg-3">
            <div class="card dish-card h-100">
              <img src="https://images.unsplash.com/photo-1529042410759-befb1204b468?auto=format&fit=crop&w=800&q=60" class="card-img-top" alt="Wrap" />
              <div class="card-body">
                <h3 class="h6 fw-bold mb-1">Paneer Tikka Wrap</h3>
                <p class="small text-muted mb-2">Spiced paneer with onions and mint chutney.</p>
                <div class="d-flex justify-content-between align-items-center">
                  <span class="price-tag">₹149</span>
                  <a class="btn btn-sm btn-outline-dark" href="<%= userId != null ? ctx + "/user/menu" : ctx + "/login.jsp" %>">Order</a>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-6 col-lg-3">
            <div class="card dish-card h-100">
              <img src="https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=800&q=60" class="card-img-top" alt="Biryani" />
              <div class="card-body">
                <span class="badge text-bg-warning text-dark mb-2">Popular</span>
                <h3 class="h6 fw-bold mb-1">Veg Biryani</h3>
                <p class="small text-muted mb-2">Aromatic rice with vegetables and spices.</p>
                <div class="d-flex justify-content-between align-items-center">
                  <span class="price-tag">₹179 <span class="small text-success fw-normal">10% off</span></span>
                  <a class="btn btn-sm btn-outline-dark" href="<%= userId != null ? ctx + "/user/menu" : ctx + "/login.jsp" %>">Order</a>
                </div>
              </div>
            </div>
          </div>
          <div class="col-md-6 col-lg-3">
            <div class="card dish-card h-100">
              <img src="https://images.unsplash.com/photo-1517701604599-bb29b565090c?auto=format&fit=crop&w=800&q=60" class="card-img-top" alt="Coffee" />
              <div class="card-body">
                <h3 class="h6 fw-bold mb-1">Cold Coffee</h3>
                <p class="small text-muted mb-2">Chilled coffee with milk and ice.</p>
                <div class="d-flex justify-content-between align-items-center">
                  <span class="price-tag">₹99</span>
                  <a class="btn btn-sm btn-outline-dark" href="<%= userId != null ? ctx + "/user/menu" : ctx + "/login.jsp" %>">Order</a>
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section class="rounded-4 bg-dark text-white p-4 p-md-5 mb-5">
        <div class="row align-items-center g-3">
          <div class="col-lg-8">
            <h2 class="h5 fw-bold mb-2">Running a kitchen?</h2>
            <p class="mb-0 opacity-75 small">Admins can manage items, prices, and availability from the back office.</p>
          </div>
          <div class="col-lg-4 text-lg-end">
            <a class="btn btn-light btn-sm fw-semibold" href="<%= ctx %>/admin/login.jsp">Restaurant admin</a>
          </div>
        </div>
      </section>
    </main>

    <footer class="footer-landing py-4 mt-auto">
      <div class="container d-flex flex-column flex-md-row justify-content-between align-items-center gap-2">
        <span>© <%= java.time.Year.now() %> FreshPlate — demo food delivery UI</span>
        <span class="d-flex flex-wrap gap-3 justify-content-center">
          <a class="link-secondary text-decoration-none" href="<%= ctx %>/login.jsp">Help / Sign in</a>
        </span>
      </div>
    </footer>

    <script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
  </body>
</html>
