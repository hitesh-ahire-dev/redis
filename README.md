# Spring Boot User CRUD with Redis Cache

This project is a production-ready Spring Boot application that performs User CRUD operations using a relational database (MySQL) and integrates Redis as a caching layer to improve read performance and reduce database load.

## 🚀 Features

* **User CRUD APIs** (Create, Read, Update, Delete)
* **Redis Integration**: High-performance data retrieval using in-memory caching to reduce database load.
* **Declarative Cacheable Service Layer**:
  * **Add to Cache (`@Cacheable`)**: When a user queries data for the first time, Spring fetches it from MySQL, stores it in the Redis cache, and sets a 10-minute countdown. Subsequent reads within those 10 minutes hit Redis directly.
  * **Update Cache (`@CachePut`)**: When a user is modified or created via the API, Spring saves the fresh data to MySQL and automatically overwrites the associated Redis cache entry with the new data.
  * **Delete Cache (`@CacheEvict`)**: When a user is deleted via the API, Spring removes the user from MySQL and automatically deletes the user's data from the Redis cache.
* **MySQL Database**: Long-term relational persistence using Spring Data JPA.
* **Clean Layered Architecture**: Clear separation of concerns (Controller → Service → Repository).

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

### Time To Live (TTL): 10 minutes
In `RedisConfig.java`, we've configured a TTL of 10 minutes for our cache entries.
- After a user is fetched from MySQL and stored in Redis, a 10-minute timer starts.
- If no one accesses or updates that user's data for 10 minutes, Redis automatically deletes it from memory.
- The next request for that user will be a Cache Miss, and Spring will fetch fresh data from MySQL and start a new 10-minute timer.
- **Why?** It guarantees the cache memory doesn't fill up with stale or unused data and ensures data naturally refreshes.

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
