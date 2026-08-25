ALTER TABLE public.title
    DROP COLUMN TitleID,
    DROP COLUMN ImageID,
    DROP COLUMN ImageName;

ALTER TABLE public.title
    ADD COLUMN TitleID SERIAL PRIMARY KEY;


