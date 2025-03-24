CREATE TABLE clients (
                         id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                         name VARCHAR(255) NOT NULL,
                         email VARCHAR(255) UNIQUE NOT NULL,
                         phone VARCHAR(50),
                         company_name VARCHAR(255),
                         address TEXT,
                         created_at TIMESTAMP DEFAULT now(),
                         updated_at TIMESTAMP DEFAULT now(),
                         active BOOLEAN DEFAULT true
);

CREATE TABLE projects (
                          id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                          name VARCHAR(255) NOT NULL,
                          description TEXT,
                          client_id UUID NOT NULL,
                          start_date DATE,
                          end_date DATE,
                          status VARCHAR(50) NOT NULL,
                          manager VARCHAR(255),
                          notes TEXT,
                          is_deleted BOOLEAN DEFAULT false,
                          created_at TIMESTAMP DEFAULT now(),
                          updated_at TIMESTAMP DEFAULT now(),
                          FOREIGN KEY (client_id) REFERENCES clients(id) ON DELETE CASCADE
);

CREATE TABLE users (
                       id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                       username VARCHAR(255) UNIQUE NOT NULL,
                       password VARCHAR(255) NOT NULL,
                       email VARCHAR(255) UNIQUE NOT NULL,
                       full_name VARCHAR(255),
                       enabled BOOLEAN DEFAULT true,
                       created_at TIMESTAMP DEFAULT now(),
                       updated_at TIMESTAMP DEFAULT now()
);

CREATE TABLE user_roles (
                            user_id UUID NOT NULL,
                            role VARCHAR(100) NOT NULL,
                            FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE activities (
                            id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                            title VARCHAR(255) NOT NULL,
                            description TEXT,
                            project_id UUID NOT NULL,
                            due_date DATE,
                            assigned_to VARCHAR(255),
                            completed BOOLEAN DEFAULT false,
                            priority VARCHAR(50),
                            estimated_hours INT,
                            created_at TIMESTAMP DEFAULT now(),
                            updated_at TIMESTAMP DEFAULT now(),
                            FOREIGN KEY (project_id) REFERENCES projects(id) ON DELETE CASCADE
);
