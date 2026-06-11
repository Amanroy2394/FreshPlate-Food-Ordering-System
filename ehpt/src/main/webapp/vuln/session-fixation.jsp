<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="jakarta.servlet.http.Cookie" %>
<%
  String jsession = null;
  Cookie[] cookies = request.getCookies();
  if (cookies != null) {
    for (Cookie c : cookies) {
      if ("JSESSIONID".equals(c.getName())) {
        jsession = c.getValue();
        break;
      }
    }
  }
%>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Session fixation (Project 1)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-2">Project 1 Demo: Session handling (detection)</h1>
              <p class="text-muted small mb-3">
                Compare <b>before</b> and <b>after</b> login: the normal app does not invalidate the session or rotate the session id on successful login.
                That enables <strong>session fixation</strong> in a real attack chain (Project 2: <code>HttpServletRequest.changeSessionId()</code> or new session after auth).
              </p>

              <div class="alert alert-info small mb-3">
                Current <code>JSESSIONID</code> (first 24 chars): <code><%= jsession == null ? "(no cookie yet — refresh after visiting site)" : jsession.substring(0, Math.min(24, jsession.length())) + "…" %></code>
              </div>

              <ol class="small">
                <li>Open DevTools → Application → Cookies → note <code>JSESSIONID</code>.</li>
                <li>Log in via <a href="<%= request.getContextPath() %>/login.jsp">main login</a> with a test user.</li>
                <li>Compare: session id often stays the same if the server reuses the pre-auth session (typical servlet default).</li>
              </ol>

              <p class="small text-muted mb-0">
                Pair with <a href="<%= request.getContextPath() %>/vuln/weak-session">weak cookie demo</a> and <a href="<%= request.getContextPath() %>/vuln/weak-session-access">cookie trust demo</a> for session hijacking / insecure session storyboard.
              </p>

              <div class="mt-3">
                <a class="small" href="<%= request.getContextPath() %>/index.jsp">Home</a>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  </body>
</html>
