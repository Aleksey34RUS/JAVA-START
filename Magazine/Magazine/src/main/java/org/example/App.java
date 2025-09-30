package org.example;

import java.io.InputStream;
import java.sql.*;
import java.util.Properties;

public class App {
    public static void main(String[] args) {
        // Загрузка настроек
        Properties props = new Properties();
        try (InputStream input = App.class.getClassLoader().getResourceAsStream("application.properties")) {
            props.load(input);
        } catch (Exception e) {
            System.out.println("Ошибка настроек: " + e.getMessage());
            return;
        }

        String url = props.getProperty("db.url");
        String user = props.getProperty("db.user");
        String password = props.getProperty("db.password");

        // Подключение к БД
        try (Connection conn = DriverManager.getConnection(url, user, password)) {
            conn.setAutoCommit(false);

            // 1. Добавление книги
            String insertProduct = "INSERT INTO product (description, price, quantity, category) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertProduct, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Новая книга");
                ps.setDouble(2, 300.00);
                ps.setInt(3, 10);
                ps.setString(4, "Новинка");
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) System.out.println("Добавлена книга ID: " + rs.getInt(1));
            }

            // 2. Добавление клиента
            String insertCustomer = "INSERT INTO customer (first_name, last_name, phone, email) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertCustomer, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, "Новый");
                ps.setString(2, "Клиент");
                ps.setString(3, "123456789");
                ps.setString(4, "new@client.com");
                ps.executeUpdate();
                ResultSet rs = ps.getGeneratedKeys();
                if (rs.next()) System.out.println("Добавлен клиент ID: " + rs.getInt(1));
            }

            // 3. Создание заказа
            String insertOrder = "INSERT INTO \"order\" (product_id, customer_id, quantity, status_id) VALUES (?, ?, ?, ?)";
            try (PreparedStatement ps = conn.prepareStatement(insertOrder)) {
                ps.setInt(1, 1);
                ps.setInt(2, 1);
                ps.setInt(3, 1);
                ps.setInt(4, 1);
                ps.executeUpdate();
                System.out.println("Заказ создан");
            }

            // 4. Чтение 5 последних заказов
            String selectOrders = "SELECT o.id, p.description, c.first_name || ' ' || c.last_name AS customer, o.order_date, o.quantity " +
                    "FROM \"order\" o JOIN product p ON o.product_id = p.id JOIN customer c ON o.customer_id = c.id " +
                    "ORDER BY o.order_date DESC LIMIT 5";
            try (Statement stmt = conn.createStatement(); ResultSet rs = stmt.executeQuery(selectOrders)) {
                System.out.println("Последние 5 заказов:");
                while (rs.next()) {
                    System.out.printf("ID: %d, Книга: %s, Клиент: %s, Дата: %s, Кол-во: %d%n",
                            rs.getInt("id"), rs.getString("description"), rs.getString("customer"),
                            rs.getDate("order_date"), rs.getInt("quantity"));
                }
            }

            // 5. Обновление книги
            String updateProduct = "UPDATE product SET price = ?, quantity = ? WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(updateProduct)) {
                ps.setDouble(1, 550.00);
                ps.setInt(2, 15);
                ps.setInt(3, 1);
                ps.executeUpdate();
                System.out.println("Книга обновлена");
            }

            // 6. Удаление заказа
            String deleteOrder = "DELETE FROM \"order\" WHERE id = ?";
            try (PreparedStatement ps = conn.prepareStatement(deleteOrder)) {
                ps.setInt(1, 1);
                ps.executeUpdate();
                System.out.println("Заказ удалён");
            }

            conn.commit();
            System.out.println("Операции завершены!");
        } catch (SQLException e) {
            System.out.println("Ошибка: " + e.getMessage());
            try (Connection conn = DriverManager.getConnection(url, user, password)) {
                conn.rollback();
                System.out.println("Транзакция отменена");
            } catch (SQLException ex) {
                System.out.println("Ошибка rollback: " + ex.getMessage());
            }
        }
    }
}