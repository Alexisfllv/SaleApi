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

-- ROLES
INSERT INTO role (role_name, role_enabled) VALUES
  ('ADMIN', TRUE),
  ('USER', TRUE),
  ('MANAGER', TRUE),
  ('SUPPORT', TRUE),
  ('GUEST', FALSE);
  
select * from role;

-- USER





