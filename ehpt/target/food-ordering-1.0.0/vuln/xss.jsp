<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<!doctype html>
<html>
  <head>
    <meta charset="UTF-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1" />
    <title>Vulnerable XSS Echo (Project 1)</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
  </head>
  <body class="bg-light">
    <div class="container py-5">
      <div class="row justify-content-center">
        <div class="col-md-8 col-lg-7">
          <div class="card shadow-sm">
            <div class="card-body p-4">
              <h1 class="h5 mb-2">Project 1 Demo: Reflected XSS</h1>
              <p class="text-muted small mb-3">Intentionally vulnerable echo of query parameter.</p>

              <form class="d-flex gap-2" method="get" action="<%= request.getContextPath() %>/vuln/xss-echo">
                <input class="form-control" name="q" placeholder="Type anything..." />
                <button class="btn btn-danger" type="submit">Echo (Vulnerable)</button>
              </form>

              <hr class="my-4"/>

              <div class="small text-muted mb-1">Output (unsafe):</div>
              <div class="p-3 border rounded bg-white">
                <%= request.getParameter("q") %>
              </div>

              <div class="small mt-3">
                Test payload:
                <div class="mt-2 p-2 bg-light border rounded"><code>&lt;script&gt;alert('XSS')&lt;/script&gt;</code></div>
              </div>

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

