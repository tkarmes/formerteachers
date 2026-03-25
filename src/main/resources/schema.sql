-- Existing tables
CREATE TABLE IF NOT EXISTS app_users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(255) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(50)
);

CREATE TABLE IF NOT EXISTS jobs (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    company VARCHAR(255) NOT NULL,
    location VARCHAR(255),
    salary_range VARCHAR(255),
    description TEXT,
    approved BOOLEAN DEFAULT FALSE,
    category VARCHAR(255),
    work_type VARCHAR(255),
    apply_info VARCHAR(255),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    employer_id INTEGER
);

-- New Employer Profiles Table
CREATE TABLE IF NOT EXISTS employer_profiles (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES app_users(id),
    company_name VARCHAR(255) NOT NULL,
    company_description TEXT,
    website VARCHAR(255),
    logo_url VARCHAR(255)
);

-- Ensure jobs.employer_id is linked to employer_profiles
-- (Skip foreign key if table already exists without it for now)

-- DROP TABLE IF EXISTS subscribers;
