CREATE USER IF NOT EXISTS 'user'@'localhost' IDENTIFIED BY 'mypass';
GRANT ALL PRIVILEGES ON *.* TO 'user'@'localhost';

DROP DATABASE IF EXISTS dssrestaurantes;
CREATE DATABASE dssrestaurantes;
USE dssrestaurantes;

CREATE TABLE restaurantes (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    morada VARCHAR(100) NOT NULL
);

CREATE TABLE indicadores (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    faturacaoTotal DECIMAL(10,2) NOT NULL,
    tempoMedioAtendimento TIME NOT NULL,
    numPedidos INT NOT NULL,
    FOREIGN KEY (id) REFERENCES restaurantes(id)
);

CREATE TABLE users (
    email VARCHAR(50) NOT NULL PRIMARY KEY,
    password VARCHAR(100) NOT NULL,
    nome VARCHAR(100) NOT NULL,
    cargo VARCHAR(20) NOT NULL
);

CREATE TABLE userRestaurante (
    email VARCHAR(50) NOT NULL,
    idRestaurante VARCHAR(10) NOT NULL,
    PRIMARY KEY (email, idRestaurante),
    FOREIGN KEY (email) REFERENCES users(email),
    FOREIGN KEY (idRestaurante) REFERENCES restaurantes(id)
);

CREATE TABLE mensagens (
    id VARCHAR(10) PRIMARY KEY,
    conteudo VARCHAR(500) NOT NULL,
    autor VARCHAR(50) NOT NULL,
    timestampEnvio TIMESTAMP NOT NULL,
    idRestaurante VARCHAR(10) NOT NULL,
    FOREIGN KEY (autor) REFERENCES users(email),
    FOREIGN KEY (idRestaurante) REFERENCES restaurantes(id)
);

CREATE TABLE postos (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    funcao VARCHAR(100) NOT NULL,
    idRestaurante VARCHAR(10) NOT NULL,
    FOREIGN KEY (idRestaurante) REFERENCES restaurantes(id)
);

CREATE TABLE stock (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    idRestaurante VARCHAR(10) NOT NULL,
    FOREIGN KEY (idRestaurante) REFERENCES restaurantes(id)
);

CREATE TABLE ingredientes (
    id VARCHAR(100) NOT NULL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    alergenio BOOLEAN NOT NULL
);

CREATE TABLE stockIngrediente (
    idStock VARCHAR(10) NOT NULL,
    idIngrediente VARCHAR(100) NOT NULL,
    quantidadeMin INT NOT NULL,
    quantidadeAtual INT NOT NULL,
    quantidadeRec INT NOT NULL,
    PRIMARY KEY (idStock, idIngrediente),
    FOREIGN KEY (idStock) REFERENCES stock(id),
    FOREIGN KEY (idIngrediente) REFERENCES ingredientes(id)
);

CREATE TABLE solicitacoes (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    quantidade INT NOT NULL,
    data_solicitacao TIMESTAMP NOT NULL,
    data_reposicao_prevista TIMESTAMP,
    data_reposicao TIMESTAMP,
    idIngrediente VARCHAR(100) NOT NULL,
    idStock VARCHAR(10) NOT NULL,
    idPosto VARCHAR(10) NOT NULL,
    FOREIGN KEY (idIngrediente) REFERENCES ingredientes(id),
    FOREIGN KEY (idStock) REFERENCES stock(id),
    FOREIGN KEY (idPosto) REFERENCES postos(id)
);

CREATE TABLE pedidos (
    numero INT NOT NULL PRIMARY KEY,
    precoTotal DECIMAL(10,2),
    tempoPreparacao INT,
    timestamp TIMESTAMP,
    localEntrega VARCHAR(100),
    estado VARCHAR(20),
    nota VARCHAR(500),
    idRestaurante VARCHAR(10) NOT NULL,
    balcao INT,
    FOREIGN KEY (idRestaurante) REFERENCES restaurantes(id)
);

CREATE TABLE produtos (
    id VARCHAR(10) NOT NULL PRIMARY KEY,
    nome VARCHAR(100) NOT NULL,
    precoBase DECIMAL(10,2) NOT NULL,
    tempoPreparacao INT NOT NULL
);

CREATE TABLE menus (
    idProduto VARCHAR(10) NOT NULL PRIMARY KEY,
    desconto DECIMAL(5,2) NOT NULL,
    FOREIGN KEY (idProduto) REFERENCES produtos(id)
);

CREATE TABLE itens (
    idProduto VARCHAR(10) NOT NULL,
    PRIMARY KEY (idProduto),
    FOREIGN KEY (idProduto) REFERENCES produtos(id)
);

