import java.util.Scanner;

public class DZ_3_zd_1 {
    public static void main(String[] args) {
        // Создаем объект Scanner для чтения ввода с клавиатуры
        Scanner scanner = new Scanner(System.in);

        // Запрашиваем имя пользователя
        System.out.print("Введите ваше имя: ");
        String name = scanner.nextLine(); // Читаем всю введенную строку

        // Выводим приветствие
        System.out.println("Привет, " + name);

        // Закрываем Scanner (рекомендуется для освобождения ресурсов)
        scanner.close();
    }
}