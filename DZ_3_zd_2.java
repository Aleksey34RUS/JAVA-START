import java.util.Scanner;

public class DZ_3_zd_2 {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        // Запрос хода у Васи
        System.out.print("Вася, введите число (0-камень, 1-ножницы, 2-бумага): ");
        int vasya = scanner.nextInt();

        // Запрос хода у Пети
        System.out.print("Петя, введите число (0-камень, 1-ножницы, 2-бумага): ");
        int petya = scanner.nextInt();

        // Проверка корректности ввода
        if (vasya < 0 || vasya > 2 || petya < 0 || petya > 2) {
            System.out.println("Ошибка! Введите числа от 0 до 2.");
            return; // Завершаем программу при ошибке
        }

        // Определение победителя
        if (vasya == petya) {
            System.out.println("Ничья!");
        } else if (
                (vasya == 0 && petya == 1) || // Камень vs ножницы
                        (vasya == 1 && petya == 2) ||  // Ножницы vs бумага
                        (vasya == 2 && petya == 0)     // Бумага vs камень
        ) {
            System.out.println("Вася победил!");
        } else {
            System.out.println("Петя победил!");
        }

        scanner.close();
    }
}