# 📚 Library Management System

A simple **Library Management System** built with **Spring Boot 3**, **Spring Security 6**, and **JWT authentication**.  
It allows admins and librarians to manage **books**, **members**, and **system users** securely.

---

## ✨ Features
- 🔐 **JWT authentication** and role-based access (`ADMIN`, `LIBRARIAN`, `STAFF`)
- 👥 Manage **members**, 📖 **books**, and 🗂 **categories**
- 📊 Track book copy statuses: `AVAILABLE`, `LOANED`, `LOST`, `DAMAGED`
- 📝 Audit fields: `createdAt`, `updatedAt`, `createdBy`
- 🌐 Built-in **Swagger UI** for easy API testing

---

## ⚙️ Tech Stack
- **Java 17+**
- **Spring Boot 3.5.5**
- **Spring Security 6**
- **Spring Data JPA**
- **MySQL 8+**
- **JWT (jjwt)**
- **Lombok**
- **Swagger UI**

---

## 🗄 Database Setup
Run these SQL commands to prepare the initial data:

```sql
CREATE DATABASE library_db;

INSERT INTO roles(role) VALUES ('ADMIN'), ('LIBRARIAN'), ('STAFF');

INSERT INTO system_users(username, email, full_name, password, enabled, create_at, update_at, create_by, role_id)
VALUES ('admin', 'admin@example.com', 'Admin User',
        '$2a$10$7EqJtq98hPqEX7fNZaFWo./Xq5Q6DOW3EtFPXK62o/4pG7Wu3.yGa', -- password = "password"
        true, NOW(), NOW(), 1, (SELECT id FROM roles WHERE role = 'ADMIN'));
