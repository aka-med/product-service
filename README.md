# Product API

A small RESTful web service for managing a catalog of **products**, built with Spring Boot and backed by a MySQL database. The API exposes standard CRUD operations plus a few convenience lookups (by category, by maximum price, and by name search).

This is a clean, single-resource example of a typical layered Spring Boot application: **Controller → Service → Repository → Entity**.

---

## What the project does

The application manages a single domain entity, `Product`, with the following fields:

| Field           | Type       | Notes                       |
| --------------- | ---------- | --------------------------- |
| `id`          | `Long`   | Primary key, auto-generated |
| `name`        | `String` |                             |
| `description` | `String` |                             |
| `price`       | `Double` |                             |
| `category`    | `String` |                             |

Products are persisted to a `products` table in MySQL. Hibernate creates/updates the schema automatically on startup (`ddl-auto=update`).

---

## Tech stack

| Layer            | Technology                                          |
| ---------------- | --------------------------------------------------- |
| Language         | Java 21                                             |
| Framework        | Spring Boot 4.1.0 (Spring Web MVC, Spring Data JPA) |
| ORM              | Hibernate / JPA                                     |
| Database         | MySQL 8.0                                           |
| Build tool       | Maven (with the Maven Wrapper`mvnw`)              |
| Containerization | Docker Compose (for the MySQL instance)             |
| Testing          | JUnit 5, Spring Boot Test                           |

### Project layout

```
src/main/java/com/akamed/productservice/
├── ProductServiceApiApplication.java   # Spring Boot entry point
├── model/Product.java             # JPA @Entity mapped to the "products" table
├── repository/ProductRepository.java  # Spring Data JPA repository + derived queries
├── service/ProductService.java    # Business logic layer
└── controller/ProductController.java  # REST endpoints under /api/products

src/main/resources/
└── application.properties         # Datasource & JPA configuration

docker-compose.yml                 # MySQL 8.0 service definition
.env.example                       # Template for environment variables
```

---

## API reference

All endpoints are served under the base path **`/api/products`**.

| Method     | Path                               | Description                                        | Body             |
| ---------- | ---------------------------------- | -------------------------------------------------- | ---------------- |
| `GET`    | `/api/products`                  | List all products                                  | —               |
| `GET`    | `/api/products/{id}`             | Get a product by its ID                            | —               |
| `GET`    | `/api/products/category/{name}`  | List products in a given category                  | —               |
| `GET`    | `/api/products/price/{maxPrice}` | List products priced at or below`maxPrice`       | —               |
| `GET`    | `/api/products/search?name={q}`  | Search products whose name contains`q` (no case) | —               |
| `POST`   | `/api/products`                  | Create a new product (returns`201 Created`)      | `Product` JSON |
| `PUT`    | `/api/products/{id}`             | Update an existing product                         | `Product` JSON |
| `DELETE` | `/api/products/{id}`             | Delete a product                                   | —               |

**Product JSON shape:**

```json
{
  "name": "Wireless Mouse",
  "description": "Ergonomic 2.4GHz wireless mouse",
  "price": 25.99,
  "category": "electronics"
}
```

> Note: Requests for a missing `id` currently raise a `RuntimeException` ("Product not found"), which Spring surfaces as an HTTP `500`.

---

## Prerequisites

- **Java 21** (JDK)
- **Docker** and **Docker Compose** (to run MySQL locally), or a standalone MySQL 8.0 instance
- No local Maven install needed — the bundled Maven Wrapper (`./mvnw`) is used

---

## Configuration

The app reads its database connection from environment variables, with sensible defaults baked into `application.properties`:

| Variable          | Default (if unset) | Used by                                        |
| ----------------- | ------------------ | ---------------------------------------------- |
| `DB_HOST`       | `localhost`      | Spring datasource URL                          |
| `DB_PORT`       | `3306`           | Spring datasource URL                          |
| `DB_NAME`       | `productdb`      | Spring datasource URL                          |
| `DB_USER`       | `root`           | Spring datasource username                     |
| `DB_PASSWORD`   | `1234`           | Spring datasource password                     |
| `ROOT_PASSWORD` | —                 | MySQL container root password (Docker Compose) |

Create a `.env` file from the template before starting the database:

```bash
cp .env.example .env
```

Then fill it in, for example:

```env
ROOT_PASSWORD=1234
DB_NAME=productdb
DB_USER=root
DB_PASSWORD=1234
DB_PORT=3306
```

---

## Running the application

### 1. Start MySQL

```bash
docker compose up -d
```

This launches a `mysql:8.0` container named `productdb-mysql`, exposes it on port `3306`, and creates the database named in `DB_NAME`. Data persists in the `mysql_data` volume.

### 2. Start the Spring Boot app

```bash
./mvnw spring-boot:run
```

The app boots on **http://localhost:8080**. Hibernate will create/update the `products` table automatically.

### 3. Try it out

```bash
# Create a product
curl -X POST http://localhost:8080/api/products \
  -H "Content-Type: application/json" \
  -d '{"name":"Wireless Mouse","description":"Ergonomic mouse","price":25.99,"category":"electronics"}'

# List all products
curl http://localhost:8080/api/products

# Get one by id
curl http://localhost:8080/api/products/1

# Filter by category
curl http://localhost:8080/api/products/category/electronics

# Filter by max price
curl http://localhost:8080/api/products/price/30

# Search by name
curl "http://localhost:8080/api/products/search?name=mouse"

# Update
curl -X PUT http://localhost:8080/api/products/1 \
  -H "Content-Type: application/json" \
  -d '{"name":"Wireless Mouse Pro","description":"Upgraded","price":29.99,"category":"electronics"}'

# Delete
curl -X DELETE http://localhost:8080/api/products/1
```

---

## Testing

### Run the test suite

```bash
./mvnw test
```

The current suite (`ProductServiceApiApplicationTests`) contains a `contextLoads` smoke test that verifies the Spring application context starts correctly. Because it uses `@SpringBootTest`, it needs a reachable database matching the configured datasource — so **start MySQL (step 1 above) before running the tests**, or point the datasource variables at a test database.

### Build a runnable JAR

```bash
./mvnw clean package
java -jar target/product-service-api-0.0.1-SNAPSHOT.jar
```

### Manual / exploratory testing

Use the `curl` commands above, or import the endpoints into **Postman** / **Insomnia**. Since `spring.jpa.show-sql=true` is enabled, you can watch the generated SQL in the application logs while testing.
