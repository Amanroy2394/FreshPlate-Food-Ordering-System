package com.ehpt.foodorder.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public final class Db {
  private Db() {}

  // Change these for your local MySQL.
  private static final String URL = "jdbc:mysql://localhost:3306/ehpt_food?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC";
  private static final String USER = "root";
  private static final String PASS = "root";

  public static Connection getConnection() throws SQLException {
    return DriverManager.getConnection(URL, USER, PASS);
  }
}
