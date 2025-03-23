CREATE TABLE IF NOT EXISTS guests (
      id BIGSERIAL PRIMARY KEY,
      name VARCHAR(255) NOT NULL,
      status VARCHAR(50) NULL,
      check_in_time TIMESTAMP,
      check_out_time TIMESTAMP
    );
