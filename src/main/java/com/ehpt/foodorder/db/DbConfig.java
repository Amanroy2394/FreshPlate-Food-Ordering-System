package com.ehpt.foodorder.db;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Properties;

/** Loads JDBC settings: local/property files override environment variables. */
final class DbConfig {
  private static final String DEFAULT_URL =
      "jdbc:mysql://localhost:3306/ehpt_food?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";

  final String url;
  final String username;
  final String password;

  DbConfig() {
    Properties props = new Properties();
    try (InputStream in = DbConfig.class.getClassLoader().getResourceAsStream("db.properties")) {
      if (in != null) {
        props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
      }
    } catch (Exception ignored) {
      // optional classpath file
    }
    Path local = Path.of(System.getProperty("user.dir"), "db.local.properties");
    try {
      if (Files.isRegularFile(local)) {
        try (var in = Files.newInputStream(local)) {
          props.load(new InputStreamReader(in, StandardCharsets.UTF_8));
        }
      }
    } catch (Exception ignored) {
      // optional local override
    }

    // Prefer project config files so stale env vars do not break local runs.
    url = firstNonBlank(props.getProperty("jdbc.url"), System.getenv("EHPT_JDBC_URL"), DEFAULT_URL);
    username = firstNonBlank(props.getProperty("jdbc.username"), System.getenv("EHPT_DB_USER"), "root");
    password = firstNonBlank(props.getProperty("jdbc.password"), System.getenv("EHPT_DB_PASSWORD"), "root");
  }

  private static String firstNonBlank(String... values) {
    for (String v : values) {
      if (v != null && !v.isBlank()) {
        return v.trim();
      }
    }
    return "";
  }
}
