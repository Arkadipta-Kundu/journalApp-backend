# Journal App Backend - Phase 1

## Overview

This is Phase 1 of the Journal App Backend, implementing core user management and journal entry functionality. This phase provides a solid foundation for a journaling application with basic authentication and CRUD operations.

## Phase 1 Features ✅

### User Module

- ✅ User registration with username, email, and password
- ✅ Secure password hashing using bcrypt
- ✅ User login with session-based authentication
- ✅ User profile management (view, update, delete)
- ✅ Session management with automatic cleanup

### Journal Module

- ✅ Create journal entries with title and content
- ✅ View all entries for authenticated user
- ✅ Update existing journal entries
- ✅ Delete journal entries
- ✅ Authorization: users can only access their own entries

### Security

- ✅ Password hashing with bcrypt
- ✅ Session-based authentication
- ✅ Input validation
- ✅ Authorization checks

## API Endpoints

### Authentication

- `POST /api/auth/register` - Register a new user
- `POST /api/auth/login` - Login user
- `POST /api/auth/logout` - Logout user
- `GET /api/auth/me` - Get current user info

### Users

- `GET /api/users/{userId}` - Get user by ID
- `PUT /api/users/{userId}` - Update user profile
- `DELETE /api/users/{userId}` - Delete user account

### Journal Entries

- `POST /api/entries` - Create new journal entry
- `GET /api/entries` - Get current user's entries
- `GET /api/entries/{entryId}` - Get specific entry
- `PUT /api/entries/{entryId}` - Update entry
- `DELETE /api/entries/{entryId}` - Delete entry
- `GET /api/users/{userId}/entries` - Get entries by user ID

### Public

- `GET /public/api/health` - Health check
- `GET /public/api/info` - Application info

## Setup Instructions

### Prerequisites

- Java 21
- Maven 3.6+
- MongoDB 4.4+

### Database Setup

1. Install and start MongoDB
2. The application will connect to `mongodb://localhost:27017/journalapp`
3. Collections will be created automatically

### Running the Application

1. Clone the repository
2. Navigate to project directory
3. Run: `mvn spring-boot:run`
4. Application will start on `http://localhost:8080`

## Configuration

### MongoDB

```properties
spring.data.mongodb.uri=mongodb://localhost:27017/journalapp
spring.data.mongodb.database=journalapp
```

### Session Management

```properties
app.session-timeout=3600  # 1 hour
app.session-secret=journal-app-secret-key-change-in-production
```

## API Usage Examples

### Register User

```bash
curl -X POST http://localhost:8080/api/auth/register \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "email": "john@example.com",
    "password": "password123"
  }'
```

### Login

```bash
curl -X POST http://localhost:8080/api/auth/login \
  -H "Content-Type: application/json" \
  -d '{
    "username": "johndoe",
    "password": "password123"
  }'
```

### Create Journal Entry

```bash
curl -X POST http://localhost:8080/api/entries \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_SESSION_TOKEN" \
  -d '{
    "title": "My First Entry",
    "content": "Today was a great day..."
  }'
```

### Get User Entries

```bash
curl -X GET http://localhost:8080/api/entries \
  -H "Authorization: Bearer YOUR_SESSION_TOKEN"
```

## Data Models

### User

```json
{
  "id": "string",
  "username": "string",
  "email": "string",
  "password": "string (hashed)",
  "createdAt": "datetime"
}
```

### Journal Entry

```json
{
  "id": "string",
  "authorId": "string",
  "title": "string (max 140 chars)",
  "content": "string (max 20k chars)",
  "createdAt": "datetime",
  "updatedAt": "datetime"
}
```

### Session

```json
{
  "id": "string",
  "sessionToken": "string",
  "userId": "string",
  "createdAt": "datetime",
  "expiresAt": "datetime"
}
```

## Validation Rules

- Username: 3-20 characters, unique
- Email: Valid email format, unique
- Password: 6-100 characters
- Journal title: Required, max 140 characters
- Journal content: Required, max 20,000 characters

## Next Phase Preview

Phase 2 will add:

- Follow/unfollow system
- Like system for journal entries
- Basic social interactions
- Follower/following counters

## Architecture Notes

- Built with Spring Boot 3.5.5
- MongoDB for data persistence
- Lombok for reducing boilerplate
- Session-based authentication (will upgrade to JWT in Phase 4)
- RESTful API design
- Input validation with Bean Validation
- Global exception handling
- Scheduled cleanup of expired sessions

This implementation provides a solid foundation that can be easily extended in future phases while maintaining clean code architecture and proper separation of concerns.
