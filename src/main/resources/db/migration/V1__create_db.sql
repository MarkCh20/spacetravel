CREATE TABLE client (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL
    CONSTRAINT chk_client_name_min_length CHECK (LENGTH(name) >= 3)
);

CREATE TABLE planet (
    id VARCHAR(10) PRIMARY KEY,
    name VARCHAR(500) NOT NULL
);

CREATE TABLE ticket (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at TIMESTAMP NOT NULL,
    client_id BIGINT NOT NULL,
    from_planet_id VARCHAR(10) NOT NULL,
    to_planet_id VARCHAR(10) NOT NULL,
    CONSTRAINT fk_ticket_client FOREIGN KEY (client_id) REFERENCES client(id),
    CONSTRAINT fk_ticket_from_planet FOREIGN KEY (from_planet_id) REFERENCES planet(id),
    CONSTRAINT fk_ticket_to_planet FOREIGN KEY (to_planet_id) REFERENCES planet(id)
);
