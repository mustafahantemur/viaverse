ALTER TABLE auth_session
    ADD COLUMN device_id VARCHAR(255),
    ADD COLUMN device_name VARCHAR(100),
    ADD COLUMN platform VARCHAR(20),
    ADD COLUMN last_ip VARCHAR(45);
