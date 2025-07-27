import java.util.*;
import java.util.stream.Collectors;

public class App {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Map<String, Person> people = new HashMap<>();
        Map<String, Product> products = new HashMap<>();

        // Обработка ввода покупателей
        System.out.print("Введите покупателей (Формат: Имя = сумма; ...): ");
        String[] peopleInput = scanner.nextLine().split(";");
        for (String personData : peopleInput) {
            try {
                String[] parts = personData.split("=");
                String name = parts[0].trim();
                double money = Double.parseDouble(parts[1].trim());
                people.put(name, new Person(name, money));
            } catch (Exception e) {
                System.out.println("Ошибка при обработке покупателя: " + e.getMessage());
            }
        }

        // Обработка ввода продуктов
        System.out.print("Введите продукты (Формат: Название = цена; ...): ");
        String[] productsInput = scanner.nextLine().split(";");
        for (String productData : productsInput) {
            try {
                String[] parts = productData.split("=");
                String name = parts[0].trim();
                double price = Double.parseDouble(parts[1].trim());
                products.put(name, new Product(name, price));
            } catch (Exception e) {
                System.out.println("Ошибка при обработке продукта: " + e.getMessage());
            }
        }

        // Обработка покупок
        while (true) {
            String input = scanner.nextLine();
            if (input.equals("END")) break;

            try {
                String[] parts = input.split("-");
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

        scanner.close();
    }
}