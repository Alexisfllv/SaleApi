-- tablas
use SaleApi;

show tables;
-- CATEGORY
select * from category;
INSERT INTO category (category_name, category_description, category_enabled) VALUES
  ('Electrónica', 'Dispositivos electrónicos y gadgets', TRUE),
  ('Ropa', 'Vestimenta para hombres, mujeres y niños', TRUE),
  ('Hogar', 'Productos para el hogar y decoración', TRUE),
  ('Deportes', 'Equipamiento y ropa deportiva', TRUE),
  ('Juguetes', 'Juguetes y juegos para todas las edades', TRUE),
  ('Libros', 'Libros y material de lectura', TRUE),
  ('Belleza', 'Productos de cuidado personal y belleza', TRUE),
  ('Alimentos', 'Comestibles y productos alimenticios', TRUE),
  ('Automotriz', 'Accesorios y partes para vehículos', TRUE),
  ('Herramientas', 'Herramientas y equipos para construcción', TRUE);
  
select * from category;

-- PRODUCT

INSERT INTO product (product_name, product_description, product_price, product_enabled, category_id) VALUES
  ('Smartphone', 'Teléfono inteligente con pantalla AMOLED', 699.99, TRUE, 1),
  ('Laptop', 'Computadora portátil para uso profesional', 1200.00, TRUE, 1),
  ('Camiseta deportiva', 'Camiseta para actividades físicas', 29.99, TRUE, 2),
  ('Jeans', 'Pantalones de mezclilla azul', 49.99, TRUE, 2),
  ('Sofá 3 plazas', 'Sofá cómodo para sala de estar', 499.00, TRUE, 3),
  ('Lámpara de mesa', 'Lámpara decorativa para escritorio', 35.50, TRUE, 3),
  ('Pelota de fútbol', 'Pelota oficial para partidos', 25.00, TRUE, 4),
  ('Ropa de yoga', 'Conjunto para práctica de yoga', 60.00, TRUE, 4),
  ('Muñeca Barbie', 'Muñeca con accesorios variados', 15.99, TRUE, 5),
  ('Rompecabezas 1000 piezas', 'Juego de rompecabezas para adultos', 20.00, TRUE, 5),
  ('Novela de misterio', 'Libro con trama intrigante', 12.50, TRUE, 6),
  ('Libro de cocina', 'Recetas fáciles y saludables', 18.00, TRUE, 6),
  ('Crema facial', 'Crema hidratante para todo tipo de piel', 25.75, TRUE, 7),
  ('Perfume', 'Fragancia fresca para uso diario', 40.00, TRUE, 7),
  ('Café molido', 'Paquete de café premium 500g', 15.00, TRUE, 8),
  ('Chocolate artesanal', 'Chocolate negro con almendras', 7.50, TRUE, 8),
  ('Aceite para motor', 'Aceite sintético 5W-30', 30.00, TRUE, 9),
  ('Faro delantero', 'Luz para vehículo tipo LED', 75.00, TRUE, 9),
  ('Taladro eléctrico', 'Herramienta para perforar con batería', 110.00, TRUE, 10),
  ('Martillo', 'Martillo de acero con mango ergonómico', 18.00, TRUE, 10);
  
select * from product;

SELECT COUNT(*) FROM product;



-- ROLES
INSERT INTO role (role_name, role_enabled) VALUES
  ('ADMIN', TRUE),
  ('USER', TRUE),
  ('MANAGER', TRUE),
  ('SUPPORT', TRUE),
  ('GUEST', FALSE);
  
select * from role;

-- USER
INSERT INTO user_name (user_name, password, user_enabled, role_id) VALUES
  ('admin01', 'adminPass123', TRUE, 1),   -- ADMIN
  ('user01', 'userPass123', TRUE, 2),     -- USER
  ('user02', 'userPass234', TRUE, 2),
  ('manager01', 'managerPass123', TRUE, 3), -- MANAGER
  ('support01', 'supportPass123', TRUE, 4), -- SUPPORT
  ('guest01', 'guestPass123', FALSE, 5),    -- GUEST (deshabilitado)
  ('admin02', 'adminPass456', TRUE, 1),
  ('user03', 'userPass345', TRUE, 2),
  ('support02', 'supportPass456', TRUE, 4),
  ('manager02', 'managerPass456', TRUE, 3);
  
select * from user_name;

-- CLIENT
INSERT INTO client (client_first_name, client_last_name, client_email, client_card_id, client_phone, client_address) VALUES
('Carlos', 'Ramirez', 'carlos.ramirez@example.com', 'ID12345678', '987654321', 'Av. Siempre Viva 123, Lima'),
('Ana', 'Lopez', 'ana.lopez@example.com', 'ID87654321', '912345678', 'Jr. Los Pinos 456, Lima'),
('Jorge', 'Gonzalez', 'jorge.gonzalez@example.com', 'ID23456789', '998877665', 'Calle Falsa 789, Arequipa'),
('Maria', 'Fernandez', 'maria.fernandez@example.com', 'ID34567890', '976543210', 'Av. Central 321, Cusco'),
('Luis', 'Vargas', 'luis.vargas@example.com', 'ID45678901', '965432109', 'Jr. Las Flores 654, Trujillo'),
('Sofia', 'Mendoza', 'sofia.mendoza@example.com', 'ID56789012', '954321098', 'Av. Libertad 987, Chiclayo'),
('Diego', 'Rojas', 'diego.rojas@example.com', 'ID67890123', '943210987', 'Jr. Las Palmas 123, Piura'),
('Elena', 'Castillo', 'elena.castillo@example.com', 'ID78901234', '932109876', 'Av. del Sol 456, Iquitos'),
('Pablo', 'Torres', 'pablo.torres@example.com', 'ID89012345', '921098765', 'Calle Luna 789, Huancayo'),
('Claudia', 'Martinez', 'claudia.martinez@example.com', 'ID90123456', '910987654', 'Jr. Estrella 321, Tacna');

select * from client;

-- SALE

-- SALE DETAIL

select * from sale;

select * from sale_detail;


show tables;

select * from category_audit_log;

-- csv
select * from empleado;

select * from persona;

select * from employee;




