      
# 🛍️ E-commerce Microservices Platform

> A comprehensive Spring Boot microservices example demonstrating a modern e-commerce backend. It showcases polyglot persistence (MongoDB for products, MySQL for orders/inventory), API Gateway with routing, circuit breaking, and aggregated OpenAPI documentation, along with robust testing strategies using Testcontainers.


## 📖 Table of Contents

*   [🔍 Project Overview](#-project-overview)
*   [🧩 Architecture & Communication Flow](#-architecture--communication-flow)
*   [🧱 Services & Key Technologies](#-services--key-technologies)
    *   [Product Service](#product-service)
    *   [Order Service](#order-service)
    *   [Inventory Service](#inventory-service)
    *   [API Gateway](#api-gateway)
*   [✨ Key Concepts & Features Demonstrated](#-key-concepts--features-demonstrated)
*   [🚀 Getting Started](#-getting-started)
    *   [Prerequisites](#prerequisites)
    *   [Building the Services](#building-the-services)
    *   [Running with Docker Compose (Recommended)](#running-with-docker-compose-recommended)
    *   [Running Services Individually](#running-services-individually)
*   [🛠 API Endpoints](#-api-endpoints)
*   [✅ Testing](#-testing)


---

## 🔍 Project Overview

This project implements a simplified e-commerce backend system using a microservices architecture. It aims to demonstrate how different services can work together, each focusing on a specific business domain (Products, Orders, Inventory), utilizing diverse technologies (e.g., different databases) and communicating effectively. A central API Gateway handles request routing, security, and resiliency.

## 🧩 Architecture & Communication Flow

The system is composed of several independent microservices, coordinated by an API Gateway.

1.  **Client ➜ API Gateway**: All external requests from clients first hit the API Gateway. The Gateway acts as a single entry point, responsible for routing requests to the appropriate downstream service. It also handles cross-cutting concerns like security (OAuth2 Resource Server for JWT validation) and circuit breaking.
2.  **API Gateway ➜ Microservices**:
    *   The Gateway intelligently routes requests to the `Product Service`, `Order Service`, and `Inventory Service` based on the request path.
    *   It implements **Circuit Breaker patterns (Resilience4j)** to gracefully handle service failures and prevent cascading failures.
    *   For documentation, it aggregates OpenAPI (Swagger) definitions from individual services.
3.  **Microservice Interaction**: While the provided controllers primarily show external APIs, in a real-world scenario, services might communicate with each other (e.g., Order Service checking Inventory Service before placing an order). This project primarily uses synchronous REST communication.
4.  **Persistence**: Each service manages its own data store, demonstrating polyglot persistence. Product Service uses MongoDB, while Order and Inventory Services use MySQL. Database schema migrations for MySQL services are handled by Flyway.

---

## 🧱 Services & Key Technologies

### Product Service

*   **Purpose:** Manages the catalog of products, including their creation and retrieval.
*   **Technologies:**
    *   Spring Boot, Spring Web (RESTful API)
    *   Spring Data MongoDB (for NoSQL product data storage)
    *   Lombok (boilerplate code reduction)
    *   SpringDoc OpenAPI (API documentation)
    *   Testcontainers (for integration testing with MongoDB)
    *   Rest-Assured (for API testing)
*   **Endpoints:**
    *   `POST /api/product`: Creates a new product.
    *   `GET /api/product`: Retrieves a list of all products.

### Order Service

*   **Purpose:** Handles the creation and management of customer orders.
*   **Technologies:**
    *   Spring Boot, Spring Web (RESTful API)
    *   Spring Data JPA with MySQL (relational order data storage)
    *   Flyway (database migration management for MySQL)
    *   Lombok
    *   SpringDoc OpenAPI
    *   Testcontainers (for integration testing with MySQL)
    *   Rest-Assured, Hamcrest (for API testing)
    *   Spring Cloud Contract Stub Runner (for consumer-driven contract testing)
*   **Endpoints:**
    *   `POST /api/order`: Places a new order.

### Inventory Service

*   **Purpose:** Manages product stock levels and verifies product availability.
*   **Technologies:**
    *   Spring Boot, Spring Web (RESTful API)
    *   Spring Data JPA with MySQL (relational inventory data storage)
    *   Flyway (database migration management for MySQL)
    *   Lombok
    *   Testcontainers (for integration testing with MySQL)
    *   Rest-Assured, Hamcrest (for API testing)
*   **Endpoints:**
    *   `GET /api/inventory?skuCode={skuCode}&quantity={quantity}`: Checks if a product with the given `skuCode` is in stock for the specified `quantity`.

### API Gateway

*   **Purpose:** The central entry point for all client requests. It provides routing, load balancing, security, and resilience.
*   **Technologies:**
    *   Spring Cloud Gateway (MVC flavor for reactive routing)
    *   Spring Boot OAuth2 Resource Server (for JWT token validation)
    *   Spring Cloud Circuit Breaker with Resilience4j (for fault tolerance)
    *   SpringDoc OpenAPI (for aggregating documentation from downstream services)
    *   Spring Boot Actuator (for monitoring and management)
*   **Routing Logic (from `Routes.java`):**
    *   `product_service`: Routes requests from `/api/product` to `http://localhost:8080` (Product Service). Includes Circuit Breaker.
    *   `order_service`: Routes requests from `/api/order` to `http://localhost:8081` (Order Service). Includes Circuit Breaker.
    *   `inventory_service`: Routes requests from `/api/inventory` to `http://localhost:8082` (Inventory Service). Includes Circuit Breaker.
    *   **Aggregated Swagger:** Routes `/aggregate/{service-name}/v3/api-docs` to respective service's `/v3/api-docs`, re-pathing them.
    *   `fallbackRoute`: Provides a generic fallback response (`503 Service Unavailable`) when a circuit breaker trips.

---

## ✨ Key Concepts & Features Demonstrated

*   **Microservices Architecture:** Decomposing a monolithic application into smaller, independent, and loosely coupled services.
*   **Polyglot Persistence:** Using different types of databases (MongoDB for Product, MySQL for Order/Inventory) based on service specific needs.
*   **API Gateway Pattern:** Centralizing entry points, routing, and cross-cutting concerns (security, resilience).
*   **Circuit Breaker (Resilience4j):** Preventing cascading failures by isolating failing services.
*   **OAuth2 Resource Server:** Securing APIs with JWT tokens at the gateway level.
*   **Database Migrations (Flyway):** Managing schema changes for relational databases in a version-controlled way.
*   **RESTful APIs:** Designing and implementing standard HTTP-based APIs for inter-service communication and external exposure.
*   **OpenAPI / Swagger UI (SpringDoc):** Auto-generating and serving interactive API documentation, including aggregation at the Gateway.
*   **Containerization (Testcontainers, Docker Compose):** Facilitating consistent development and testing environments by running databases and services in containers.
*   **Robust Testing:** Utilizing `Testcontainers` for integration tests, `Rest-Assured` for API tests, and `Spring Cloud Contract Stub Runner` for consumer-driven contract testing.
*   **Lombok:** Reducing boilerplate code in Java classes (Getters, Setters, Constructors).
*   **Spring Boot Actuator:** Providing production-ready features like monitoring and management endpoints.

---

## 🚀 Getting Started

To get this microservices platform up and running on your local machine, follow these steps.

### Prerequisites

*   **Java Development Kit (JDK) 17+**
*   **Apache Maven 3.x+**
*   **Docker & Docker Compose** (Essential for running databases and potentially services in containers)

### Building the Services

Navigate to the root directory of each microservice (`product-service`, `order-service`, `inventory-service`, `api-gateway`) and build them:

```bash
# Build all services from the root of your multi-module project (if applicable)
mvn clean install

# Or build individually:
cd product-service
mvn clean install
cd ../order-service
mvn clean install
# ... and so on for inventory-service and api-gateway

    

Running with Docker Compose

This is the easiest way to start all services and their required databases. You'll need a docker-compose.yml file in the root directory that defines all services and their dependencies. Each of the server has docker compose file.


To run with Docker Compose:

    Make sure you have built all the services (mvn clean install).

    Create Dockerfile for each service in their respective directories (e.g., product-service/Dockerfile). A basic Spring Boot Dockerfile might look like:
    code Dockerfile
          
    docker-compose up --build    

    This will build the Docker images for your services (if they don't exist) and start all containers.

Running Services Individually

Alternatively, you can run each service directly using Maven/Spring Boot after building:

    Start Databases:

        Ensure your MongoDB and MySQL instances are running and accessible (e.g., through Docker, or local installations).

        For local MySQL, ensure microservices_db database exists or your application properties are configured to create it.

    Run Each Service: Open a separate terminal for each service.
    code Bash

    IGNORE_WHEN_COPYING_START
    IGNORE_WHEN_COPYING_END

          
    # Product Service (default port 8080)
    cd product-service
    mvn spring-boot:run

    # Order Service (default port 8081, assuming you configure application.properties)
    cd order-service
    mvn spring-boot:run

    # Inventory Service (default port 8082, assuming you configure application.properties)
    cd inventory-service
    mvn spring-boot:run

    # API Gateway (default port 8083, assuming you configure application.properties)
    cd api-gateway
    mvn spring-boot:run

        

    Note: You'll need to configure application.properties or application.yml for each service to ensure they listen on different ports (e.g., server.port=8080 for Product, 8081 for Order, etc.) and connect to their respective databases.

🛠 API Endpoints

Once the services are running (e.g., API Gateway on http://localhost:8083), you can interact with the system via the Gateway:

    Product Service:

        Create Product: POST http://localhost:8083/api/product
        code Json

    IGNORE_WHEN_COPYING_START
    IGNORE_WHEN_COPYING_END

          
    {
      "name": "Laptop Pro",
      "description": "High-performance laptop",
      "price": 1200.0
    }

        

    Get All Products: GET http://localhost:8083/api/product

Order Service:

    Place Order: POST http://localhost:8083/api/order
    code Json

        IGNORE_WHEN_COPYING_START
        IGNORE_WHEN_COPYING_END

              
        {
          "customerName": "Jane Doe",
          "items": [
            {"skuCode": "laptop-pro-123", "quantity": 1},
            {"skuCode": "mouse-rgb-456", "quantity": 2}
          ]
        }

            

    Inventory Service:

        Check Stock: GET http://localhost:8083/api/inventory?skuCode=laptop-pro-123&quantity=1

    OpenAPI (Swagger UI):

        Aggregated Gateway Swagger: http://localhost:8083/swagger-ui.html

        You can also access individual service Swagger UIs if their ports are exposed:

            Product Service: http://localhost:8080/swagger-ui.html

            Order Service: http://localhost:8081/swagger-ui.html

            Inventory Service: http://localhost:8082/swagger-ui.html

✅ Testing

This project emphasizes robust testing:

    Unit Tests: Standard Spring Boot unit tests for individual components.

    Integration Tests:

        Extensively uses Testcontainers to spin up real database instances (MongoDB, MySQL) for reliable and isolated integration testing.

        Rest-Assured is used for easily performing HTTP requests and validating responses in integration tests.

    Contract Testing: The Order Service includes spring-cloud-starter-contract-stub-runner, indicating the use of consumer-driven contract testing to ensure compatible APIs between services.

📈 Observability

    Spring Boot Actuator: The API Gateway includes Spring Boot Actuator, providing various endpoints for monitoring and managing the application in production (e.g., health, metrics). You can access these typically at http://localhost:8083/actuator.
