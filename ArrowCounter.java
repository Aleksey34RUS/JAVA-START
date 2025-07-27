import java.util.Scanner;

public class ArrowCounter {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        int count = 0;
        int n = input.length();

        // Перебираем все возможные стартовые позиции для стрелок
        for (int i = 0; i <= n - 5; i++) {
            // Проверка на стрелку ">>-->"
            if (input.charAt(i) == '>'
                    && input.charAt(i + 1) == '>'
                    && input.charAt(i + 2) == '-'
                    && input.charAt(i + 3) == '-'
                    && input.charAt(i + 4) == '>') {
                count++;
            }
            // Проверка на стрелку "<--<<"
            else if (input.charAt(i) == '<'
                    && input.charAt(i + 1) == '-'
                    && input.charAt(i + 2) == '-'
                    && input.charAt(i + 3) == '<'
                    && input.charAt(i + 4) == '<') {
                count++;
            }
        }

        System.out.println(count);
        scanner.close();
    }
}