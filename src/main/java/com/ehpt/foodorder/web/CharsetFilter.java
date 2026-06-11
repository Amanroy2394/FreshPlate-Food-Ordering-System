package com.ehpt.foodorder.web;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public class CharsetFilter extends HttpFilter {

  @Override
  protected void doFilter(HttpServletRequest req, HttpServletResponse resp, FilterChain chain)
      throws IOException, ServletException {
    req.setCharacterEncoding(StandardCharsets.UTF_8.name());
    resp.setCharacterEncoding(StandardCharsets.UTF_8.name());
    chain.doFilter(req, resp);
  }
}
