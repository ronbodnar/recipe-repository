# Recipe Repository

Recipe Repository is a work-in-progress application for managing and organizing recipes.

The project currently consists of an Angular frontend and a Spring Boot backend made up of several services. Authentication is handled through Keycloak using JWTs.

## Prerequisites

* Java 21
* Maven
* Node.js and npm
* Angular CLI
* Docker
* Docker Compose

## Project Structure

```text
recipe-repository/
├── frontend/                 # Angular application
└── backend/
    ├── docker/               # Docker Compose and database configuration
    ├── secrets/              # Local environment configuration
    └── services/
        ├── common-core/      # Shared Spring components
        ├── gateway/          # API gateway
        ├── identity-service/ # User-related functionality
        └── recipe-service/   # Recipe functionality
```

## Setup

### 1. Clone the repository

```bash
git clone <repository-url>
cd recipe-repository
```

### 2. Create environment files

The `backend/secrets` directory contains example configuration files.

Copy each example file and remove `.example` from the filename:

```bash
cd backend/secrets

cp app-env.example.properties app-env.properties
cp keycloak-env.example.properties keycloak-env.properties
cp mysql-env.example.properties mysql-env.properties
cp postgres-keycloak-env.example.properties postgres-keycloak-env.properties
```

Update the newly created files with the appropriate local configuration.

**Do not commit the generated `.properties` files.**

### 3. Start the databases and Keycloak

From the backend directory:

```bash
cd ../
docker compose -f docker/compose.yaml up -d
```

This starts:

* MySQL for the recipe application
* PostgreSQL for Keycloak
* Keycloak

To view the running containers:

```bash
docker compose -f docker/compose.yaml ps
```

To stop them:

```bash
docker compose -f docker/compose.yaml down
```

### 4. Build the backend

From the `backend` directory:

```bash
./mvnw clean install
```

On Windows:

```cmd
mvnw.cmd clean install
```

The project is a Maven multi-module project containing:

* `common-core`
* `gateway`
* `identity-service`
* `recipe-service`

### 5. Start the backend services

Each service can be started independently.

#### Gateway

```bash
cd services/gateway
../../mvnw spring-boot:run
```

The gateway runs on:

```text
http://localhost:8080
```

#### Recipe Service

```bash
cd services/recipe-service
../../mvnw spring-boot:run
```

The recipe service runs on:

```text
http://localhost:8081
```

#### Identity Service

```bash
cd services/identity-service
../../mvnw spring-boot:run
```

The identity service runs on:

```text
http://localhost:8082
```

### 6. Start the frontend

From the repository root:

```bash
cd frontend
npm install
ng serve
```

The frontend runs on:

```text
http://localhost:4200
```

## Development

A typical local development environment consists of:

```text
Angular
   │
   ▼
Gateway :8080
   │
   ├──► Recipe Service :8081 ──► MySQL
   │
   └──► Identity Service :8082

Keycloak :9000
   │
   └──► PostgreSQL
```

Keycloak is available at:

```text
http://localhost:9000
```

## Technologies

### Frontend

* Angular
* TypeScript
* RxJS
* Angular Material
* Tailwind CSS

### Backend

* Java 21
* Spring Boot
* Spring Security
* Spring Data JPA
* Spring Cloud Gateway
* Flyway
* MySQL
* Keycloak
* PostgreSQL
* Maven

### Infrastructure

* Docker
* Docker Compose
* Amazon S3

## Current Features

* User authentication through Keycloak
* JWT-based authentication and authorization
* Recipe creation and management
* Recipe image uploads
* Recipe database migrations using Flyway
* Spanish and English localization

## Planned Features

* Family-based recipe sharing
* Family members and roles
* Public recipe sharing through links
* Meal planning
* Additional recipe organization features

## License

This project is licensed under the [MIT License](LICENSE).

