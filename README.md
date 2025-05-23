# 🌌 *SpaceTravel CLI Application*

**SpaceTravel** is a Java-based command-line application that demonstrates working with an **H2 database** using **Flyway migrations** and **Hibernate ORM**. It allows you to perform full **CRUD operations** on `Client` and `Planet` entities via a simple **CLI interface**.


## 🗂️ Table of Contents

- [📌 Introduction](#-introduction)
- [🌟 Features](#-features)
- [🛠️ Technologies Used](#-technologies-used)
- [🧱 Core Components](#-core-components)
- [⚙️ Application Logic](#-application-logic)
- [▶️ How to Run](#-how-to-run)
- [💻 Command Examples](#-command-examples)
- [🧾 Flyway SQL Scripts](#-flyway-sql-scripts)
- [📂 Project Structure](#-project-structure)


## 📌 Introduction

**SpaceTravel** demonstrates:

- Automatic schema creation and data population using Flyway migrations.

- Lightweight H2 database – file-based for development, in-memory for testing.

- Full CRUD functionality for Client and Planet entities.

- Modular CLI interface and layered architecture using interfaces for flexibility and testability.

**Core concepts:**

- Database versioning using Flyway (V1__create_db.sql, V2__populate_db.sql)

- Hibernate ORM with JPA entity mapping

- Command-driven user interface



## 🌟 Features

- ✅ Flyway migration system (V1__create_db.sql, V2__populate_db.sql)

- ✅ Hibernate ORM integration

- ✅ H2 database (embedded)

- ✅ Full CLI control with error handling

- ✅ Clean separation of concerns: CLI ↔ Service ↔ DAO ↔ DB

- ✅ Interface-based design for DAO and Service layers

- ✅ Dependency injection via constructors

- ✅ CRUD operations for both entities:

  - Create / Read / Update / Delete Clients

  - Create / Read / Update / Delete Planets

- ✅ Simple command-line interface

- ✅ Clean code architecture (Entity, DAO, Service, CLI parser)

- ✅ Test coverage for entities, services, DAOs, integration


## 🛠️ Technologies Used

- Java 17 – primary programming language

- Gradle – build automation tool

- Hibernate ORM 6 – object-relational mapping framework

- Flyway – database migration tool

- H2 Database – lightweight H2 database for dev/test

- JPA (Jakarta Persistence API) – for defining entity classes

- JUnit 5 – testing framework

- SLF4J – logging interface


## 🧱 Core Components

- `App.java` – entry point; initializes the command parser

- `CommandParser.java` – CLI command processor; wires services with DAO implementations

- `ClientCrudServiceImpl.java`, `PlanetCrudServiceImpl .java` – business logic services; service implementations, injected via constructors

- `ClientDaoImpl.java`, `PlanetDaoImpl.java` – DAO layer using Hibernate; DAO implementations

- **Interfaces**:
    - `ClientCrudService`, `PlanetCrudService`
    - `ClientDao`, `PlanetDao`

- `Client.java`, `Planet.java` – JPA entities

- `FlywayConfig.java`, `HibernateUtil.java` – DB and Hibernate setup

- `application.properties`, `application-test.properties` – environment-specific configs

- **Tests:**

  - Unit tests for DAO, Service, and Entity

  - Integration tests using H2 and Flyway


## ⚙️ Application Logic

1. The user enters a command in the CLI.

2. CommandParser parses the command and delegates to the correct service.

3. Services call DAOs (wired via constructor injection).

4. DAOs interact with the DB via Hibernate.

5. Results are displayed in the console.

**Typical scenarios:**

- client create Elon → creates a client named Elon

- planet list → lists all planets

- client delete 3 → deletes client with ID = 3


## ▶️ How to Run

### 🖥️ Run with Gradle:

#### Use the following commands to run the application via Gradle:

- **Build Project Jar**

For MacOS/Linux
  ```bash
  ./gradlew build
  ```

For Windows
  ```bash
  gradlew.bat build
  ```

- **Start CLI**


  ```bash
  java -jar path/to/*.jar start
  ```

- **Run Command**

For example
  ```bash
  > client list  
  ```

- **Exit CLI**

For example
  ```bash
  > exit  
  ```

## 💻 Command Examples

### Core Commands

```bash
help                          # Show help menu
exit                          # Exit program
```
### Client Commands:

```bash
client create <name>          # Create new client
client list                   # List all clients
client get <id>               # Get client with ID = 1
client update <id> <name>     # Update client name by ID
client delete <id>            # Delete client by ID = 2
```
### Planet Commands:

```bash
planet create <id> <name>     # Create new planet
planet list                   # List all planets
planet get <id>               # Get planet by ID
planet update <id> <name>     # Update planet name
planet delete <id>            # Delete planet
```

## 🧾 Flyway SQL Scripts
V1__create_db.sql
  - Creates client and planet tables

V2__populate_db.sql
  - Inserts initial data:

    - 5 Clients (e.g., John, Jane, etc.)

    - 5 Planets (e.g., EARTH, MARS, etc.)

    - 10 Tickets

## 📂 Project Structure

```bash
spacetravel/
├── build.gradle
├── data/
├── src/
│   ├── main/
│   │   ├── java/com/spacetravel/
│   │   │   ├── cli/                # App.java, CommandParser.java
│   │   │   ├── config/             # Hibernate and Flyway setup
│   │   │   ├── dao/                # ClientDao, PlanetDao interfaces + implementations
│   │   │   ├── entity/             # Client, Planet (JPA entities)
│   │   │   ├── exception/          # Custom exceptions
│   │   │   ├── service/            # Business logic (CRUD services) interfaces + implementations
│   │   │   └── util/               # Logger utility
│   │   └── resources/
│   │       ├── application.properties
│   │       └── db/migration/
│   │           ├── V1__create_db.sql
│   │           └── V2__populate_db.sql
│   │
│   └── test/
│       ├── java/com/spacetravel/
│       │   ├── dao/                # Unit tests for DAO
│       │   ├── entity/             # Unit tests for Entity
│       │   ├── integration/        # Integration tests using H2 and Flyway
│       │   └── service/            # Unit tests for Services 
│       └── resources/
│           ├── application.properties
│           ├── META-INF/persistence.xml
│           └── db/migration/
│               ├── V1__init_db.sql
│               └── V2__fulfill_db.sql
```