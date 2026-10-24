# Recipe Repository

A work-in-progress app for storing and organizing recipes. Angular frontend, Spring Boot backend.

## Prerequisites

* Java 21
* Node.js and npm
* Docker and Docker Compose

## Project Structure

```text
recipe-repository/
├── frontend/   # Angular app
└── backend/    # Spring Boot app (Maven)
    ├── docker/   # MySQL and Redis compose file
    ├── secrets/  # Local config (example files included)
    └── src/
```

## Setup

### 1. Configure secrets

In `backend/secrets`, copy each `*-example.properties` file to the same name without `-example`, then fill in the values:

```bash
cd backend/secrets
cp app-example.properties app.properties
cp aws-example.properties aws.properties
cp mysql-example.properties mysql.properties
```

* `app.properties`: JWT secret, admin password, database connection, mail credentials. `application.yml` also reads `APP_NAME` and `EMAIL_REPLY_TO`.
* `aws.properties`: S3 credentials, used for image uploads.
* `mysql.properties`: MySQL container credentials. Keep these consistent with the database values in `app.properties`.

These files are git-ignored. Don't commit them.

### 2. Start MySQL and Redis

```bash
cd backend
docker compose -f docker/compose-infra.yaml up -d
```

### 3. Run the backend

```bash
./mvnw spring-boot:run
```

On Windows, use `mvnw.cmd spring-boot:run`. The API runs on `http://localhost:8080`. Flyway applies database migrations on startup.

### 4. Run the frontend

```bash
cd frontend
npm install
npm start
```

The app runs on `http://localhost:4200`.

Other frontend scripts: `npm test`, `npm run build:prod`.

## Stack

* **Frontend:** Angular 21, TypeScript, Angular Material, Tailwind CSS, ngx-translate, Vitest
* **Backend:** Spring Boot 4, Spring Security (JWT), Spring Data JPA, Flyway
* **Infrastructure:** MySQL, Redis, Amazon S3, Docker

## Features

* Registration, login and password reset
* Recipe creation, editing and search, with variants and ingredient scaling
* Recipe image uploads
* Recipe groups with email invitations
* English and Spanish

## License

[MIT](LICENSE)
