INSERT INTO client (name) VALUES
('Alice Johnson'),
('Bob Smith'),
('Charlie Brown'),
('Diana Prince'),
('Ethan Hunt'),
('Fiona Gallagher'),
('George Martin'),
('Hannah Baker'),
('Ivan Petrov'),
('Julia Roberts');


INSERT INTO planet (id, name) VALUES
('PLN001', 'Earth'),
('PLN002', 'Mars'),
('PLN003', 'Jupiter'),
('PLN004', 'Venus'),
('PLN005', 'Saturn');

INSERT INTO ticket (created_at, client_id, from_planet_id, to_planet_id) VALUES
(CURRENT_TIMESTAMP, 1, 'PLN001', 'PLN002'),
(CURRENT_TIMESTAMP, 2, 'PLN002', 'PLN003'),
(CURRENT_TIMESTAMP, 3, 'PLN003', 'PLN004'),
(CURRENT_TIMESTAMP, 4, 'PLN004', 'PLN005'),
(CURRENT_TIMESTAMP, 5, 'PLN005', 'PLN001'),
(CURRENT_TIMESTAMP, 6, 'PLN001', 'PLN003'),
(CURRENT_TIMESTAMP, 7, 'PLN002', 'PLN004'),
(CURRENT_TIMESTAMP, 8, 'PLN003', 'PLN005'),
(CURRENT_TIMESTAMP, 9, 'PLN004', 'PLN001'),
(CURRENT_TIMESTAMP, 10,'PLN005', 'PLN002');