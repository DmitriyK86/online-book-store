# Online Book Store

## Description
This project is an online book store where users can browse, search, and purchase books. It includes user registration and authentication, category management, order placement, and shopping cart functionality

## Features
- User registration
- User authentication using JWT tokens
- App has roles with different rights - admin/user
- Manage books
- Browse books by category
- Search for specific books
- Add books to the shopping cart
- View and manage the shopping cart
- Place orders and complete purchases

## Detailed API breakdown:
Users can have USER or ADMIN roles. USER role assigns automatically to every new user.
Here is detailed list of API Endpoints:
- POST: auth/register (all) - register new user;
- POST: auth/login (for registered users) - login user;
- POST: /books (admin) - create a new book;
- GET: /books (user/admin) - get a list of all available books;
- GET: /books/{id} (user/admin) - get a book by id;
- DELETE: /books/{id} (admin) - delete a book by id;
- PUT: /books/{id} (admin) - update a book;
- GET: /books/search (user/admin) - search for books by specific parameters;
- POST: /categories (admin) - create a new category;
- GET: /categories (user/admin) - get a list of all available categories;
- GET: /categories/{id} (user/admin) - get category by id;
- DELETE: /categories/{id} (admin) - delete a category by id;
- GET: /categories/{id}/books (user/admin) - get all books by category id;
- POST: /orders (user/admin) - place an order;
- GET: /orders (user/admin) - retrieve user's order history;
- GET: /orders/{orderId}/items (user) - retrieve all order items for a specific order;
- GET: /orders/{orderId}/items/{itemId} (user) - retrieve a specific order item within an order;
- PATCH: /orders/{orderId} (admin) - update order status;
- GET: /cart (user) - get shopping cart;
- POST /cart (user) - add book to shopping cart;
- PUT: /cart/cart-items/{cartItemId} (user) - update quantity of a book in the shopping cart;
- DELETE: /cart/cart-items/{cartItemId} (user) - delete a book from shopping cart by id;

## Project structure:
Project has a 3-layer architecture:
- the data access layer (CRUD operations with database);
- the application logic layer (services);
- the presentation layer (controllers).

## Used technologies:
- Java 17
- MySql 8.0.22
- Apache Maven, Tomcat 9.0.73
- Hibernate 5.6.14
- Spring Boot, Security, Web, Spring MVC 5.3.20
- Docker, Test containers
- Liquibase
- Lombok
- Mockito, JUnit tests, Integrations tests

## Instructions for running the application:
1) Ensure you have Docker installed on your system ([Docker's official website](https://www.docker.com/get-started))
2) Clone this repo to your IDE, configure your database settings in your .env file
3) Open a terminal and navigate to the root directory of your project
4) Run the application using "docker-compose up" command
5) Explore the endpoints using tools like Postman or Swagger