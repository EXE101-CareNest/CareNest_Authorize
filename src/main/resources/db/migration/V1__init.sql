-- Flyway baseline schema for authorize-service

-- ROLES
CREATE TABLE IF NOT EXISTS user_role (
    id INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- SHOP ROLES
CREATE TABLE IF NOT EXISTS shop_role (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- MODULES
CREATE TABLE IF NOT EXISTS module (
    url_pattern VARCHAR(255) PRIMARY KEY,
    name VARCHAR(255) NOT NULL
);

-- ROLE PERMISSIONS
CREATE TABLE IF NOT EXISTS role_permission (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    role_id INTEGER REFERENCES user_role(id) ON DELETE CASCADE,
    module_url_pattern VARCHAR(255) REFERENCES module(url_pattern) ON DELETE CASCADE,
    http_permission VARCHAR(32)
);

-- USERS
CREATE TABLE IF NOT EXISTS app_user (
    id VARCHAR(64) PRIMARY KEY,
    username VARCHAR(255),
    full_name VARCHAR(255),
    password VARCHAR(255),
    email VARCHAR(255),
    gender VARCHAR(32),
    date_of_birth TIMESTAMP,
    nationality VARCHAR(255),
    permanent_address VARCHAR(255),
    home_town VARCHAR(255),
    issued_date VARCHAR(64),
    issued_by VARCHAR(255),
    img_url VARCHAR(512),
    is_active BOOLEAN,
    status BOOLEAN,
    register_status VARCHAR(32),
    role_id INTEGER REFERENCES user_role(id),
    shop_role_id BIGINT REFERENCES shop_role(id),
    created_date TIMESTAMP,
    updated_date TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_app_user_username ON app_user(username);
CREATE UNIQUE INDEX IF NOT EXISTS uq_module_url_pattern ON module(url_pattern);

-- SHOPS
CREATE TABLE IF NOT EXISTS shop (
    id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    shop_name VARCHAR(50) NOT NULL,
    owner_id VARCHAR(64) REFERENCES app_user(id),
    description TEXT,
    password VARCHAR(255),
    status BOOLEAN,
    img_url VARCHAR(512),
    working_days VARCHAR(255)
);


