INSERT INTO TB_USUARIO (nome, email, senha) VALUES ('Alex','alex@gmail.com', '123456');
INSERT INTO TB_USUARIO (nome, email, senha) VALUES ('João','joao@gmail.com', '123456');
INSERT INTO TB_USUARIO (nome, email, senha) VALUES ('Ana','ana@gmail.com', '123456');
INSERT INTO TB_USUARIO (nome, email, senha) VALUES ('Maria','maria@gmail.com', '123456');


INSERT INTO TB_GASTO (descricao, valor, data_vencimento, usuario_id) VALUES ('Fatura Sicoob', 1500.00, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, data_vencimento, usuario_id) VALUES ('Seguro carro', 1500.00, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, data_vencimento, usuario_id) VALUES ('Internet', 1500.00, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, data_vencimento, usuario_id) VALUES ('Condominio', 1500.00, '2025-07-15 10:30:00', 4);
INSERT INTO TB_GASTO (descricao, valor, data_vencimento, usuario_id) VALUES ('Conta luz', 1500.00, '2025-07-15 10:30:00', 4);

INSERT INTO tb_role (authority) VALUES ('ROLE_OPERATOR');
INSERT INTO tb_role (authority) VALUES ('ROLE_ADMIN');

INSERT INTO tb_usuario_role (usuario_id, role_id) VALUES (1, 1);
INSERT INTO tb_usuario_role (usuario_id, role_id) VALUES (2, 1);
INSERT INTO tb_usuario_role (usuario_id, role_id) VALUES (2, 2);
INSERT INTO tb_usuario_role (usuario_id, role_id) VALUES (3, 1);