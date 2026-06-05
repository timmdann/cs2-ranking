CREATE DATABASE cs2_players;
CREATE DATABASE cs2_matches;
CREATE DATABASE cs2_ranking;

\c cs2_players;

CREATE TABLE players (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(32) NOT NULL UNIQUE,
    elo_rating INT NOT NULL,
    matches_played INT NOT NULL DEFAULT 0,
    wins INT NOT NULL DEFAULT 0,
    losses INT NOT NULL DEFAULT 0
);

CREATE TABLE player_team_history (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT REFERENCES players(id),
    team_name VARCHAR(255) NOT NULL,
    joined_at TIMESTAMP NOT NULL,
    left_at TIMESTAMP
);

CREATE TABLE player_map_stats (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT REFERENCES players(id),
    map_name VARCHAR(255) NOT NULL,
    wins INT NOT NULL DEFAULT 0,
    losses INT NOT NULL DEFAULT 0
);

CREATE TABLE player_tournament_earnings (
    id BIGSERIAL PRIMARY KEY,
    player_id BIGINT REFERENCES players(id),
    tournament_name VARCHAR(255) NOT NULL,
    amount DECIMAL(19, 2) NOT NULL,
    currency VARCHAR(10) NOT NULL,
    date TIMESTAMP NOT NULL
);

\c cs2_matches;

CREATE TABLE matches (
    id BIGSERIAL PRIMARY KEY,
    map_name VARCHAR(255) NOT NULL,
    played_at TIMESTAMP NOT NULL
);

CREATE TABLE match_winner_players (
    match_id BIGINT REFERENCES matches(id),
    player_id BIGINT NOT NULL
);

CREATE TABLE match_loser_players (
    match_id BIGINT REFERENCES matches(id),
    player_id BIGINT NOT NULL
);

\c cs2_ranking;

CREATE TABLE rankings (
    player_id BIGINT PRIMARY KEY,
    username VARCHAR(32) NOT NULL,
    elo_rating INT NOT NULL,
    matches_played INT NOT NULL DEFAULT 0,
    wins INT NOT NULL DEFAULT 0,
    losses INT NOT NULL DEFAULT 0
);
