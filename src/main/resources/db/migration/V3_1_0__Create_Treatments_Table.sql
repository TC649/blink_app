CREATE TABLE IF NOT EXISTS public.treatments (
    TreatmentID SERIAL PRIMARY KEY,
    TreatmentName VARCHAR(255) NOT NULL,
    TreatmentDescription TEXT NOT NULL
);