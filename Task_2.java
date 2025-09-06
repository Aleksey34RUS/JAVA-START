import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

public class Task_2 {
    public static boolean isAnagram(String s, String t) {
        if (s.length() != t.length()) return false;

        Map<Character, Integer> charCount = new HashMap<>();

        // Увеличиваем счетчик для символов первой строки
        for (char c : s.toCharArray()) {
            charCount.put(c, charCount.getOrDefault(c, 0) + 1);
        }

        // Уменьшаем счетчик для символов второй строки
        for (char c : t.toCharArray()) {
            if (!charCount.containsKey(c)) return false;
            charCount.put(c, charCount.get(c) - 1);
            if (charCount.get(c) == 0) charCount.remove(c);
        }

        return charCount.isEmpty();
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Выберите способ ввода:");
        System.out.println("1 - Две отдельные строки");
        System.out.println("2 - Две строки через запятую");
        System.out.print("Ваш выбор: ");

        int choice = scanner.nextInt();
        scanner.nextLine(); // Очистка буфера

        String s, t;

        if (choice == 1) {
            System.out.print("Введите первую строку: ");
            s = scanner.nextLine();
            System.out.print("Введите вторую строку: ");
            t = scanner.nextLine();
        } else if (choice == 2) {
            System.out.print("Введите две строки через запятую: ");
            String input = scanner.nextLine();
            String[] parts = input.split(",");

            if (parts.length < 2) {
                System.out.println("Ошибка: нужно ввести две строки через запятую!");
                return;
            }

            s = parts[0].trim();
            t = parts[1].trim();
        } else {
            System.out.println("Неверный выбор!");
            return;
        }

        // Нормализация строк (приведение к нижнему регистру и удаление пробелов)
        s = s.toLowerCase().replaceAll("\\s", "");
        t = t.toLowerCase().replaceAll("\\s", "");

        System.out.println("Результат: " + isAnagram(s, t));

        // Тестовые примеры
        System.out.println("\nТестовые примеры:");
        System.out.println("Героин - регион: " + isAnagram("героин", "регион"));
        System.out.println("Клоака - околка: " + isAnagram("клоака", "околка"));
    }
}