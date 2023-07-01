INSERT INTO EXPENSE_CATEGORY (CATEGORY_NAME, CREATED_AT, UPDATED_AT)
VALUES ('Aluguel', now(), now()),
       ('Condomínio', now(), now()),
       ('Manutenção', now(), now()),
       ('Mercado', now(), now()),
       ('Refeição Fora', now(), now()),
       ('Esportes', now(), now()),
       ('Jogos', now(), now()),
       ('Água', now(), now()),
       ('Luz', now(), now()),
       ('Telefone', now(), now()),
       ('TV/Internet/Telefone', now(), now()),
       ('Carro', now(), now()),
       ('Estacionamento', now(), now()),
       ('Combustível', now(), now()),
       ('Transporte público', now(), now()),
       ('Hotel', now(), now()),
       ('Seguro', now(), now()),
       ('Despesas Médicas', now(), now()),
       ('Roupa', now(), now()),
       ('Educação', now(), now()),
       ('Fatura cartão', now(), now()),
       ('Outros', now(), now());

INSERT INTO PAYMENT_TYPE (PAYMENT_NAME, CREATED_AT, UPDATED_AT)
VALUES ('Dinheiro', now(), now()),
       ('Pix', now(), now()),
       ('Cartão de Débito', now(), now()),
       ('Cartão de Crédito', now(), now()),
       ('Cartão Alimentação/Refeição', now(), now()),
       ('Outros', now(), now());

INSERT INTO person (person_name, created_at, updated_at)
    VALUES
           ('Thon', now(), now()),
           ('Karina', now(), now());

INSERT INTO expense_group (group_name, created_by, created_at, updated_at) VALUES
    ('Despesas de casa', 1, now(), now());

insert into person_expense_group (person, expense_group)
    values (1,1), (2,1);

insert into financial_account (account_name, created_by, expense_group, created_at, updated_at, is_active) VALUES
    ('Itau', 1, 1, now(), now(), true);