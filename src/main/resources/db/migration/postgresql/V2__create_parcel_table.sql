CREATE TABLE IF NOT EXISTS parcels (
    id BIGSERIAL PRIMARY KEY,
    guest_id BIGINT NOT NULL,
    description VARCHAR(255),
    status VARCHAR(50) NULL,
    received_date TIMESTAMP,
    FOREIGN KEY (guest_id) REFERENCES guests(id) ON DELETE CASCADE
    );