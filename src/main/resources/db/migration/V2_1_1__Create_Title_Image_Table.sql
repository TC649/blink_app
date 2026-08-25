CREATE TABLE IF NOT EXISTS public.title_image (
            ImageID SERIAL PRIMARY KEY,
            ImageName VARCHAR(255) NOT NULL,
            Date DATE NOT NULL
);