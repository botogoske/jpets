-- Esquema do banco de dados JPets (SQLite)
-- Executado na inicializacao da aplicacao. Todas as tabelas usam
-- CREATE TABLE IF NOT EXISTS, portanto o script pode rodar varias vezes.

CREATE TABLE IF NOT EXISTS tutor (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nome            TEXT    NOT NULL,
    cpf             TEXT,
    email           TEXT,
    telefone        TEXT,
    data_nascimento TEXT,
    endereco        TEXT,
    bairro          TEXT,
    cidade          TEXT,
    data_cadastro   TEXT    NOT NULL,
    ativo           INTEGER NOT NULL DEFAULT 1
);

CREATE TABLE IF NOT EXISTS funcionario (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nome            TEXT    NOT NULL,
    cpf             TEXT,
    email           TEXT,
    telefone        TEXT,
    data_nascimento TEXT,
    data_admissao   TEXT,
    cargo           TEXT,
    salario         REAL,
    ativo           INTEGER NOT NULL DEFAULT 1
);

-- pet.tutor_id referencia tutor.id. ON DELETE RESTRICT devolve um erro se um tutor
-- com pets vinculados for excluido, reforcando a regra aplicada pela interface.
CREATE TABLE IF NOT EXISTS pet (
    id              INTEGER PRIMARY KEY AUTOINCREMENT,
    nome            TEXT    NOT NULL,
    especie         TEXT,
    raca            TEXT,
    sexo            TEXT,
    porte           TEXT,
    peso            REAL,
    data_nascimento TEXT,
    data_cadastro   TEXT    NOT NULL,
    observacoes     TEXT,
    tutor_id        INTEGER REFERENCES tutor (id) ON DELETE RESTRICT,
    ativo           INTEGER NOT NULL DEFAULT 1
);

CREATE INDEX IF NOT EXISTS idx_pet_tutor ON pet (tutor_id);
CREATE INDEX IF NOT EXISTS idx_pet_nome ON pet (nome);
CREATE INDEX IF NOT EXISTS idx_tutor_ativo ON tutor (ativo);
CREATE INDEX IF NOT EXISTS idx_funcionario_ativo ON funcionario (ativo);