CREATE TABLE menuItem (
    idMenu VARCHAR(10) NOT NULL,
    idItem VARCHAR(10) NOT NULL,
    PRIMARY KEY (idMenu, idItem),
    FOREIGN KEY (idMenu) REFERENCES menus(idProduto),
    FOREIGN KEY (idItem) REFERENCES itens(idProduto)
);

CREATE TABLE itemIngrediente (
    idItem VARCHAR(10) NOT NULL,
    idIngrediente VARCHAR(100) NOT NULL,
    PRIMARY KEY (idItem, idIngrediente),
    FOREIGN KEY (idItem) REFERENCES itens(idProduto),
    FOREIGN KEY (idIngrediente) REFERENCES ingredientes(id)
);

CREATE TABLE pedidoproduto (
    numeroPedido INT NOT NULL,
    idProduto VARCHAR(10) NOT NULL,
    quantidade INT NOT NULL,
    PRIMARY KEY (numeroPedido, idProduto),
    FOREIGN KEY (numeroPedido) REFERENCES pedidos(numero),
    FOREIGN KEY (idProduto) REFERENCES produtos(id)
);

CREATE TABLE pagamentos (
    numeroPedido INT NOT NULL,
    precoTotal DECIMAL(10,2) NOT NULL,
    valor DECIMAL(10,2) NOT NULL,
    metodo VARCHAR(50) NOT NULL,
    troco DECIMAL(10,2),
    timestamp TIMESTAMP NOT NULL,
    fatura VARCHAR(100),
    PRIMARY KEY (numeroPedido),
    FOREIGN KEY (numeroPedido) REFERENCES pedidos(numero)
);

-- povoamento 
INSERT INTO restaurantes (id, morada) VALUES 
('REST01', 'Braga - Campus Gualtar'),
('REST02', 'Porto - Avenida dos Aliados'),
('REST03', 'Lisboa - Parque das Nações'),
('REST04', 'Coimbra - Alta Universitária'),
('REST05', 'Faro - Marina'),
('REST06', 'Aveiro - Centro Comercial Glicínias'),
('REST07', 'Viseu - Palácio do Gelo'),
('REST08', 'Guimarães - Largo da Oliveira'),
('REST09', 'Évora - Praça do Giraldo'),
('REST10', 'Leiria - Shopping Leiria');

-- 2. Inserir Indicadores (Dados fictícios variados)
INSERT INTO indicadores (id, faturacaoTotal, tempoMedioAtendimento, numPedidos) VALUES 
('REST01', 15430.50, 12.5, 1250),
('REST02', 32100.00, 9.2, 2800),
('REST03', 45200.00, 14.5, 3500),
('REST04', 18500.00, 11.0, 1600),
('REST05', 21000.00, 10.5, 1900),
('REST06', 16800.00, 13.0, 1400),
('REST07', 14200.00, 11.8, 1100),
('REST08', 19300.00, 10.2, 1750),
('REST09', 12500.00, 15.0, 950),
('REST10', 20150.00, 10.8, 1820);

-- 3. Inserir Users (COO e Chefes de Restaurante)
INSERT INTO users (email, password, nome, cargo) VALUES 
-- O Grande Chefe (COO)
('ana.silva@gmail.com', '1234', 'Ana Silva', 'COO'),

-- Chefes de Restaurante (Um para cada restaurante)
('joao.santos@gmail.com', '1234', 'João Santos', 'ChefeRestaurante'),
('maria.costa@gmail.com', '1234', 'Maria Costa', 'ChefeRestaurante'),
('pedro.almeida@gmail.com', '1234', 'Pedro Almeida', 'ChefeRestaurante'),
('sofia.oliveira@gmail.com', '1234', 'Sofia Oliveira', 'ChefeRestaurante'),
('tiago.martins@gmail.com', '1234', 'Tiago Martins', 'ChefeRestaurante'),
('beatriz.ferreira@gmail.com', '1234', 'Beatriz Ferreira', 'ChefeRestaurante'),
('ricardo.pereira@gmail.com', '1234', 'Ricardo Pereira', 'ChefeRestaurante'),
('catarina.rodrigues@gmail.com', '1234', 'Catarina Rodrigues', 'ChefeRestaurante'),
('miguel.gomes@gmail.com', '1234', 'Miguel Gomes', 'ChefeRestaurante'),
('lara.cardoso@gmail.com', '1234', 'Lara Cardoso', 'ChefeRestaurante');

