# MediaTrackerWithAuth

This is an extended version of the [MediaTracker](https://github.com/Lemersom/MediaTracker) project, incorporating authentication and authorization features using Spring Security and JWT. This version is designed to manage media items while ensuring secure access for different types of users.

## Features Added in MediaTrackerWithAuth

- **User Management:**
  - Endpoint to register new users (/auth/register).
  - Endpoint for user login, returning a JWT token (/auth/login).
  - Role-based access control with two roles: ADMIN and USER.
- **Secure Endpoints:**
  - Specific permissions:
    - `ADMIN`: Full access to create, update, delete, and manage media items and types.
    - `USER`: Limited to viewing media items.
- **JWT Integration:**
  - Token generation on successful login.
  - Validation of tokens for secure API requests.
  - Stateless authentication using JWT.

# Authentication Workflow

## Register a User
- Endpoint: `POST /auth/register`
- Request Body:
  ```
  {
    "userName": "exampleUser",
    "password": "examplePassword",
    "role": "USER"
  }
  ```
- Response:
  - `200 OK` on success.
  - `400 Bad Request` if the username has already been taken.

## Login
- Endpoint: `POST /auth/login`
- Request Body:
  ```
  {
    "userName": "exampleUser",
    "password": "examplePassword"
  }
  ```
- Response:
  - `200 OK` with JWT token..
  - `401 Unauthorized` for invalid credentials.

## Access Control

- Include the token in the `Authorization` header as `Bearer <token>` for all subsequent requests.

### Role-Based Access Control

| Endpoint          | Method | Role Required |
| ----------------- | ------ | ------------- |
| /auth/register    | POST   | None          |
| /auth/login       | POST   | None          |
| /user             | GET    | ADMIN         |
| /user/{id}        | GET    | ADMIN         |
| /media-item       | POST   | ADMIN         |
| /media-item/{id}  | PUT    | ADMIN         |
| /media-item/{id}  | DELETE | ADMIN         |
| /media-type       | POST   | ADMIN         |
| /media-type/{id}  | PUT    | ADMIN         |
| /media-type/{id}  | DELETE | ADMIN         |

# Usage

- Set the JWT secret key in the application properties: `api.security.token.secret`

# Related Repositories

- [MediaTracker](https://github.com/Lemersom/MediaTracker)
