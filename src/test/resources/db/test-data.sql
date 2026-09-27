CREATE TABLE IF NOT EXISTS title(TitleID SERIAL PRIMARY KEY, Description TEXT, Date DATE);

INSERT INTO title(Description, Date)
VALUES ('Welcome to Lashes', '2026-08-23'),
       ('Not welcome to Lashes', '2026-08-21'),
       ('Welcome to blink....', '2026-08-20' );

CREATE TABLE IF NOT EXISTS treatments(TreatmentID SERIAL PRIMARY KEY, TreatmentName VARCHAR(255), TreatmentDescription TEXT);

INSERT INTO treatments(TreatmentName, TreatmentDescription)
VALUES ('Deluxe Lash Lift', 'Big ol lashes'),
       ('Korean Lashes', 'very korean'),
       ('Chinese Lashes', 'very chinese');

CREATE TABLE IF NOT EXISTS testaments(TestamentID SERIAL PRIMARY KEY, TreatmentDescription TEXT NOT NULL, CustomerName VARCHAR(255) NOT NULL, TreatmentID INTEGER NOT NULL);

INSERT INTO testaments(TreatmentDescription ,CustomerName,TreatmentID)
VALUES ('Awful lashes', 'Tom Campbell', 1),
       ('Amazing lashes', 'Sophie Steele', 2),
       ('Just fantastic lashes', 'Ellen Cooke', 3 );