-- 4. Definir Permissões (Quem acede a quê)

-- Permissões dos Chefes (Cada um acede apenas ao seu REST)
INSERT INTO userRestaurante (email, idRestaurante) VALUES 
('joao.santos@gmail.com', 'REST01'),
('maria.costa@gmail.com', 'REST02'),
('pedro.almeida@gmail.com', 'REST03'),
('sofia.oliveira@gmail.com', 'REST04'),
('tiago.martins@gmail.com', 'REST05'),
('beatriz.ferreira@gmail.com', 'REST06'),
('ricardo.pereira@gmail.com', 'REST07'),
('catarina.rodrigues@gmail.com', 'REST08'),
('miguel.gomes@gmail.com', 'REST09'),
('lara.cardoso@gmail.com', 'REST10');

-- Permissões do COO (Acede a TODOS os restaurantes automaticamente)
-- Este comando insere uma linha para cada restaurante existente associando ao email da Ana Silva
INSERT INTO userRestaurante (email, idRestaurante)
SELECT 'ana.silva@gmail.com', id FROM restaurantes;

-- 5. Stock (um por restaurante)
INSERT INTO stock (id, idRestaurante) VALUES 
('STK-REST01', 'REST01'),
('STK-REST02', 'REST02'),
('STK-REST03', 'REST03'),
('STK-REST04', 'REST04'),
('STK-REST05', 'REST05'),
('STK-REST06', 'REST06'),
('STK-REST07', 'REST07'),
('STK-REST08', 'REST08'),
('STK-REST09', 'REST09'),
('STK-REST10', 'REST10');

-- 6. Ingredientes (fast food)
INSERT INTO ingredientes (id, nome, alergenio) VALUES 
    ('ING001', 'Pão de Hambúrguer', TRUE),
    ('ING002', 'Carne de Vaca (hambúrguer)', FALSE),
    ('ING003', 'Queijo Cheddar', TRUE),
    ('ING004', 'Alface', FALSE),
    ('ING005', 'Tomate', FALSE),
    ('ING006', 'Cebola', FALSE),
    ('ING007', 'Ketchup', TRUE),
    ('ING008', 'Maionese', TRUE),
    ('ING009', 'Bacon', TRUE),
    ('ING010', 'Frango (filete)', FALSE),
    ('ING011', 'Batata Frita', FALSE),
    ('ING012', 'Pão de Cachorro', TRUE),
    ('ING013', 'Salsicha', TRUE),
    ('ING014', 'Refrigerante Cola', FALSE),
    ('ING015', 'Refrigerante Laranja', FALSE),
    ('ING016', 'Água', FALSE),
    ('ING017', 'Café', FALSE),
    ('ING018', 'Leite', TRUE),
    ('ING019', 'Açúcar', FALSE),
    ('ING020', 'Gelo', FALSE);

