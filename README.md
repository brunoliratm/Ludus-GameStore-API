<div align="center">
  <img src="images/logo.png" width="300" height="300" alt="Ludus Logo">
  <h1 align="center">Ludus Game Store API</h1>
  <h1 align="center">In Progress</h1>
  
  [![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.4.3-brightgreen.svg)](https://spring.io/projects/spring-boot)
  [![Java](https://img.shields.io/badge/Java-21-orange.svg)](https://www.oracle.com/java/)
  [![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)
  [![Swagger UI](https://img.shields.io/badge/Swagger-UI-85EA2D.svg)](http://localhost:8080/swagger-ui.html)
  
  [![Typing SVG](https://readme-typing-svg.demolab.com/?font=Fira+Code&weight=800&size=24&pause=1000&color=A0153E&center=true&lines=Welcome+to+Ludus+GameStore!;)](https://git.io/typing-svg)
  
  <h3>Ludus is not just a game store, it's a gateway to endless adventures and thrilling experiences. Whether you're a casual gamer or a hardcore enthusiast, Ludus has something for everyone.</h3>
</div>

## 📋 Table of Contents

- [📌 Overview](#-overview)
- [✨ Features](#-features)
- [🚀 Getting Started](#-getting-started)
- [📊 API Endpoints](#-api-endpoints)
- [🔧 Technologies](#-technologies)
- [👥 Credits](#-credits)
- [🤝 Contributing](#-contributing)
- [📜 License](#-license)

## 📌 Overview

Ludus Game Store API is a RESTful backend service that powers the Ludus Game Store platform. It provides a comprehensive set of endpoints for game management, user authentication, and purchase processing, allowing developers to integrate game store functionality into their applications.

## ✨ Features

- **Game Management**: Browse, search, add, update, and delete games in the store
- **User Authentication**: Register, login, and manage user profiles securely
- **Purchase Processing**: Seamless game purchase experience with multiple payment methods
- **Categories & Filtering**: Organize games by genre, platform, release year, and price
- **Reviews & Ratings**: Allow users to leave reviews and ratings for games
- **Wishlists**: Create and manage game wishlists for future purchases

## 🚀 Getting Started

### Prerequisites

- Java 21 or above
- Apache Maven
- IDE (IntelliJ IDEA, Eclipse, VS Code, etc.)

### Installation

1. Clone this repository to your local machine:
```bash
git clone https://github.com/brunoliratm/Ludus-GameStore-Api.git
cd Ludus-GameStore-Api
```

2. Build the project:
```bash
mvn clean install
```

3. Run the application:
```bash
java -jar ./target/ludus-2.0.0.jar
```

4. Access the API at `http://localhost:8080/`
5. Explore the API documentation with Swagger UI: `http://localhost:8080/swagger-ui.html`

> [!NOTE]
> You may need to modify the database configuration in `application.properties` to match your environment.

## 📊 API Endpoints

The API provides the following main endpoints:

- **Games**
  - `GET /api/games`: Get all games
  - `GET /api/games/{id}`: Get game by ID
  - `POST /api/games`: Add a new game
  - `PUT /api/games/{id}`: Update a game
  - `DELETE /api/games/{id}`: Delete a game

- **Users**
  - `POST /api/users`: Register a new user
  - `GET /api/users/{id}`: Get user by Id
  - `PUT /api/users/{id}`: Update user information
  - `DELETE /api/users/{id}`: Delete user

- **Purchases**
  - `GET /api/purchases`: Get all purchases
  - `GET /api/purchases/{id}`: Get purchase by ID
  - `POST /api/purchases`: Create a new purchase
  - `GET /api/purchases/user/{id}`: Get purchases by user

## 🔧 Technologies

- **Spring Boot**: Framework for creating stand-alone, production-grade Spring applications
- **Spring Data JPA**: Simplifies data access layer implementation
- **MySQL**: Relational database management system for production
- **Swagger/OpenAPI**: API documentation
- **Lombok**: Reduces boilerplate code
- **Maven**: Dependency management and build tool

## 👥 Credits

| <a href="https://github.com/brunoliratm"><img src="https://avatars.githubusercontent.com/u/114788642?v=4" float="left" width="40px" height=40px><p>BrunoMagno</p></a> | <a href="https://github.com/Paulo-Araujo-Jr"><img src="https://avatars.githubusercontent.com/u/127964717?v=4" float="left" width="40px" height="40px"><p>PauloAraujo</p></a> |
| --- | --- |

## 🤝 Contributing

We welcome contributions from the open-source community. To contribute:

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/feature`)
3. Commit your changes (`git commit -m 'Add some feature'`)
4. Push to the branch (`git push origin feature/feature`)
5. Open a Pull Request

Please make sure your code follows the project's coding standards and includes appropriate tests.

## 📜 License

This project is licensed under the [MIT License](LICENSE).

