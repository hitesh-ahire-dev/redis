# Spring Boot User CRUD with Redis Cache

This project is a production-ready Spring Boot application that performs User CRUD operations using a relational database (MySQL) and integrates Redis as a caching layer to improve read performance and reduce database load.

## 🚀 Features

* User CRUD APIs (Create, Read, Update, Delete)
* Redis integration for caching user data
* Cacheable service layer using Spring Cache (`@Cacheable`, `@CachePut`, `@CacheEvict`)
* MySQL database with Spring Data JPA
* RESTful API design
* Clean layered architecture (Controller → Service → Repository)
* High-performance data retrieval using in-memory caching

## 🏗️ Tech Stack

* Java 17+
* Spring Boot
* Spring Data JPA
* Redis
* MySQL
* Maven
* Lombok

## ⚡ How It Works

1. Client calls User API (GET /api/users/{id})
2. Application first checks Redis cache
3. If cache hit → returns data from Redis (fast)
4. If cache miss → fetches from database and stores in Redis
5. Subsequent requests are served from cache, reducing DB load

## 🧠 Caching Strategy

* Cache Name: `users`
* Cache Key Format: `users::userId`
* Automatic cache update on update operations (`@CachePut`)
* Cache eviction on delete operations (`@CacheEvict`)
* Time To Live (TTL): 10 minutes

## 📦 APIs

* `POST /api/users` → Create User
* `GET /api/users/{id}` → Get User by ID (Cached)
* `GET /api/users` → Get All Users
* `PUT /api/users/{id}` → Update User (Cache Updated)
* `DELETE /api/users/{id}` → Delete User (Cache Evicted)

## 🔧 Setup Instructions

### 1. Mac Setup Requirements
* Homebrew (Mac package manager) installed.
* Java 17+ via SDKMAN or Homebrew.
* MySQL installed and running (`brew install mysql` then `brew services start mysql`).

### 2. Start Redis locally
If you do not have Redis installed on your Mac, you can install and run it easily through Homebrew:

```bash
# Install Redis via Homebrew
brew install redis

# Start Redis as a background service
brew services start redis
```
Redis will now be running on its default port, `6379`.

### 3. Database Configuration
Ensure your `application.properties` (or `application.yml`) is configured to point your MySQL server. This project's default configuration assumes a database named `radis` with a user of `root` and a password of `root1234`.

### 4. Run the Spring Boot application
You can start the Spring Boot application from your terminal via the Maven wrapper:

```bash
./mvnw spring-boot:run
```

### 5. Test APIs
Test the API endpoints via tools like `cURL` or Postman targeting `http://localhost:8080/api/users`.

## 🎯 Use Case

This project demonstrates how to use Redis as a caching layer in microservices or high-throughput systems to handle large-scale read operations efficiently (e.g., real-time systems, event processing pipelines, and enrichment services).

## 📈 Benefits of Using Redis

* Faster response time
* Reduced database load
* Better scalability
* Suitable for high traffic applications