INSERT INTO stockIngrediente (idStock, idIngrediente, quantidadeMin, quantidadeAtual, quantidadeRec) VALUES 
    ('STK-REST01', 'ING001', 100, 150, 200), ('STK-REST01', 'ING002', 80, 120, 150),
    ('STK-REST01', 'ING003', 60, 90, 120), ('STK-REST01', 'ING004', 40, 60, 80),
    ('STK-REST01', 'ING005', 30, 45, 60), ('STK-REST01', 'ING006', 25, 40, 50),
    ('STK-REST01', 'ING007', 50, 80, 100), ('STK-REST01', 'ING008', 50, 75, 100),
    ('STK-REST01', 'ING009', 30, 45, 60), ('STK-REST01', 'ING010', 70, 105, 140),
    ('STK-REST01', 'ING011', 200, 300, 400), ('STK-REST01', 'ING012', 50, 75, 100),
    ('STK-REST01', 'ING013', 40, 60, 80), ('STK-REST01', 'ING014', 100, 150, 200),
    ('STK-REST01', 'ING015', 100, 150, 200), ('STK-REST01', 'ING016', 100, 150, 200),
    ('STK-REST01', 'ING017', 50, 75, 100), ('STK-REST01', 'ING018', 30, 45, 60),
    ('STK-REST01', 'ING019', 20, 30, 40), ('STK-REST01', 'ING020', 10, 15, 20),

    -- Restaurante 2 (REST02 - Porto)
    ('STK-REST02', 'ING001', 120, 180, 240), ('STK-REST02', 'ING002', 100, 150, 200),
    ('STK-REST02', 'ING003', 70, 105, 140), ('STK-REST02', 'ING004', 50, 75, 100),
    ('STK-REST02', 'ING005', 40, 60, 80), ('STK-REST02', 'ING006', 30, 45, 60),
    ('STK-REST02', 'ING007', 60, 90, 120), ('STK-REST02', 'ING008', 55, 85, 110),
    ('STK-REST02', 'ING009', 45, 65, 90), ('STK-REST02', 'ING010', 90, 130, 180),
    ('STK-REST02', 'ING011', 250, 350, 500), ('STK-REST02', 'ING012', 60, 90, 120),
    ('STK-REST02', 'ING013', 50, 75, 100), ('STK-REST02', 'ING014', 120, 180, 240),
    ('STK-REST02', 'ING015', 120, 180, 240), ('STK-REST02', 'ING016', 120, 180, 240),
    ('STK-REST02', 'ING017', 60, 90, 120), ('STK-REST02', 'ING018', 40, 60, 80),
    ('STK-REST02', 'ING019', 30, 45, 60), ('STK-REST02', 'ING020', 15, 25, 35),

    -- Restaurante 3 (REST03 - Lisboa)
    ('STK-REST03', 'ING001', 90, 135, 180), ('STK-REST03', 'ING002', 75, 110, 150),
    ('STK-REST03', 'ING003', 50, 75, 100), ('STK-REST03', 'ING004', 35, 50, 70),
    ('STK-REST03', 'ING005', 30, 45, 60), ('STK-REST03', 'ING006', 25, 40, 50),
    ('STK-REST03', 'ING007', 40, 60, 80), ('STK-REST03', 'ING008', 45, 65, 90),
    ('STK-REST03', 'ING009', 35, 50, 70), ('STK-REST03', 'ING010', 90, 130, 180),
    ('STK-REST03', 'ING011', 180, 270, 360), ('STK-REST03', 'ING012', 40, 60, 80),
    ('STK-REST03', 'ING013', 35, 50, 70), ('STK-REST03', 'ING014', 90, 135, 180),
    ('STK-REST03', 'ING015', 90, 135, 180), ('STK-REST03', 'ING016', 90, 135, 180),
    ('STK-REST03', 'ING017', 50, 75, 100), ('STK-REST03', 'ING018', 30, 45, 60),
    ('STK-REST03', 'ING019', 20, 30, 40), ('STK-REST03', 'ING020', 10, 15, 20),

    -- Restaurante 4 (REST04 - Coimbra)
    ('STK-REST04', 'ING001', 85, 130, 170), ('STK-REST04', 'ING002', 70, 105, 140),
    ('STK-REST04', 'ING003', 55, 85, 110), ('STK-REST04', 'ING004', 45, 65, 90),
    ('STK-REST04', 'ING005', 35, 50, 70), ('STK-REST04', 'ING006', 30, 45, 60),
    ('STK-REST04', 'ING007', 55, 85, 110), ('STK-REST04', 'ING008', 60, 90, 120),
    ('STK-REST04', 'ING009', 40, 60, 80), ('STK-REST04', 'ING010', 80, 120, 160),
    ('STK-REST04', 'ING011', 220, 330, 440), ('STK-REST04', 'ING012', 55, 85, 110),
    ('STK-REST04', 'ING013', 45, 65, 90), ('STK-REST04', 'ING014', 110, 165, 220),
    ('STK-REST04', 'ING015', 110, 165, 220), ('STK-REST04', 'ING016', 110, 165, 220),
    ('STK-REST04', 'ING017', 55, 85, 110), ('STK-REST04', 'ING018', 35, 50, 70),
    ('STK-REST04', 'ING019', 25, 40, 50), ('STK-REST04', 'ING020', 12, 18, 25),

    -- Restaurante 5 (REST05 - Faro)
    ('STK-REST05', 'ING001', 75, 110, 150), ('STK-REST05', 'ING002', 60, 90, 120),
    ('STK-REST05', 'ING003', 45, 65, 90), ('STK-REST05', 'ING004', 35, 50, 70),
    ('STK-REST05', 'ING005', 25, 40, 50), ('STK-REST05', 'ING006', 20, 30, 40),
    ('STK-REST05', 'ING007', 45, 65, 90), ('STK-REST05', 'ING008', 50, 75, 100),
    ('STK-REST05', 'ING009', 35, 50, 70), ('STK-REST05', 'ING010', 70, 105, 140),
    ('STK-REST05', 'ING011', 180, 270, 360), ('STK-REST05', 'ING012', 45, 65, 90),
    ('STK-REST05', 'ING013', 35, 50, 70), ('STK-REST05', 'ING014', 95, 140, 190),
    ('STK-REST05', 'ING015', 95, 140, 190), ('STK-REST05', 'ING016', 95, 140, 190),
    ('STK-REST05', 'ING017', 45, 65, 90), ('STK-REST05', 'ING018', 30, 45, 60),
    ('STK-REST05', 'ING019', 20, 30, 40), ('STK-REST05', 'ING020', 10, 15, 20),

    -- Restaurante 6 (REST06 - Aveiro)
    ('STK-REST06', 'ING001', 95, 140, 190), ('STK-REST06', 'ING002', 85, 130, 170),
    ('STK-REST06', 'ING003', 65, 95, 130), ('STK-REST06', 'ING004', 55, 85, 110),
    ('STK-REST06', 'ING005', 45, 65, 90), ('STK-REST06', 'ING006', 40, 60, 80),
    ('STK-REST06', 'ING007', 65, 95, 130), ('STK-REST06', 'ING008', 70, 105, 140),
    ('STK-REST06', 'ING009', 50, 75, 100), ('STK-REST06', 'ING010', 95, 140, 190),
    ('STK-REST06', 'ING011', 240, 360, 480), ('STK-REST06', 'ING012', 65, 95, 130),
    ('STK-REST06', 'ING013', 55, 85, 110), ('STK-REST06', 'ING014', 130, 195, 260),
    ('STK-REST06', 'ING015', 130, 195, 260), ('STK-REST06', 'ING016', 130, 195, 260),
    ('STK-REST06', 'ING017', 65, 95, 130), ('STK-REST06', 'ING018', 45, 65, 90),
    ('STK-REST06', 'ING019', 35, 50, 70), ('STK-REST06', 'ING020', 18, 28, 38),

    -- Restaurante 7 (REST07 - Viseu)
    ('STK-REST07', 'ING001', 80, 120, 160), ('STK-REST07', 'ING002', 65, 95, 130),
    ('STK-REST07', 'ING003', 50, 75, 100), ('STK-REST07', 'ING004', 40, 60, 80),
    ('STK-REST07', 'ING005', 30, 45, 60), ('STK-REST07', 'ING006', 25, 40, 50),
    ('STK-REST07', 'ING007', 50, 75, 100), ('STK-REST07', 'ING008', 55, 85, 110),
    ('STK-REST07', 'ING009', 38, 55, 75), ('STK-REST07', 'ING010', 75, 110, 150),
    ('STK-REST07', 'ING011', 190, 285, 380), ('STK-REST07', 'ING012', 50, 75, 100),
    ('STK-REST07', 'ING013', 40, 60, 80), ('STK-REST07', 'ING014', 105, 155, 210),
    ('STK-REST07', 'ING015', 105, 155, 210), ('STK-REST07', 'ING016', 105, 155, 210),
    ('STK-REST07', 'ING017', 50, 75, 100), ('STK-REST07', 'ING018', 33, 50, 66),
    ('STK-REST07', 'ING019', 22, 33, 44), ('STK-REST07', 'ING020', 11, 17, 22),

    -- Restaurante 8 (REST08 - Guimarães)
    ('STK-REST08', 'ING001', 110, 165, 220), ('STK-REST08', 'ING002', 95, 140, 190),
    ('STK-REST08', 'ING003', 75, 110, 150), ('STK-REST08', 'ING004', 65, 95, 130),
    ('STK-REST08', 'ING005', 55, 85, 110), ('STK-REST08', 'ING006', 50, 75, 100),
    ('STK-REST08', 'ING007', 75, 110, 150), ('STK-REST08', 'ING008', 80, 120, 160),
    ('STK-REST08', 'ING009', 60, 90, 120), ('STK-REST08', 'ING010', 105, 155, 210),
    ('STK-REST08', 'ING011', 260, 390, 520), ('STK-REST08', 'ING012', 75, 110, 150),
    ('STK-REST08', 'ING013', 65, 95, 130), ('STK-REST08', 'ING014', 140, 210, 280),
    ('STK-REST08', 'ING015', 140, 210, 280), ('STK-REST08', 'ING016', 140, 210, 280),
    ('STK-REST08', 'ING017', 75, 110, 150), ('STK-REST08', 'ING018', 50, 75, 100),
    ('STK-REST08', 'ING019', 40, 60, 80), ('STK-REST08', 'ING020', 20, 30, 40),

    -- Restaurante 9 (REST09 - Évora)
    ('STK-REST09', 'ING001', 70, 105, 140), ('STK-REST09', 'ING002', 55, 85, 110),
    ('STK-REST09', 'ING003', 40, 60, 80), ('STK-REST09', 'ING004', 30, 45, 60),
    ('STK-REST09', 'ING005', 20, 30, 40), ('STK-REST09', 'ING006', 15, 25, 35),
    ('STK-REST09', 'ING007', 40, 60, 80), ('STK-REST09', 'ING008', 45, 65, 90),
    ('STK-REST09', 'ING009', 30, 45, 60), ('STK-REST09', 'ING010', 65, 95, 130),
    ('STK-REST09', 'ING011', 160, 240, 320), ('STK-REST09', 'ING012', 40, 60, 80),
    ('STK-REST09', 'ING013', 30, 45, 60), ('STK-REST09', 'ING014', 85, 130, 170),
    ('STK-REST09', 'ING015', 85, 130, 170), ('STK-REST09', 'ING016', 85, 130, 170),
    ('STK-REST09', 'ING017', 40, 60, 80), ('STK-REST09', 'ING018', 25, 40, 50),
    ('STK-REST09', 'ING019', 15, 25, 35), ('STK-REST09', 'ING020', 8, 12, 16),

    -- Restaurante 10 (REST10 - Leiria)
    ('STK-REST10', 'ING001', 100, 150, 200), ('STK-REST10', 'ING002', 90, 135, 180),
    ('STK-REST10', 'ING003', 70, 105, 140), ('STK-REST10', 'ING004', 60, 90, 120),
    ('STK-REST10', 'ING005', 50, 75, 100), ('STK-REST10', 'ING006', 45, 65, 90),
    ('STK-REST10', 'ING007', 70, 105, 140), ('STK-REST10', 'ING008', 75, 110, 150),
    ('STK-REST10', 'ING009', 55, 85, 110), ('STK-REST10', 'ING010', 100, 150, 200),
    ('STK-REST10', 'ING011', 230, 345, 460), ('STK-REST10', 'ING012', 70, 105, 140),
    ('STK-REST10', 'ING013', 60, 90, 120), ('STK-REST10', 'ING014', 120, 180, 240),
    ('STK-REST10', 'ING015', 120, 180, 240), ('STK-REST10', 'ING016', 120, 180, 240),
    ('STK-REST10', 'ING017', 70, 105, 140), ('STK-REST10', 'ING018', 48, 72, 96),
    ('STK-REST10', 'ING019', 38, 55, 75), ('STK-REST10', 'ING020', 19, 29, 38);

