# CS2 Ranking System

System rankingowy i tabela liderów dla gry Counter-Strike 2.

## Architektura

Projekt składa się z 3 niezależnych serwisów Spring Boot oraz wspólnego modułu `common`:

```
cs2-ranking/
├── common/          # wspólne kontrakty i eventy
├── player-service/  # zarządzanie graczami         :8081
├── match-service/   # rejestracja meczów            :8082
└── ranking-service/ # ELO i tabela liderów          :8083
```

### Przepływ danych

```
POST /players  →  player-service  →  ranking-service (rejestracja gracza)
POST /matches  →  match-service   →  ranking-service (aktualizacja ELO)
GET /leaderboard  ←  ranking-service
```

## Wymagania

- Java 21
- Maven (wbudowany w IntelliJ IDEA)
- Docker Desktop

## Uruchomienie

### 1. Sklonuj repozytorium

```bash
git clone <repo-url>
cd cs2-ranking
```

### 2. Zbuduj projekt

```bash
./manage.sh build
```

### 3. Uruchom wszystkie serwisy

```bash
./manage.sh deploy
```

Skrypt automatycznie:
- uruchomi PostgreSQL w Dockerze
- stworzy bazy danych `cs2_players`, `cs2_matches`, `cs2_ranking`
- uruchomi wszystkie 3 serwisy Spring Boot
- sprawdzi status każdego serwisu

### 4. Zatrzymaj serwisy

```bash
./manage.sh stop
```

### Pozostałe komendy

| Komenda | Opis |
|--------|------|
| `./manage.sh build` | kompiluje wszystkie moduły |
| `./manage.sh deploy` | uruchamia PostgreSQL i wszystkie serwisy |
| `./manage.sh stop` | zatrzymuje serwisy i Docker |
| `./manage.sh clean` | usuwa build i wolumeny Docker |

## API

### Player Service — `http://localhost:8081`

| Metoda | Endpoint | Opis |
|--------|----------|------|
| POST | `/players` | Utwórz gracza |
| GET | `/players` | Lista graczy |
| GET | `/players/{id}` | Pobierz gracza |

**Przykład — utwórz gracza:**
```bash
curl -X POST http://localhost:8081/players \
  -H "Content-Type: application/json" \
  -d '{"username": "s1mple"}'
```

**Odpowiedź:**
```json
{
  "id": 1,
  "username": "s1mple",
  "eloRating": 1000,
  "matchesPlayed": 0,
  "wins": 0,
  "losses": 0,
  "winRate": 0.0
}
```

### Match Service — `http://localhost:8082`

| Metoda | Endpoint | Opis |
|--------|----------|------|
| POST | `/matches` | Zarejestruj mecz |
| GET | `/matches` | Lista meczów |
| GET | `/matches/{id}` | Pobierz mecz |

**Przykład — zarejestruj mecz:**
```bash
curl -X POST http://localhost:8082/matches \
  -H "Content-Type: application/json" \
  -d '{"winnerTeamPlayerIds": [1, 2, 3, 4, 5], "loserTeamPlayerIds": [6, 7, 8, 9, 10]}'
```

### Ranking Service — `http://localhost:8083`

| Metoda | Endpoint | Opis |
|--------|----------|------|
| GET | `/leaderboard` | Tabela liderów posortowana po ELO |

**Przykład:**
```bash
curl http://localhost:8083/leaderboard
```

**Odpowiedź:**
```json
[
  {
    "position": 1,
    "playerId": 1,
    "username": "s1mple",
    "eloRating": 1016,
    "matchesPlayed": 1,
    "wins": 1,
    "losses": 0,
    "winRate": 100.0
  }
]
```

## System ELO

Ranking oparty na klasycznym algorytmie ELO z współczynnikiem K=32.

Wzór na oczekiwany wynik:
```
E = 1 / (1 + 10^((przeciwnik - gracz) / 400))
```

Nowy rating:
```
R_new = R_old + K * (wynik - E)
```

Gdzie `wynik = 1.0` dla zwycięzcy i `0.0` dla przegranego. Przy równych ratingach zwycięzca zyskuje +16, przegrany traci -16. Przy większej różnicy ratingów — mniejszy zysk za wygraną z słabszym przeciwnikiem.

## Technologie

- Java 21
- Spring Boot 3.3
- Spring Data JPA
- PostgreSQL 16
- Docker
- Maven (multi-module)
- JUnit 5 + Mockito