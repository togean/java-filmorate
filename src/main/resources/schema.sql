DROP TABLE IF EXISTS genres, rates, films, users, films_users, users_friends, statuses, films_genres;

CREATE TABLE IF NOT EXISTS statuses(
status_id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
statusname VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS genres (
            genre_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            genrename VARCHAR(255) NOT NULL
          );

CREATE TABLE IF NOT EXISTS rates (
            rate_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            ratename VARCHAR(255) NOT NULL
          );

CREATE TABLE IF NOT EXISTS films (
film_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
name VARCHAR(255) NOT NULL,
film_releasedate DATE NOT NULL,
description VARCHAR(255) NOT NULL,
duration INTEGER NOT NULL,
rateid INTEGER REFERENCES rates(rate_id)
);

CREATE TABLE IF NOT EXISTS users (
            user_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
            name VARCHAR(255) NOT NULL,
            email VARCHAR(255) NOT NULL,
            login VARCHAR(255) NOT NULL,
            birthday DATE NOT NULL
          );

CREATE TABLE IF NOT EXISTS films_users (
film_id INTEGER NOT NULL REFERENCES films(film_id),
user_id INTEGER NOT NULL REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS films_genres (
film_id INTEGER NOT NULL REFERENCES films(film_id),
genre_id INTEGER NOT NULL REFERENCES genres(genre_id)
);

CREATE TABLE IF NOT EXISTS users_friends (
user_id INTEGER NOT NULL REFERENCES users(user_id),
friend_id INTEGER NOT NULL REFERENCES users(user_id),
status_id INTEGER NOT NULL REFERENCES statuses(status_id)
);