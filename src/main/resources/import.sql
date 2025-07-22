INSERT INTO TB_USER (nome, email, password) VALUES ('Alex','alex@gmail.com', '$2a$10$2qhPu70lsjB8.KyuggwD/eUXyChTTunA7aQ0whq2D1gukDFuNw2Fy');
INSERT INTO TB_USER (nome, email, password) VALUES ('João','joao@gmail.com', '$2a$10$2qhPu70lsjB8.KyuggwD/eUXyChTTunA7aQ0whq2D1gukDFuNw2Fy');
INSERT INTO TB_USER (nome, email, password) VALUES ('Ana','ana@gmail.com', '$2a$10$2qhPu70lsjB8.KyuggwD/eUXyChTTunA7aQ0whq2D1gukDFuNw2Fy');
INSERT INTO TB_USER (nome, email, password) VALUES ('Maria','maria@gmail.com', '$2a$10$2qhPu70lsjB8.KyuggwD/eUXyChTTunA7aQ0whq2D1gukDFuNw2Fy');


INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Fatura Sicoob', 1500.00, 072025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Seguro carro', 1500.00, 072025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Internet', 1500.00, 072025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Condominio', 1500.00, 072025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Conta luz', 1500.00, 072025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Fatura Sicoob', 1500.00, 082025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Seguro carro', 1500.00, 082025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Internet', 1500.00, 082025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Condominio', 1500.00, 082025, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, periodo, data_vencimento, user_id) VALUES ('Conta luz', 1500.00, 082025, '2025-07-15 10:30:00', 4);

INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_user_role (user_id, role_id) VALUES (1, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (2, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (3, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 1);
INSERT INTO tb_user_role (user_id, role_id) VALUES (4, 2);