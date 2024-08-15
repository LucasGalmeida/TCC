CREATE TABLE IF NOT EXISTS public.users (
    id SERIAL PRIMARY KEY,
    login VARCHAR(255) UNIQUE NOT NULL,
    name VARCHAR(255) NOT NULL,
    password VARCHAR(255) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_users_login ON public.users (login);
