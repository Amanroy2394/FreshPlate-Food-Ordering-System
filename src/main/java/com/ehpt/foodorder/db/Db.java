package com.ehpt.foodorder.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Db {
  private static final DbConfig CFG = new DbConfig();
  private static volatile boolean schemaReady = false;

  private Db() {}

  public static Connection getConnection() throws SQLException {
    Connection c = DriverManager.getConnection(CFG.url, CFG.username, CFG.password);
    ensureSchema(c);
    return c;
  }

  private static void ensureSchema(Connection c) throws SQLException {
    if (schemaReady) return;
    synchronized (Db.class) {
      if (schemaReady) return;
      try (Statement st = c.createStatement()) {
        st.executeUpdate(
            "CREATE TABLE IF NOT EXISTS users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(100) NOT NULL," +
                "email VARCHAR(150) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL" +
            ")"
        );
        st.executeUpdate(
            "CREATE TABLE IF NOT EXISTS admin_users (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "username VARCHAR(80) NOT NULL UNIQUE," +
                "password VARCHAR(255) NOT NULL" +
            ")"
        );
        st.executeUpdate(
            "CREATE TABLE IF NOT EXISTS food_items (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "name VARCHAR(120) NOT NULL," +
                "category VARCHAR(80) NOT NULL," +
                "description VARCHAR(500) NULL," +
                "price DECIMAL(10,2) NOT NULL," +
                "discount_percent INT NOT NULL DEFAULT 0," +
                "image_url VARCHAR(500) NULL," +
                "is_available TINYINT(1) NOT NULL DEFAULT 1," +
                "is_popular TINYINT(1) NOT NULL DEFAULT 0" +
            ")"
        );
        st.executeUpdate(
            "CREATE TABLE IF NOT EXISTS cart_items (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_id INT NOT NULL," +
                "item_id INT NOT NULL," +
                "qty INT NOT NULL," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "UNIQUE KEY uq_cart_user_item(user_id, item_id)," +
                "CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE," +
                "CONSTRAINT fk_cart_item FOREIGN KEY (item_id) REFERENCES food_items(id) ON DELETE CASCADE" +
            ")"
        );
        st.executeUpdate(
            "CREATE TABLE IF NOT EXISTS orders (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "user_id INT NOT NULL," +
                "total DECIMAL(10,2) NOT NULL," +
                "status VARCHAR(30) NOT NULL DEFAULT 'PLACED'," +
                "delivery_address VARCHAR(300) NOT NULL," +
                "created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP," +
                "CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE" +
            ")"
        );
        st.executeUpdate(
            "CREATE TABLE IF NOT EXISTS order_items (" +
                "id INT PRIMARY KEY AUTO_INCREMENT," +
                "order_id INT NOT NULL," +
                "item_id INT NOT NULL," +
                "item_name VARCHAR(120) NOT NULL," +
                "item_price DECIMAL(10,2) NOT NULL," +
                "qty INT NOT NULL," +
                "line_total DECIMAL(10,2) NOT NULL," +
                "CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE," +
                "CONSTRAINT fk_order_items_item FOREIGN KEY (item_id) REFERENCES food_items(id) ON DELETE RESTRICT" +
            ")"
        );
      }
      schemaReady = true;
    }
  }
}
