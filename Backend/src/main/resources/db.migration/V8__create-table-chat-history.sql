CREATE TABLE IF NOT EXISTS public.chat_history (
    id SERIAL PRIMARY KEY,
    type VARCHAR(50) NOT NULL,
    title VARCHAR(100) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    message TEXT NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_chat_history_user_id FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE INDEX IF NOT EXISTS idx_chat_history_id ON public.chat_history (id);