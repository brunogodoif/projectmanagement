-- Inserir dados na tabela clients
INSERT INTO clients (id, name, email, phone, company_name, address, created_at, updated_at, active)
VALUES
    (gen_random_uuid(), 'Client One', 'client1@example.com', '123456789', 'Client One Corp', '123 Client St', now(), now(), true),
    (gen_random_uuid(), 'Client Two', 'client2@example.com', '987654321', 'Client Two LLC', '456 Client Ave', now(), now(), true);

-- Inserir dados na tabela projects
INSERT INTO projects (id, name, description, client_id, start_date, end_date, status, manager, notes, is_deleted, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Project Alpha', 'Description of Project Alpha', (SELECT id FROM clients WHERE email = 'client1@example.com'), '2025-04-01', '2025-09-30', 'IN_PROGRESS', 'John Doe', 'Initial project notes', false, now(), now()),
    (gen_random_uuid(), 'Project Beta', 'Description of Project Beta', (SELECT id FROM clients WHERE email = 'client2@example.com'), '2025-05-01', '2025-12-31', 'PLANNED', 'Jane Smith', 'Initial project notes', false, now(), now());

-- Inserir dados na tabela users
INSERT INTO users (id, username, password, email, full_name, enabled, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'admin', '$2a$10$CPj.VcCHOilMe5yLa1jjCOjXlXQzJZTjFvnsLiSQO9sUpZmCi2Niy', 'admin@example.com', 'Admin User', true, now(), now()),
    (gen_random_uuid(), 'jdoe', '$2a$10$CPj.VcCHOilMe5yLa1jjCOjXlXQzJZTjFvnsLiSQO9sUpZmCi2Niy', 'jdoe@example.com', 'John Doe', true, now(), now());

-- Inserir dados na tabela user_roles
INSERT INTO user_roles (user_id, role)
VALUES
    ((SELECT id FROM users WHERE username = 'admin'), 'ADMIN'),
    ((SELECT id FROM users WHERE username = 'admin'), 'USER'),
    ((SELECT id FROM users WHERE username = 'jdoe'), 'USER');

-- Inserir dados na tabela activities
INSERT INTO activities (id, title, description, project_id, due_date, assigned_to, completed, priority, estimated_hours, created_at, updated_at)
VALUES
    (gen_random_uuid(), 'Activity One', 'Description for Activity One', (SELECT id FROM projects WHERE name = 'Project Alpha'), '2025-04-10', 'John Doe', false, 'HIGH', 10, now(), now()),
    (gen_random_uuid(), 'Activity Two', 'Description for Activity Two', (SELECT id FROM projects WHERE name = 'Project Beta'), '2025-05-15', 'Jane Smith', false, 'MEDIUM', 8, now(), now());
