import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        String ring = "pasdfghjklzxcvbnmqwertyuiop";
        Scanner scanner = new Scanner(System.in);
        char inputChar = scanner.next().charAt(0);
        int index = ring.indexOf(inputChar);
        int prevIndex = (index - 1 + 26) % 26;
        System.out.println(ring.charAt(prevIndex));
        scanner.close();
    }
}