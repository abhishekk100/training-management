# Training Management System – UAT Setup

This guide explains how to set up and run the **Training Management System** (Frontend + Backend + Database) for UAT testing using Docker Compose.

---

## 1. Project Structure

training_management_system/
├── backend/ # Spring Boot app (with Dockerfile)
├── frontend/ # Vue app (with Dockerfile)
├── db/ # Database initialization scripts
│ └── init.sql
├── docker-compose.yml
├── .env.example # Sample environment variables
├── README.md

---

## 2. Prerequisites

- [Docker](https://www.docker.com/get-started) installed and running
- [Docker Compose](https://docs.docker.com/compose/install/)
- Optional: Local MariaDB for development (if running Spring Boot outside Docker)
- Git client for cloning the repo

---

## 3. Environment Variables

1. Copy the example environment file:

- Create a copy of .env.example to a .env file and add following changes
- Keep the username and password same for the database and backend spring boot configuration

cp .env.example .env
# Database root (internal use)
MARIADB_ROOT_PASSWORD=your_root_password

# Application database user
MARIADB_DATABASE=training_management
MARIADB_USER=training_user
MARIADB_PASSWORD=training_pass

# Spring Boot datasource
SPRING_DATASOURCE_URL=jdbc:mariadb://db:3306/training_management
SPRING_DATASOURCE_USERNAME=training_user
SPRING_DATASOURCE_PASSWORD=training_pass

# Launch the Docker Desktop and Run the command from the root dir
docker-compose up --build

| Service  | URL/Port                                                      |
| -------- | ------------------------------------------------------------- |
| Frontend | [http://localhost](http://localhost)                          |
| Backend  | [http://localhost:8080](http://localhost:8080)                |
| Database | localhost:3306 (user: `training_user`, pass: `training_pass`) |

docker-compose down
docker-compose down -v
