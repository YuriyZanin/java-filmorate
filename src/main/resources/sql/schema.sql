CREATE TABLE IF NOT EXISTS films
(
    id           INTEGER PRIMARY KEY AUTO_INCREMENT,
    duration     INTEGER,
    description  VARCHAR,
    name         VARCHAR,
    rating       VARCHAR,
    RELEASE_DATE DATE,
    CHECK rating IN ('G', 'PG', 'PG-13', 'R', 'NC-17')
);

CREATE TABLE IF NOT EXISTS genres
(
    id   INTEGER PRIMARY KEY AUTO_INCREMENT,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS film_genres
(
    film_id  INTEGER,
    genre_id VARCHAR,
    PRIMARY KEY (film_id, genre_id),
    CONSTRAINT film_genre_idx UNIQUE (film_id, genre_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (genre_id) REFERENCES genres (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS users
(
    id       INTEGER PRIMARY KEY AUTO_INCREMENT,
    email    VARCHAR,
    login    VARCHAR,
    name     VARCHAR,
    birthday DATE
);

CREATE TABLE IF NOT EXISTS film_who_liked_users
(
    film_id           INTEGER,
    who_liked_user_id INTEGER,
    PRIMARY KEY (film_id, who_liked_user_id),
    CONSTRAINT film_user_idx UNIQUE (film_id, who_liked_user_id),
    FOREIGN KEY (film_id) REFERENCES films (id) ON DELETE CASCADE,
    FOREIGN KEY (who_liked_user_id) REFERENCES users (id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS friendships
(
    initiator_id      INTEGER,
    receiver_id       INTEGER,
    friendship_status VARCHAR,
    PRIMARY KEY (initiator_id, receiver_id),
    FOREIGN KEY (initiator_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (receiver_id) REFERENCES users (id) ON DELETE CASCADE,
    CHECK friendship_status IN ('AGREED', 'NOT_AGREED'),
    CHECK initiator_id <> receiver_id
);