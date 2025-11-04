# 📚 Library Management System

A simple **Library Management System** built with **Spring Boot 3**, **Spring Security 6**, and **JWT authentication**.  
It allows admins and librarians to manage **books**, **members**, and **system users** securely.

![CI](https://github.com/sohamohsen/Library-Management-System/actions/workflows/ci.yml/badge.svg)

---

## ✨ Features
- 🔐 **JWT auth** with role-based access: `ADMIN`, `LIBRARIAN`, `STAFF`
- 👥 Manage **members**, 📖 **books**, 🗂 **categories**
- 📊 Track copy status: `AVAILABLE`, `LOANED`, `LOST`, `DAMAGED`
- 📝 Auditing fields: `createdAt`, `updatedAt`, `createdBy`
- 🌐 **Swagger UI** for easy API testing

---

## 🧱 Tech Stack
- **Java 17+**, **Spring Boot 3.5.x**
- **Spring Security 6**, **Spring Data JPA**
- **MySQL 8+**
- **JWT (jjwt)**, **Lombok**
- **Swagger / springdoc-openapi**
- Build tool: **Maven**

---

## ⚡ Quick Start

### 1) Prerequisites
- JDK 17+
- Maven 3.9+
- MySQL 8+ (or Docker)

### 2) Configure environment
Create `src/main/resources/application.yml` (or use `application.properties`) with your DB creds:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/library_db?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
    username: root
    password: root
    driver-class-name: com.mysql.cj.jdbc.Driver

  jpa:
    hibernate:
      ddl-auto: update   # for dev only
    show-sql: true
    properties:
      hibernate:
        format_sql: true
        dialect: org.hibernate.dialect.MySQL8Dialect

server:
  port: 8080
