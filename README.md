# CS2 Ranking System

A microservices-based ranking system and leaderboard for Counter-Strike 2.

## Architecture

The project consists of 4 independent Spring Boot services and a shared `common` module:

```
cs2-ranking/
├── common/           # Shared contracts and events
├── player-service/   # Player management & statistics   :8081
├── match-service/    # Match registration               :8082
├── ranking-service/  # Elo calculation & leaderboard    :8083
└── activity-service/ # Real-time activity feed (WS)     :8084
```

### Data Flow

1.  **Registration**: `POST /players` → `player-service` → `ranking-service`
2.  **Match**: `POST /matches` → `match-service` → `ranking-service` (Elo) → `player-service` (Map Stats)
3.  **Social**: `POST /players/{id}/teams` → `player-service` → `activity-service` (WebSocket Broadcast)
4.  **Earnings**: `POST /players/{id}/earnings` → `player-service`

## Requirements

-   Java 21
-   Maven
-   Docker Desktop

## Getting Started

### 1. Build the project

```bash
./manage.sh build
```

### 2. Deploy all services

```bash
./manage.sh deploy
```

The script will automatically start PostgreSQL, create databases, run all 4 Spring Boot services, and verify their status.

### 3. Stop services

```bash
./manage.sh stop
```

## Testing Examples

### 1. Create Players
```bash
# Create Player 1
curl -X POST http://localhost:8081/players -H "Content-Type: application/json" -d '{"username": "s1mple"}'

# Create Player 2
curl -X POST http://localhost:8081/players -H "Content-Type: application/json" -d '{"username": "zywoo"}'
```

### 2. Join a Team (Triggers Real-time Feed)
```bash
# s1mple joins NaVi
curl -X POST "http://localhost:8081/players/1/teams?teamName=NaVi"
```
*Check `activity-service` logs or WebSocket feed to see the broadcast.*

### 3. Record a Match (Updates Elo & Map Stats)
```bash
curl -X POST http://localhost:8082/matches \
  -H "Content-Type: application/json" \
  -d '{
    "winnerTeamPlayerIds": [1],
    "loserTeamPlayerIds": [2],
    "mapName": "de_dust2"
  }'
```

### 4. Add Tournament Earnings
```bash
curl -X POST http://localhost:8081/players/1/earnings \
  -H "Content-Type: application/json" \
  -d '{
    "tournamentName": "PGL Major Copenhagen 2024",
    "amount": 500000,
    "currency": "USD"
  }'
```

### 5. Check Player Profile
```bash
curl http://localhost:8081/players/1
```
*Observe `eloRating`, `teamHistory`, `mapStats` (with win rates), and `totalEarnings`.*

### 6. View Leaderboard
```bash
curl http://localhost:8083/leaderboard
```

### 7. Real-time Activity Feed (WebSocket)
Connect to: `ws://localhost:8084/ws-activity`
Topic to subscribe: `/topic/feed`

## Elo System

Based on the classic Elo algorithm (K-factor = 32).
-   Expected score formula: `E = 1 / (1 + 10^((Opponent - Player) / 400))`
-   New rating: `R_new = R_old + K * (Actual - E)`

## Technologies

-   Java 21 / Spring Boot 3.3
-   Spring Data JPA / PostgreSQL 16
-   WebSockets (STOMP / SockJS)
-   Docker & Docker Compose
-   Maven Multi-module
-   JUnit 5 & Mockito