-- 8. Produtos (fast food) - SEM idRestaurante
INSERT INTO produtos (id, nome, precoBase, tempoPreparacao) VALUES 
    ('PROD001', 'Hambúrguer Clássico', 5.50, 8),
    ('PROD002', 'Cheeseburger', 6.00, 8),
    ('PROD003', 'Bacon Burger', 7.50, 9),
    ('PROD004', 'Double Cheese', 8.50, 10),
    ('PROD005', 'Chicken Burger', 6.50, 7),

    -- Sanduíches/Cachorros
    ('PROD006', 'Cachorro Quente', 4.50, 5),
    ('PROD007', 'Cachorro Completo', 6.00, 6),

    -- Acompanhamentos
    ('PROD010', 'Batata Frita Pequena', 2.50, 5),
    ('PROD011', 'Batata Frita Média', 3.50, 5),
    ('PROD012', 'Batata Frita Grande', 4.50, 6),

    -- Bebidas
    ('PROD016', 'Coca-Cola 33cl', 1.80, 1),
    ('PROD017', 'Fanta Laranja 33cl', 1.80, 1),
    ('PROD018', 'Água 50cl', 1.20, 1),
    ('PROD019', 'Sumo Natural Laranja', 3.00, 3),
    ('PROD020', 'Café', 1.00, 2);

