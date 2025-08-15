import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.util.*;

public class App {
    public static void main(String[] args) {
        Scanner scanner = null;

        try {
            if (args.length > 0) {
                // Чтение из файла, указанного в аргументе
                scanner = new Scanner(new File(args[0]));
            } else {
                // Чтение из консоли
                scanner = new Scanner(System.in);
            }

            Map<String, Person> people = new HashMap<>();
            Map<String, Product> products = new HashMap<>();

            // Обработка ввода покупателей
            System.out.print("Введите покупателей (Формат: Имя=сумма; ...): ");
            String peopleInput = scanner.nextLine();
            processPeople(peopleInput, people);

            // Обработка ввода продуктов
            System.out.print("Введите продукты (Формат: Обычный=цена; Скидочный=базовая_цена:скидка:гггг-мм-дд; ...): ");
            String productsInput = scanner.nextLine();
            processProducts(productsInput, products);

            // Обработка покупок
            System.out.println("Введите покупки (Формат: Имя - Продукт). Для завершения введите END:");
            while (scanner.hasNextLine()) {
                String input = scanner.nextLine();
                if (input.equals("end")) break;

                try {
                    String[] parts = input.split("-");
                    if (parts.length < 2) {
                        System.out.println("Неверный формат: " + input);
                        continue;
                    }

                    String personName = parts[0].trim();
                    String productName = parts[1].trim();

                    Person person = people.get(personName);
                    Product product = products.get(productName);

                    if (person != null && product != null) {
                        person.buyProduct(product);
                    } else {
                        System.out.println("Неверные данные: " + input);
                    }
                } catch (Exception e) {
                    System.out.println("Ошибка при обработке покупки: " + e.getMessage());
                }
            }

            // Вывод результатов
            System.out.println("\nРезультаты:");
            for (Person person : people.values()) {
                System.out.println(person);
            }

        } catch (FileNotFoundException e) {
            System.out.println("Файл не найден: " + e.getMessage());
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private static void processPeople(String input, Map<String, Person> people) {
        String[] peopleData = input.split(";");
        for (String personData : peopleData) {
            try {
                if (personData.trim().isEmpty()) continue;

                String[] parts = personData.split("=");
                if (parts.length < 2) {
                    System.out.println("Неверный формат покупателя: " + personData);
                    continue;
                }

                String name = parts[0].trim();
                double money = Double.parseDouble(parts[1].trim());
                people.put(name, new Person(name, money));
            } catch (Exception e) {
                System.out.println("Ошибка при обработке покупателя: " + e.getMessage());
            }
        }
    }

    private static void processProducts(String input, Map<String, Product> products) {
        String[] productsData = input.split(";");
        for (String productData : productsData) {
            try {
                if (productData.trim().isEmpty()) continue;

                String[] parts = productData.split("=");
                if (parts.length < 2) {
                    System.out.println("Неверный формат продукта: " + productData);
                    continue;
                }

                String name = parts[0].trim();
                String data = parts[1].trim();

                if (data.contains(":")) {
                    // Обработка скидочного продукта
                    String[] details = data.split(":");
                    if (details.length < 3) {
                        System.out.println("Неверный формат скидочного продукта: " + data);
                        continue;
                    }

                    double basePrice = Double.parseDouble(details[0].trim());
                    double discount = Double.parseDouble(details[1].trim());
                    LocalDate expiryDate = LocalDate.parse(details[2].trim());
                    products.put(name, new DiscountProduct(name, basePrice, discount, expiryDate));
                } else {
                    // Обработка обычного продукта
                    double price = Double.parseDouble(data);
                    products.put(name, new Product(name, price));
                }
            } catch (Exception e) {
                System.out.println("Ошибка при обработке продукта: " + productData + " - " + e.getMessage());
            }
        }
    }
}