# Blackjack API Endpoints

This document provides an overview of all available API endpoints in the Blackjack API.

## Base URL
```
http://localhost:8080
```

## Swagger Documentation
- **Swagger UI**: http://localhost:8080/swagger-ui
- **OpenAPI Spec**: http://localhost:8080/v3/api-docs

## Player Management Endpoints

### Create Player
```http
POST /api/players
Content-Type: application/json

{
  "username": "johnsmith",
  "email": "john.smith@example.com"
}
```

### Get Player by ID
```http
GET /api/players/{id}
```

### Get Player by Username
```http
GET /api/players/username/{username}
```

### Update Player
```http
PUT /api/players/{id}
Content-Type: application/json

{
  "username": "johnsmith_updated",
  "email": "john.updated@example.com"
}
```

### Delete Player
```http
DELETE /api/players/{id}
```

### Get All Players
```http
GET /api/players
```

### Get Top Players
```http
GET /api/players/top?limit=10
```

### Get Wealthy Players
```http
GET /api/players/wealthy?threshold=100.0
```

### Get Player Statistics
```http
GET /api/players/{id}/stats
```

### Reset Daily Statistics
```http
POST /api/players/reset-daily-stats
```

## Game Management Endpoints

### Start New Game
```http
POST /api/games
Content-Type: application/json

{
  "playerId": 1,
  "bet": 25.00
}
```

### Hit (Draw Card)
```http
POST /api/games/{gameId}/hit
```

### Stand (End Turn)
```http
POST /api/games/{gameId}/stand
```

### Split Hand
```http
POST /api/games/{gameId}/split
```

### Take Insurance
```http
POST /api/games/{gameId}/insurance
```

### Get Game Details
```http
GET /api/games/{gameId}
```

### Get Player's Active Games
```http
GET /api/games/player/{playerId}/active
```

### Get Player's Game History
```http
GET /api/games/player/{playerId}/history?startDate=2023-12-01T00:00:00&endDate=2023-12-31T23:59:59
```

### Get All Player's Games
```http
GET /api/games/player/{playerId}
```

### Get High Stake Games
```http
GET /api/games/high-stakes?threshold=100.0
```

### Clean Up Old Games
```http
DELETE /api/games/cleanup?olderThan=2023-01-01T00:00:00
```

## Example Usage Flow

### 1. Create a Player
```bash
curl -X POST http://localhost:8080/api/players \
  -H "Content-Type: application/json" \
  -d '{
    "username": "alice",
    "email": "alice@example.com"
  }'
```

### 2. Start a Game
```bash
curl -X POST http://localhost:8080/api/games \
  -H "Content-Type: application/json" \
  -d '{
    "playerId": 1,
    "bet": 10.00
  }'
```

### 3. Play the Game
```bash
# Hit (draw a card)
curl -X POST http://localhost:8080/api/games/{gameId}/hit

# Stand (end turn)
curl -X POST http://localhost:8080/api/games/{gameId}/stand
```

### 4. Check Game Result
```bash
curl http://localhost:8080/api/games/{gameId}
```

### 5. View Player Statistics
```bash
curl http://localhost:8080/api/players/1/stats
```

## Response Examples

### Player Response
```json
{
  "id": 1,
  "username": "alice",
  "email": "alice@example.com",
  "balance": 90.00,
  "gamesPlayed": 1,
  "gamesWon": 0,
  "totalWinnings": 0.00,
  "gamesPlayedToday": 1,
  "gamesWonToday": 0,
  "createdAt": "2023-12-08T15:30:45",
  "updatedAt": "2023-12-08T15:35:20"
}
```

### Game Response
```json
{
  "id": "507f1f77bcf86cd799439011",
  "playerId": 1,
  "playerHand": {
    "cards": [
      {"suit": "HEARTS", "rank": "TEN", "faceUp": true},
      {"suit": "SPADES", "rank": "EIGHT", "faceUp": true}
    ]
  },
  "dealerHand": {
    "cards": [
      {"suit": "DIAMONDS", "rank": "SEVEN", "faceUp": true},
      {"suit": "CLUBS", "rank": "FOUR", "faceUp": false}
    ]
  },
  "bet": 10.00,
  "status": "IN_PROGRESS",
  "startTime": "2023-12-08T15:35:20",
  "actions": ["HIT"]
}
```

### Error Response
```json
{
  "status": 404,
  "error": "NOT_FOUND",
  "message": "Player not found with ID: 999",
  "timestamp": "2023-12-08T15:30:45",
  "path": "/api/players/999"
}
```

## Status Codes

- **200 OK**: Request successful
- **201 Created**: Resource created successfully
- **204 No Content**: Resource deleted successfully
- **400 Bad Request**: Invalid input data
- **404 Not Found**: Resource not found
- **409 Conflict**: Resource conflict (e.g., duplicate username)
- **500 Internal Server Error**: Server error

## Authentication

Currently, the API does not require authentication. In production, you would typically add:
- JWT token authentication
- API key authentication
- OAuth2 integration

## Rate Limiting

No rate limiting is currently implemented. Consider adding rate limiting for production use.

## Pagination

The `/api/players` endpoint currently returns all players. In production, implement pagination:
```http
GET /api/players?page=0&size=20&sort=username,asc
``` 