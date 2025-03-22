CREATE TABLE IF NOT EXISTS guests (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       name VARCHAR(255) NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       check_in_time TIMESTAMP,
                       check_out_time TIMESTAMP
);

INSERT INTO guests (name, status, check_in_time) VALUES ('John Doe', 'CHECKED_IN', CURRENT_TIMESTAMP);