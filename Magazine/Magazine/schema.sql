-- Таблица для книг
CREATE TABLE IF NOT EXISTS product (
    id SERIAL PRIMARY KEY,
    description VARCHAR(255) NOT NULL,
    price DECIMAL(10, 2) NOT NULL CHECK (price >= 0),
    quantity INT NOT NULL CHECK (quantity >= 0),
    category VARCHAR(100) NOT NULL
);

CREATE INDEX IF NOT EXISTS idx_product_category ON product(category);

-- Таблица для клиентов
CREATE TABLE IF NOT EXISTS customer (
    id SERIAL PRIMARY KEY,
    first_name VARCHAR(100) NOT NULL,
    last_name VARCHAR(100) NOT NULL,
    phone VARCHAR(20),
    email VARCHAR(255) UNIQUE NOT NULL
);

-- Справочник статусов
CREATE TABLE IF NOT EXISTS order_status (
    id SERIAL PRIMARY KEY,
    status_name VARCHAR(50) NOT NULL UNIQUE
);

-- Таблица для заказов
CREATE TABLE IF NOT EXISTS "order" (
    id SERIAL PRIMARY KEY,
    product_id INT NOT NULL REFERENCES product(id) ON DELETE CASCADE,
    customer_id INT NOT NULL REFERENCES customer(id) ON DELETE CASCADE,
    order_date DATE NOT NULL DEFAULT CURRENT_DATE,
    quantity INT NOT NULL CHECK (quantity > 0),
    status_id INT NOT NULL REFERENCES order_status(id)
);

CREATE INDEX IF NOT EXISTS idx_order_product ON "order"(product_id);
CREATE INDEX IF NOT EXISTS idx_order_customer ON "order"(customer_id);
CREATE INDEX IF NOT EXISTS idx_order_date ON "order"(order_date);

-- Тестовые данные
INSERT INTO product (description, price, quantity, category) VALUES
('Гарри Поттер', 500.00, 20, 'Фантастика'),
('Властелин колец', 700.00, 15, 'Фантастика'),
('1984', 400.00, 30, 'Дистопия'),
('Гордость и предубеждение', 300.00, 25, 'Классика'),
('Шерлок Холмс', 450.00, 10, 'Детектив'),
('Маленький принц', 200.00, 40, 'Детская'),
('Великий Гэтсби', 350.00, 18, 'Классика'),
('Мастер и Маргарита', 550.00, 12, 'Фантастика'),
('Преступление и наказание', 400.00, 22, 'Классика'),
('Алиса в Зазеркалье', 250.00, 35, 'Детская');

INSERT INTO customer (first_name, last_name, phone, email) VALUES
('Иван', 'Иванов', '123456789', 'ivan@example.com'),
('Мария', 'Петрова', '987654321', 'maria@example.com'),
('Алексей', 'Сидоров', '111222333', 'alex@example.com'),
('Ольга', 'Кузнецова', '444555666', 'olga@example.com'),
('Дмитрий', 'Смирнов', '777888999', 'dmitry@example.com'),
('Анна', 'Васильева', '000111222', 'anna@example.com'),
('Сергей', 'Морозов', '333444555', 'sergey@example.com'),
('Екатерина', 'Новикова', '666777888', 'ekaterina@example.com'),
('Павел', 'Федоров', '999000111', 'pavel@example.com'),
('Татьяна', 'Михайлова', '222333444', 'tatiana@example.com');

INSERT INTO order_status (status_name) VALUES
('Новый'), ('Оплачен'), ('В обработке'), ('Отправлен'), ('Доставлен'),
('Отменен'), ('Возврат'), ('Ждет оплаты'), ('Подтвержден'), ('Завершен');

INSERT INTO "order" (product_id, customer_id, order_date, quantity, status_id) VALUES
(1, 1, '2025-09-20', 2, 1),
(2, 2, '2025-09-21', 1, 2),
(3, 3, '2025-09-22', 3, 3),
(4, 4, '2025-09-23', 1, 4),
(5, 5, '2025-09-24', 2, 5),
(6, 6, '2025-09-25', 4, 6),
(7, 7, '2025-09-26', 1, 7),
(8, 8, '2025-09-27', 2, 8),
(9, 9, '2025-09-28', 3, 9),
(10, 10, '2025-09-29', 1, 10);