-- ============================================
-- LIMPEZA (caso já existam tabelas)
-- ============================================
DROP TABLE IF EXISTS public.bet_result CASCADE;
DROP TABLE IF EXISTS public.gamble CASCADE;
DROP TABLE IF EXISTS public.bet CASCADE;
DROP TABLE IF EXISTS public.users CASCADE;

-- ============================================
-- TABELA: USERS
-- ============================================
CREATE TABLE public.users (
    id SERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(150) NOT NULL UNIQUE,          -- RN1: Usuário único por e-mail
    password VARCHAR(150) NOT NULL,              -- RN2: Segurança da conta
    role VARCHAR(50) NOT NULL DEFAULT 'user',
    is_logged BOOLEAN NOT NULL DEFAULT FALSE,    -- ✅ NOVA COLUNA: status de login
    created_at TIMESTAMP DEFAULT NOW()
);

CREATE INDEX idx_users_email ON public.users(email);

-- ============================================
-- TABELA: BET (Mesa de Aposta)
-- ============================================
CREATE TABLE public.bet (
    id SERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL,                     -- RN3: Uma aposta é criada por um usuário
    name VARCHAR(100) NOT NULL,
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_bet_user FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE
);

CREATE INDEX idx_bet_user_id ON public.bet(user_id);

-- ============================================
-- TABELA: GAMBLE (Lances / Palpites)
-- ============================================
CREATE TABLE public.gamble (
    id SERIAL PRIMARY KEY,
    bet_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    value_guess INTEGER NOT NULL CHECK (value_guess BETWEEN 2 AND 12),  -- RN8
    value_money REAL NOT NULL CHECK (value_money > 0),                  -- RN5
    created_at TIMESTAMP DEFAULT NOW(),
    CONSTRAINT fk_gamble_bet FOREIGN KEY (bet_id)
        REFERENCES public.bet(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT fk_gamble_user FOREIGN KEY (user_id)
        REFERENCES public.users(id)
        ON DELETE CASCADE
        ON UPDATE CASCADE,
    CONSTRAINT unique_user_bet UNIQUE (bet_id, user_id)                 -- RN4: um lance por aposta/usuário
);

CREATE INDEX idx_gamble_bet_id ON public.gamble(bet_id);
CREATE INDEX idx_gamble_user_id ON public.gamble(user_id);

-- ============================================
-- TABELA: BET_RESULT (Resultado Final da Aposta)
-- ============================================
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

-- ============================================
-- TRIGGER: Limite de Participantes (2 a 11) por Aposta — RN7
-- ============================================
CREATE OR REPLACE FUNCTION check_gamble_limit()
RETURNS TRIGGER AS $$
DECLARE
    num_participantes INT;
BEGIN
    SELECT COUNT(*) INTO num_participantes FROM public.gamble WHERE bet_id = NEW.bet_id;

    IF num_participantes >= 11 THEN
        RAISE EXCEPTION 'Limite máximo de 11 participantes atingido para esta aposta.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_gamble_limit
BEFORE INSERT ON public.gamble
FOR EACH ROW
EXECUTE FUNCTION check_gamble_limit();

-- ============================================
-- TRIGGER: Impedir aposta com menos de 2 jogadores
-- (validação antes de registrar resultado)
-- ============================================
CREATE OR REPLACE FUNCTION check_min_players_before_result()
RETURNS TRIGGER AS $$
DECLARE
    num_participantes INT;
BEGIN
    SELECT COUNT(*) INTO num_participantes FROM public.gamble WHERE bet_id = NEW.bet_id;

    IF num_participantes < 2 THEN
        RAISE EXCEPTION 'Uma aposta precisa ter pelo menos 2 participantes antes de registrar o resultado.';
    END IF;

    RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER trg_check_min_players_before_result
BEFORE INSERT ON public.bet_result
FOR EACH ROW
EXECUTE FUNCTION check_min_players_before_result();

-- ============================================
-- CONSULTAS ÚTEIS
-- ============================================

-- 1. Ver apostas disponíveis
-- SELECT b.id, b.name AS aposta, u.name AS criador, b.created_at
-- FROM bet b
-- JOIN users u ON b.user_id = u.id
-- ORDER BY b.created_at DESC;

-- 2. Ver histórico de lances de um usuário
-- SELECT g.id, b.name AS aposta, g.value_guess, g.value_money, g.created_at
-- FROM gamble g
-- JOIN bet b ON g.bet_id = b.id
-- WHERE g.user_id = :user_id;

-- 3. Mostrar resultado final da aposta
-- SELECT b.name AS aposta, r.result_value AS resultado, u.name AS vencedor
-- FROM bet_result r
-- JOIN bet b ON r.bet_id = b.id
-- LEFT JOIN users u ON r.winner_id = u.id;