-- 9. Itens (todos os produtos são itens individuais)
INSERT INTO itens (idProduto) VALUES 
('PROD001'), ('PROD002'), ('PROD003'), ('PROD004'), ('PROD005'),
('PROD006'), ('PROD007'), 
('PROD010'), ('PROD011'), ('PROD012'),
('PROD016'), ('PROD017'), ('PROD018'), ('PROD019'), ('PROD020');

INSERT INTO produtos (id, nome, precoBase, tempoPreparacao) VALUES 
    ('PROD026', 'Menu Hambúrguer + Batata + Bebida', 12.00, 15),
    ('PROD027', 'Menu Frango + Batata + Bebida', 13.50, 15),
    ('PROD028', 'Menu Cachorro + Bebida', 8.50, 10);

-- Agora definimos os menus (apenas como menus, não como itens)
INSERT INTO menus (idProduto, desconto) VALUES 
    ('PROD026', 10.0),   -- 10% de desconto
    ('PROD027', 15.0),   -- 15% de desconto  
    ('PROD028', 5.0);    -- 5% de desconto

-- NOTA: NÃO inserimos em 'itens' porque menus não são itens individuais

-- Menu Cachorro + Bebida contém: Cachorro Quente + Coca-Cola
INSERT INTO menuItem (idMenu, idItem) VALUES 
    -- Menu Hambúrguer + Batata + Bebida contém: Cheeseburger + Batata Média + Coca-Cola
    ('PROD026', 'PROD002'), -- Cheeseburger
    ('PROD026', 'PROD011'), -- Batata Frita Média
    ('PROD026', 'PROD016'), -- Coca-Cola

    -- Menu Frango + Batata + Bebida contém: Chicken Burger + Batata Média + Fanta
    ('PROD027', 'PROD005'), -- Chicken Burger
    ('PROD027', 'PROD011'), -- Batata Frita Média
    ('PROD027', 'PROD017'), -- Fanta Laranja

    -- Menu Cachorro + Bebida contém: Cachorro Quente + Coca-Cola
    ('PROD028', 'PROD006'), -- Cachorro Quente
    ('PROD028', 'PROD016'); -- Coca-Cola

