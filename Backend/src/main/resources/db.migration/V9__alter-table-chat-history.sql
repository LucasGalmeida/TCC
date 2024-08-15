DROP TABLE IF EXISTS public.chat_history;

CREATE TABLE IF NOT EXISTS public.chat (
    id SERIAL PRIMARY KEY,
    title VARCHAR(100) NOT NULL,
    user_id INTEGER NOT NULL,
    CONSTRAINT fk_chat_user_id FOREIGN KEY (user_id) REFERENCES public.users(id)
);

CREATE TABLE IF NOT EXISTS public.chat_history (
    id SERIAL PRIMARY KEY,
    chat_id INTEGER NOT NULL,
    type VARCHAR(50) NOT NULL,
    date TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    message TEXT NOT NULL,
    CONSTRAINT fk_chat_history_chat_id FOREIGN KEY (chat_id) REFERENCES public.chat(id)
);

CREATE INDEX IF NOT EXISTS idx_chat_id ON public.chat (id);
CREATE INDEX IF NOT EXISTS idx_chat_history_chat_id ON public.chat_history (chat_id);
