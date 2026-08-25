CREATE TABLE IF NOT EXISTS title(TitleID SERIAL PRIMARY KEY, Description TEXT, Date DATE);

INSERT INTO title(Description, Date)
VALUES ('Welcome to Lashes', '2026-08-23'),
       ('Not welcome to Lashes', '2026-08-21'),
       ('Welcome to blink....', '2026-08-20' );