-- 13. ItemIngrediente (ingredientes de cada item)
INSERT INTO itemIngrediente (idItem, idIngrediente) VALUES 
    -- Hambúrguer Clássico (PROD001)
    ('PROD001', 'ING001'), -- Pão de Hambúrguer
    ('PROD001', 'ING002'), -- Carne de Vaca
    ('PROD001', 'ING004'), -- Alface
    ('PROD001', 'ING005'), -- Tomate
    ('PROD001', 'ING007'), -- Ketchup

    -- Cheeseburger (PROD002)
    ('PROD002', 'ING001'), -- Pão de Hambúrguer
    ('PROD002', 'ING002'), -- Carne de Vaca
    ('PROD002', 'ING003'), -- Queijo Cheddar
    ('PROD002', 'ING004'), -- Alface
    ('PROD002', 'ING008'), -- Maionese

    -- Bacon Burger (PROD003)
    ('PROD003', 'ING001'), -- Pão de Hambúrguer
    ('PROD003', 'ING002'), -- Carne de Vaca
    ('PROD003', 'ING003'), -- Queijo Cheddar
    ('PROD003', 'ING009'), -- Bacon
    ('PROD003', 'ING007'), -- Ketchup

    -- Double Cheese (PROD004)
    ('PROD004', 'ING001'), -- Pão de Hambúrguer
    ('PROD004', 'ING002'), -- Carne de Vaca
    ('PROD004', 'ING003'), -- Queijo Cheddar (dupla porção)
    ('PROD004', 'ING004'), -- Alface
    ('PROD004', 'ING008'), -- Maionese

    -- Chicken Burger (PROD005)
    ('PROD005', 'ING001'), -- Pão de Hambúrguer
    ('PROD005', 'ING010'), -- Frango (filete)
    ('PROD005', 'ING004'), -- Alface
    ('PROD005', 'ING005'), -- Tomate
    ('PROD005', 'ING008'), -- Maionese

    -- Cachorro Quente (PROD006)
    ('PROD006', 'ING012'), -- Pão de Cachorro
    ('PROD006', 'ING013'), -- Salsicha
    ('PROD006', 'ING007'), -- Ketchup

    -- Cachorro Completo (PROD007)
    ('PROD007', 'ING012'), -- Pão de Cachorro
    ('PROD007', 'ING013'), -- Salsicha
    ('PROD007', 'ING006'), -- Cebola
    ('PROD007', 'ING007'), -- Ketchup
    ('PROD007', 'ING008'), -- Maionese

    -- Batata Frita Pequena (PROD010)
    ('PROD010', 'ING011'), -- Batata Frita

    -- Batata Frita Média (PROD011)
    ('PROD011', 'ING011'), -- Batata Frita

    -- Batata Frita Grande (PROD012)
    ('PROD012', 'ING011'), -- Batata Frita

    -- Coca-Cola (PROD016)
    ('PROD016', 'ING014'), -- Refrigerante Cola
    ('PROD016', 'ING020'), -- Gelo

    -- Fanta Laranja (PROD017)
    ('PROD017', 'ING015'), -- Refrigerante Laranja
    ('PROD017', 'ING020'), -- Gelo

    -- Água (PROD018)
    ('PROD018', 'ING016'), -- Água

    -- Sumo Natural Laranja (PROD019)
    ('PROD019', 'ING015'), -- Refrigerante Laranja
    ('PROD019', 'ING020'), -- Gelo

    -- Café (PROD020)
    ('PROD020', 'ING017'), -- Café
    ('PROD020', 'ING018'), -- Leite
    ('PROD020', 'ING019'); -- Açúcar

