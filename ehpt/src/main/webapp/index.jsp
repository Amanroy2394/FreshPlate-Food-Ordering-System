<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>EHPT Food Ordering</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-7 col-lg-6">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h4 mb-2">EHPT Food Ordering</h1>
              <p class="text-muted mb-4">Start with authentication module.</p>
              <div class="d-flex flex-wrap gap-2">
                <a class="btn btn-primary" href="login.jsp">Login</a>
                <a class="btn btn-outline-primary" href="register.jsp">Register</a>
                <a class="btn btn-outline-secondary" href="user/home.jsp">User home</a>
              </div>
              <hr class="my-4"/>
              <h2 class="h6 text-danger mb-2">Project 1 — Attack detection (controlled labs)</h2>
              <p class="small text-muted mb-3">Use these for screenshots and reports. Project 2 will add prevention on the same flows.</p>
              <ul class="list-group list-group-flush small mb-0">
                <li class="list-group-item d-flex justify-content-between align-items-center px-0 bg-transparent">
                  <span><strong>1. SQL injection</strong> — string-built query in <code>VulnServlet</code></span>
                  <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/vuln/sqli-login">Open lab</a>
                </li>
                <li class="list-group-item d-flex justify-content-between align-items-center px-0 bg-transparent">
                  <span><strong>2. Reflected XSS</strong> — unescaped <code>request.getParameter(&quot;input&quot;)</code> in <code>xss.jsp</code></span>
                  <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/vuln/xss-echo">Open lab</a>
                </li>
                <li class="list-group-item d-flex justify-content-between align-items-center px-0 bg-transparent">
                  <span><strong>3. Brute force</strong> — no lockout / unlimited tries (<code>/vuln/bruteforce-login</code>)</span>
                  <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/vuln/bruteforce">Open lab</a>
                </li>
                <li class="list-group-item d-flex justify-content-between align-items-center px-0 bg-transparent">
                  <span><strong>4. Session issues</strong> — weak cookie + cookie trust + fixation notes</span>
                  <span class="d-flex flex-wrap gap-1">
                    <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/vuln/weak-session">Cookie flags</a>
                    <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/vuln/weak-session-access">Hijack demo</a>
                    <a class="btn btn-sm btn-outline-danger" href="<%= request.getContextPath() %>/vuln/session-fixation">Session ID</a>
                  </span>
                </li>
              </ul>
              <p class="small text-muted mt-3 mb-0">
                Main app login (<code>login.jsp</code>) also has no rate limiting — compare with Project 2 hardening.
              </p>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
