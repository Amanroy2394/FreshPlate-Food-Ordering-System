CREATE DATABASE IF NOT EXISTS ehpt_food;
USE ehpt_food;

CREATE TABLE IF NOT EXISTS users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(100) NOT NULL,
  email VARCHAR(150) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS food_items (
  id INT PRIMARY KEY AUTO_INCREMENT,
  name VARCHAR(120) NOT NULL,
  category VARCHAR(80) NOT NULL,
  description VARCHAR(500) NULL,
  price DECIMAL(10,2) NOT NULL,
  discount_percent INT NOT NULL DEFAULT 0,
  image_url VARCHAR(500) NULL,
  is_available TINYINT(1) NOT NULL DEFAULT 1,
  is_popular TINYINT(1) NOT NULL DEFAULT 0
);

CREATE TABLE IF NOT EXISTS admin_users (
  id INT PRIMARY KEY AUTO_INCREMENT,
  username VARCHAR(80) NOT NULL UNIQUE,
  password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS cart_items (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  item_id INT NOT NULL,
  qty INT NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  UNIQUE KEY uq_cart_user_item(user_id, item_id),
  CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  CONSTRAINT fk_cart_item FOREIGN KEY (item_id) REFERENCES food_items(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS orders (
  id INT PRIMARY KEY AUTO_INCREMENT,
  user_id INT NOT NULL,
  total DECIMAL(10,2) NOT NULL,
  status VARCHAR(30) NOT NULL DEFAULT 'PLACED',
  delivery_address VARCHAR(300) NOT NULL,
  created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
  CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS order_items (
  id INT PRIMARY KEY AUTO_INCREMENT,
  order_id INT NOT NULL,
  item_id INT NOT NULL,
  item_name VARCHAR(120) NOT NULL,
  item_price DECIMAL(10,2) NOT NULL,
  qty INT NOT NULL,
  line_total DECIMAL(10,2) NOT NULL,
  CONSTRAINT fk_order_items_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE,
  CONSTRAINT fk_order_items_item FOREIGN KEY (item_id) REFERENCES food_items(id) ON DELETE RESTRICT
);

-- Minimal seed user (change as needed)
INSERT INTO users(name,email,password)
VALUES ('Test User','test@example.com','test123')
ON DUPLICATE KEY UPDATE email=email;

-- Seed admin (change password later)
INSERT INTO admin_users(username,password)
VALUES ('admin','admin123')
ON DUPLICATE KEY UPDATE username=username;

-- Seed food items (columns must match: discount_percent and is_popular included)
INSERT INTO food_items(name, category, description, price, discount_percent, image_url, is_available, is_popular) VALUES
('Margherita Pizza', 'Pizza', 'Classic cheese pizza with fresh basil.', 199.00, 20, 'https://images.unsplash.com/photo-1601924582975-7d84b3b3a1aa?auto=format&fit=crop&w=1200&q=60', 1, 1),
('Paneer Tikka Wrap', 'Wraps', 'Spiced paneer with onions and mint chutney.', 149.00, 0, 'https://images.unsplash.com/photo-1529042410759-befb1204b468?auto=format&fit=crop&w=1200&q=60', 1, 0),
('Veg Biryani', 'Rice', 'Aromatic basmati rice with vegetables and spices.', 179.00, 10, 'https://images.unsplash.com/photo-1631452180519-c014fe946bc7?auto=format&fit=crop&w=1200&q=60', 1, 1),
('Cold Coffee', 'Beverages', 'Chilled coffee with milk and ice.', 99.00, 0, 'https://images.unsplash.com/photo-1517701604599-bb29b565090c?auto=format&fit=crop&w=1200&q=60', 1, 0)
ON DUPLICATE KEY UPDATE name=name;

