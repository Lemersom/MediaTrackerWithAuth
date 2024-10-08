INSERT INTO users(ID, USER_NAME, PASSWORD, ROLE)
VALUES (199, 'admin', '$2a$10$K5mH67Mk1.LJWvmxyQdHt.kkOfdtIFD2evSB2JurVHiBYaRNqmM8e', 'ADMIN');

INSERT INTO users(ID, USER_NAME, PASSWORD, ROLE)
VALUES (188, 'user', '$2a$10$D0w7RGRj.36qVznXxToDauL7qQ/h5/eHdVjONcMgUK.3MYRg8k7Z.', 'USER');

INSERT INTO media_type(ID, NAME)
VALUES (99, 'Game');

INSERT INTO media_type(ID, NAME)
VALUES (88, 'Movie');

INSERT INTO media_type(ID, NAME)
VALUES (77, 'Visual Novel');

INSERT INTO media_item(ID, TITLE, RATING, START_DATE, FINISH_DATE, STATUS, MEDIA_TYPE_ID, NOTES)
VALUES (11, 'Baldurs Gate 3', null, '2023-11-15', null, 'IN_PROGRESS', 99, null);

INSERT INTO media_item(ID, TITLE, RATING, START_DATE, FINISH_DATE, STATUS, MEDIA_TYPE_ID, NOTES)
VALUES (35, 'Portal 1', 10, '2024-02-10', '2024-02-14', 'COMPLETED', 99, 'The Cake is a Lie');

INSERT INTO media_item(ID, TITLE, RATING, START_DATE, FINISH_DATE, STATUS, MEDIA_TYPE_ID, NOTES)
VALUES (25, 'Portal 2', 10, '2024-02-21', '2024-02-25', 'COMPLETED', 99, null);

INSERT INTO media_item(ID, TITLE, RATING, START_DATE, FINISH_DATE, STATUS, MEDIA_TYPE_ID, NOTES)
VALUES (65, 'Mad Max', 10, null, null, 'COMPLETED', 88, null);