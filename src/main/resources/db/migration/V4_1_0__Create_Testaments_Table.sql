CREATE TABLE IF NOT EXISTS public.testaments(
    TestamentID SERIAL PRIMARY KEY,
    TreatmentDescription TEXT NOT NULL,
    CustomerName VARCHAR(255) NOT NULL,
    TreatmentID INTEGER NOT NULL,
    CONSTRAINT fk_testament_treatment
        FOREIGN KEY (TreatmentID)
        REFERENCES public.treatments(TreatmentID)

);