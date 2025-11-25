-- ==========================
-- LIMPEZA (CUIDADO EM PRODUÇÃO)
-- ==========================
DROP TABLE IF EXISTS public.user_value_history CASCADE;
DROP TABLE IF EXISTS public.bet_result CASCADE;
DROP TABLE IF EXISTS public.gamble CASCADE;
DROP TABLE IF EXISTS public.bet CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;

-- ==========================
-- TABELA USERS
-- ==========================
CREATE TABLE public.users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,
    password VARCHAR(150) NOT NULL,
    role VARCHAR(50) NOT NULL DEFAULT 'user',
    is_logged BOOLEAN NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT NOW(),
    valuetotal BIGINT DEFAULT 0
);

CREATE INDEX idx_users_email ON public.users(email);

-- ==========================
-- TABELA BET (ATUALIZADA)
-- ==========================
CREATE TABLE public.bet (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    finalized BOOLEAN DEFAULT FALSE,

    -- CAMPOS ADICIONADOS
    profit NUMERIC(10,2) NOT NULL DEFAULT 0,
    loss NUMERIC(10,2) NOT NULL DEFAULT 0,
    closed_at TIMESTAMP NULL,

    CONSTRAINT fk_bet_user FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_bet_user_id ON public.bet(user_id);

-- ==========================
-- TABELA GAMBLE
-- ==========================
CREATE TABLE public.gamble (
    id SERIAL PRIMARY KEY,
    bet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    value_guess INTEGER NOT NULL CHECK (value_guess BETWEEN 2 AND 12),
    value_money REAL NOT NULL CHECK (value_money > 0),
    created_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_gamble_bet FOREIGN KEY (bet_id)
        REFERENCES public.bet(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT fk_gamble_user FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,

    CONSTRAINT unique_user_bet UNIQUE (bet_id, user_id)
);

CREATE INDEX idx_gamble_bet_id ON public.gamble(bet_id);
CREATE INDEX idx_gamble_user_id ON public.gamble(user_id);

-- ==========================
-- TABELA BET_RESULT
-- ==========================
CREATE TABLE public.bet_result (
    id SERIAL PRIMARY KEY,
    bet_id BIGINT NOT NULL UNIQUE,
    result_value INTEGER CHECK (result_value BETWEEN 2 AND 12),
    winner_id BIGINT,
    created_at TIMESTAMP DEFAULT NOW(),

    CONSTRAINT fk_result_bet FOREIGN KEY (bet_id)
        REFERENCES public.bet(id)
        ON DELETE CASCADE,

    CONSTRAINT fk_result_winner FOREIGN KEY (winner_id)
        REFERENCES public.users(id)
        ON DELETE SET NULL
);

-- ==========================
-- TABELA USER_VALUE_HISTORY (CORRIGIDA)
-- ==========================
CREATE TABLE public.user_value_history (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,
    old_value BIGINT NOT NULL,
    new_value BIGINT NOT NULL,

    -- >>>>>>> ADICIONADO: DIFERENÇA ENTRE VALORES
    diff BIGINT NOT NULL DEFAULT 0,

    changed_at TIMESTAMP DEFAULT NOW(),
    reason VARCHAR(200),

    CONSTRAINT fk_uvh_user FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_uvh_user_id ON public.user_value_history(user_id);
