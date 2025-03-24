CREATE TABLE IF NOT EXISTS parcels (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        guest_id BIGINT NOT NULL,
                        description VARCHAR(255),
                        status VARCHAR(50) NOT NULL,
                        received_date TIMESTAMP,
                        FOREIGN KEY (guest_id) REFERENCES guestS(id) ON DELETE RESTRICT
);

INSERT INTO parcels (guest_id, description, status, received_date)
VALUES (1, 'Package for John', 'PENDING', CURRENT_TIMESTAMP);