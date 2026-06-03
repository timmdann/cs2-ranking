#!/bin/bash

set -e

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

MVN="/c/Program Files/JetBrains/IntelliJ IDEA 2025.3.3/plugins/maven/lib/maven3/bin/mvn"

case "$1" in
  stop)
    echo "Stopping Spring Boot services..."
    taskkill //F //IM java.exe 2>/dev/null || true
    echo "Stopping Docker containers..."
    docker-compose stop
    echo "Done."
    ;;

  clean)
    echo "Cleaning Maven build..."
    "$MVN" clean
    echo "Stopping and removing Docker containers and volumes..."
    taskkill //F //IM java.exe 2>/dev/null || true
    docker-compose down -v
    echo "Done."
    ;;

  build)
    echo "Building project..."
    "$MVN" clean install -DskipTests
    echo "Build complete."
    ;;

  deploy)
    mkdir -p "$SCRIPT_DIR/logs"

    echo "Starting PostgreSQL..."
    docker-compose up -d
    echo "Waiting for PostgreSQL to be ready..."
    sleep 5

    echo "Starting player-service on port 8081..."
    (cd "$SCRIPT_DIR/player-service" && "$MVN" spring-boot:run > "$SCRIPT_DIR/logs/player.log" 2>&1) &

    echo "Starting match-service on port 8082..."
    (cd "$SCRIPT_DIR/match-service" && "$MVN" spring-boot:run > "$SCRIPT_DIR/logs/match.log" 2>&1) &

    echo "Starting ranking-service on port 8083..."
    (cd "$SCRIPT_DIR/ranking-service" && "$MVN" spring-boot:run > "$SCRIPT_DIR/logs/ranking.log" 2>&1) &

    echo "Waiting for services to start..."
    sleep 15

    echo ""
    echo "Services status:"
    curl -s http://localhost:8081/players > /dev/null 2>&1 && echo "  ✓ player-service  -> http://localhost:8081" || echo "  ✗ player-service  -> FAILED (check logs/player.log)"
    curl -s http://localhost:8082/matches > /dev/null 2>&1 && echo "  ✓ match-service   -> http://localhost:8082" || echo "  ✗ match-service   -> FAILED (check logs/match.log)"
    curl -s http://localhost:8083/leaderboard > /dev/null 2>&1 && echo "  ✓ ranking-service -> http://localhost:8083" || echo "  ✗ ranking-service -> FAILED (check logs/ranking.log)"
    echo ""
    echo "Logs: logs/player.log, logs/match.log, logs/ranking.log"
    ;;

  *)
    echo "Usage: $0 {stop|clean|build|deploy}"
    echo ""
    echo "  stop    - stop Docker containers"
    echo "  clean   - clean Maven build and remove Docker volumes"
    echo "  build   - compile and package all modules"
    echo "  deploy  - start PostgreSQL and all 3 Spring Boot services"
    exit 1
    ;;
esac