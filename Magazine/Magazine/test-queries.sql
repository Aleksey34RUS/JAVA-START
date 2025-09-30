-- 5 запросов на чтение
SELECT o.id, o.order_date, c.first_name || ' ' || c.last_name AS customer_name, p.description AS book, o.quantity
FROM "order" o
JOIN customer c ON o.customer_id = c.id
JOIN product p ON o.product_id = p.id
WHERE o.order_date >= CURRENT_DATE - INTERVAL '7 days';

SELECT p.description, SUM(o.quantity) AS total_sold
FROM product p
JOIN "order" o ON p.id = o.product_id
GROUP BY p.description
ORDER BY total_sold DESC
LIMIT 3;

SELECT c.first_name, c.last_name, COUNT(o.id) AS orders_count
FROM customer c
LEFT JOIN "order" o ON c.id = o.customer_id
GROUP BY c.id
ORDER BY orders_count DESC;

SELECT o.id, os.status_name, o.order_date
FROM "order" o
JOIN order_status os ON o.status_id = os.id
WHERE os.status_name = 'Новый'
ORDER BY o.order_date DESC;

SELECT p.category, SUM(p.price * o.quantity) AS total_revenue
FROM product p
JOIN "order" o ON p.id = o.product_id
GROUP BY p.category;

-- 3 запроса на UPDATE
UPDATE product
SET quantity = quantity - 1
WHERE id = 1 AND quantity > 0;

UPDATE "order"
SET status_id = (SELECT id FROM order_status WHERE status_name = 'Оплачен')
WHERE id = 1;

UPDATE customer
SET email = 'updated@example.com'
WHERE id = 1;

-- 2 запроса на DELETE
DELETE FROM customer
WHERE id NOT IN (SELECT DISTINCT customer_id FROM "order");

DELETE FROM "order"
WHERE id = 10;