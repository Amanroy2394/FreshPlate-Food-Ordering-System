# 🍽️ FreshPlate - Food Ordering System

A modern full-stack food ordering web application inspired by platforms like Zomato and Swiggy. FreshPlate allows users to browse food items, manage carts, place orders, and track order history, while administrators can manage the food catalog through a dedicated dashboard.

---

## 🚀 Project Overview

FreshPlate is a Java-based food ordering platform developed using JSP, Jakarta Servlets, JDBC, MySQL, and Maven.

The application follows the MVC architecture and includes both traditional JSP-based pages and REST APIs for future frontend/mobile integration.

This project demonstrates:

- Full Stack Web Development
- Java Backend Development
- REST API Development
- Database Design & Integration
- Session Management & Authentication
- Admin Dashboard Development
- MVC Architecture
- Maven Project Management

---

## ✨ Features

### 👤 User Features

- User Registration
- User Login & Logout
- Secure Session Management
- Browse Food Menu
- Search Food Items
- Filter Food by Category
- Add Items to Cart
- Update Cart Quantity
- Remove Items from Cart
- Checkout & Place Orders
- View Order History

### 🛠️ Admin Features

- Admin Login
- Dashboard Access
- Add New Food Items
- Edit Existing Food Items
- Delete Food Items
- Manage Food Catalog
- View Available Menu

### 🌐 REST API Features

#### Authentication APIs

```http
POST /api/auth/login
POST /api/auth/register
POST /api/auth/logout
```

#### Food APIs

```http
GET /api/foods
GET /api/foods/{id}
```

#### Cart APIs

```http
GET /api/cart
POST /api/cart/add
PUT /api/cart/update
DELETE /api/cart/remove/{id}
```

#### Order APIs

```http
GET /api/orders
POST /api/orders/place
```

#### Admin APIs

```http
GET /api/admin/foods
POST /api/admin/foods
PUT /api/admin/foods/{id}
DELETE /api/admin/foods/{id}
```

---

## 🏗️ System Architecture

```text
Frontend (JSP, HTML, CSS, JS)
            │
            ▼
Jakarta Servlets (Controller Layer)
            │
            ▼
DAO / JDBC Layer
            │
            ▼
MySQL Database
```

---

## 🧰 Technology Stack

### Backend

- Java 21
- Jakarta Servlet 6
- JSP
- JDBC
- Gson

### Frontend

- HTML5
- CSS3
- JavaScript
- JSP

### Database

- MySQL

### Build Tools

- Maven

### Server

- Jetty
- Apache Tomcat Compatible

### Version Control

- Git
- GitHub

---

## 📂 Project Structure

```text
FreshPlate-Food-Ordering-System
│
├── src
│   └── main
│       ├── java
│       │   └── com.ehpt.foodorder
│       │       ├── db
│       │       └── web
│       │           └── api
│       │
│       ├── resources
│       │
│       └── webapp
│           ├── admin
│           ├── user
│           ├── WEB-INF
│           ├── index.jsp
│           ├── login.jsp
│           └── register.jsp
│
├── database.sql
├── pom.xml
└── README.md
```

---

## 🗄️ Database Design

The application uses MySQL to store:

- Users
- Admins
- Food Items
- Cart Items
- Orders
- Order Details

Main tables include:

```sql
users
admins
food_items
cart_items
orders
order_items
```

---

## 🔒 Security Features

- Session-Based Authentication
- Role-Based Access (User/Admin)
- Prepared Statements (SQL Injection Prevention)
- Input Validation
- Protected Admin Routes
- Secure REST Endpoints

---

## ⚙️ Installation & Setup

### 1. Clone Repository

```bash
git clone https://github.com/Amanroy2394/FreshPlate-Food-Ordering-System.git
```

### 2. Move into Project

```bash
cd FreshPlate-Food-Ordering-System
```

### 3. Create Database

```sql
CREATE DATABASE freshplate;
```

### 4. Import SQL Script

Run:

```bash
database.sql
```

### 5. Configure Database

Update database configuration in:

```properties
db.properties
```

### 6. Build Project

```bash
mvn clean package
```

### 7. Run Application

```bash
mvn jetty:run
```

### 8. Open Browser

```text
http://localhost:8080
```

---

## 📸 Screenshots

### Landing Page

- Hero Section
- Food Search
- Categories
- Featured Food

### User Dashboard

- Menu Listing
- Cart
- Orders

### Admin Dashboard

- Food Management
- CRUD Operations
<img width="928" height="470" alt="{ACECBEC7-3801-4B25-AFF8-A91E35D33840}" src="https://github.com/user-attachments/assets/6aba222a-0ac5-4d43-bd09-f9c0294e4b2f" />
<img width="484" height="373" alt="{0D7CAF97-E61D-4678-B16E-12DB2A6544D7}" src="https://github.com/user-attachments/assets/d37954f8-4d87-4c76-a641-e94cf49714f9" />
<img width="936" height="442" alt="{438C1F0A-E58F-4744-A3CC-F67FF9BC8106}" src="https://github.com/user-attachments/assets/6abc2bd0-b3c5-4f6f-96ec-3be1813fbec1" />
<img width="947" height="438" alt="{DF076231-CB8A-4421-96E3-17650089DBB3}" src="https://github.com/user-attachments/assets/46853896-7d18-4375-b0b9-799edfbca77b" />
<img width="947" height="418" alt="{AF53FD42-AA42-4DAE-9ED4-3FD00AFB5374}" src="https://github.com/user-attachments/assets/4f077706-1328-47a8-9e9a-2560abfa6ee9" />
<img width="927" height="433" alt="{EB62E41A-E574-4A1A-8AAB-52A506D547EC}" src="https://github.com/user-attachments/assets/b26fa3b6-7456-437d-b062-d64aba518d2b" />
<img width="408" height="314" alt="{D14CA902-7CC0-4DFC-9603-76EFC6A1A43A}" src="https://github.com/user-attachments/assets/d4c885da-79ae-4052-8f6e-c04e5ee3be10" />
<img width="932" height="450" alt="{45D31EC1-2219-41A1-A5EB-7E93FBC00D93}" src="https://github.com/user-attachments/assets/9b6d425c-dcb9-4efd-9998-e7c91742cb62" />


## 🎯 Learning Outcomes

Through this project I gained hands-on experience in:

- Java Web Development
- Servlet & JSP Development
- REST API Design
- Session Management
- Database Connectivity using JDBC
- Maven Build Lifecycle
- Git & GitHub Workflow
- MVC Architecture
- Full Stack Development

---

## 👨‍💻 Author

### Aman Kumar Choudhary

B.Tech Computer Science Student

Aspiring Java Full Stack Developer

GitHub:
https://github.com/Amanroy2394
Live Url: https://freshplate-food-ordering-system-production.up.railway.app/

LinkedIn:
www.linkedin.com/in/aman-kumar-choudhary-37b9b22b0

---

## ⭐ Future Enhancements

- OTP Authentication
- Email Verification
- Razorpay Payment Gateway
- Order Tracking
- Delivery Partner Module
- JWT Authentication
- React Frontend
- Mobile App Integration
- Docker Deployment
- Cloud Hosting (AWS)

---

## 📜 License

This project is developed for educational and portfolio purposes.
