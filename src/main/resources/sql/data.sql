DELETE
FROM films;
DELETE
FROM users;

INSERT INTO films (DURATION, DESCRIPTION, NAME, RATING, RELEASE_DATE)
VALUES (100, 'test description 1', 'test film 1', 'R', '2023-01-01'),
       (80, 'test description 2', 'test film 2', 'PG', '2023-01-02'),
       (90, 'test description 3', 'test film 3', 'G', '2023-01-03');

INSERT INTO genres (NAME)
VALUES ('Комедия'),
       ('Драма'),
       ('Мультфильм'),
       ('Триллер'),
       ('Боевик');

INSERT INTO film_genres (FILM_ID, GENRE_ID)
VALUES (1, 1),
       (1, 2),
       (2, 3),
       (3, 4);

INSERT INTO users (EMAIL, LOGIN, NAME, BIRTHDAY)
VALUES ('test1@email.com', 'login1', 'user1', '1988-09-10'),
       ('test2@email.com', 'login2', 'user2', '1980-10-12'),
       ('test3@email.com', 'login3', 'user3', '1991-09-20');

INSERT INTO film_who_liked_users (FILM_ID, WHO_LIKED_USER_ID)
VALUES (2, 1),
       (3, 2);

INSERT INTO friendships (INITIATOR_ID, RECEIVER_ID, FRIENDSHIP_STATUS)
VALUES (1, 2, 'AGREED');