-- Povoamento da tabela postos
INSERT INTO postos (id, funcao, idRestaurante) VALUES 
-- Restaurante 1 (REST01 - Braga)
('POSTO001', 'Balcão de Atendimento', 'REST01'),
('POSTO002', 'Preparação de Hamburgueres', 'REST01'),
('POSTO003', 'Preparação de Fritos', 'REST01'),
('POSTO004', 'Montagem de Pedidos', 'REST01'),
('POSTO005', 'Gestão de Stock', 'REST01'),

-- Restaurante 2 (REST02 - Porto)
('POSTO006', 'Balcão de Atendimento', 'REST02'),
('POSTO007', 'Preparação de Hamburgueres', 'REST02'),
('POSTO008', 'Preparação de Fritos', 'REST02'),
('POSTO009', 'Montagem de Pedidos', 'REST02'),
('POSTO010', 'Gestão de Stock', 'REST02'),

-- Restaurante 3 (REST03 - Lisboa)
('POSTO011', 'Balcão de Atendimento', 'REST03'),
('POSTO012', 'Preparação de Frango', 'REST03'),
('POSTO013', 'Preparação de Fritos', 'REST03'),
('POSTO014', 'Montagem de Pedidos', 'REST03'),
('POSTO015', 'Gestão de Stock', 'REST03'),

-- Restaurante 4 (REST04 - Coimbra)
('POSTO016', 'Balcão de Atendimento', 'REST04'),
('POSTO017', 'Preparação de Cachorros', 'REST04'),
('POSTO018', 'Preparação de Fritos', 'REST04'),
('POSTO019', 'Montagem de Pedidos', 'REST04'),
('POSTO020', 'Gestão de Stock', 'REST04'),

-- Restaurante 5 (REST05 - Faro)
('POSTO021', 'Balcão de Atendimento', 'REST05'),
('POSTO022', 'Preparação de Shawarma', 'REST05'),
('POSTO023', 'Preparação de Saladas', 'REST05'),
('POSTO024', 'Montagem de Pedidos', 'REST05'),
('POSTO025', 'Gestão de Stock', 'REST05'),

-- Restaurante 6 (REST06 - Aveiro)
('POSTO026', 'Balcão de Atendimento', 'REST06'),
('POSTO027', 'Preparação de Hamburgueres', 'REST06'),
('POSTO028', 'Preparação de Fritos', 'REST06'),
('POSTO029', 'Montagem de Pedidos', 'REST06'),
('POSTO030', 'Gestão de Stock', 'REST06'),

-- Restaurante 7 (REST07 - Viseu)
('POSTO031', 'Balcão de Atendimento', 'REST07'),
('POSTO032', 'Preparação de Frango', 'REST07'),
('POSTO033', 'Preparação de Saladas', 'REST07'),
('POSTO034', 'Montagem de Pedidos', 'REST07'),
('POSTO035', 'Gestão de Stock', 'REST07'),

-- Restaurante 8 (REST08 - Guimarães)
('POSTO036', 'Balcão de Atendimento', 'REST08'),
('POSTO037', 'Preparação de Hamburgueres', 'REST08'),
('POSTO038', 'Preparação de Fritos', 'REST08'),
('POSTO039', 'Montagem de Pedidos', 'REST08'),
('POSTO040', 'Gestão de Stock', 'REST08'),

-- Restaurante 9 (REST09 - Évora)
('POSTO041', 'Balcão de Atendimento', 'REST09'),
('POSTO042', 'Preparação de Cachorros', 'REST09'),
('POSTO043', 'Preparação de Fritos', 'REST09'),
('POSTO044', 'Montagem de Pedidos', 'REST09'),
('POSTO045', 'Gestão de Stock', 'REST09'),

-- Restaurante 10 (REST10 - Leiria)
('POSTO046', 'Balcão de Atendimento', 'REST10'),
('POSTO047', 'Preparação de Hamburgueres', 'REST10'),
('POSTO048', 'Preparação de Sobremesas', 'REST10'),
('POSTO049', 'Montagem de Pedidos', 'REST10'),
('POSTO050', 'Gestão de Stock', 'REST10');

COMMIT;