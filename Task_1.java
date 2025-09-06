import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public class Task_1 {
    public static <T> Set<T> getUniqueElements(ArrayList<T> list) {
        return new HashSet<>(list); // HashSet автоматически удаляет дубликаты
    }

    public static void main(String[] args) {
        ArrayList<Integer> numbers = new ArrayList<>();
        numbers.add(1);
        numbers.add(2);
        numbers.add(3);
        numbers.add(2);
        numbers.add(12);
        numbers.add(10);
        numbers.add(8);
        numbers.add(8);
        numbers.add(4);
        numbers.add(12);
        numbers.add(12);
        numbers.add(12);
        Set<Integer> uniqueNumbers = getUniqueElements(numbers);
        System.out.println("Уникальные элементы: " + uniqueNumbers);
    }
}