import java.io.*;
import java.text.DecimalFormat;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static void main(String[] args) {
        CarsRepository repository = new CarsRepositoryImpl("cars.txt");
        List<Car> cars = repository.getAllCars();

        // Если файл пустой, добавляем тестовые данные
        if (cars.isEmpty()) {
            System.out.println("Добавление тестовых данных в файл...");
            cars.add(new Car("a123me", "Mercedes", "White", 0, 8300000));
            cars.add(new Car("b873of", "Volga", "Black", 0, 673000));
            cars.add(new Car("w487mn", "Lexus", "Grey", 76000, 900000));
            cars.add(new Car("p987hj", "Volga", "Red", 610, 704340));
            cars.add(new Car("c987ss", "Toyota", "White", 254000, 761000));
            cars.add(new Car("o983op", "Toyota", "Black", 698000, 740000));
            cars.add(new Car("p146op", "BMW", "White", 271000, 850000));
            cars.add(new Car("u893ii", "Toyota", "Purple", 210900, 440000));
            cars.add(new Car("l097df", "Toyota", "Black", 108000, 780000));
            cars.add(new Car("y876wd", "Toyota", "Black", 160000, 1000000));

            repository.saveCars(cars);
            cars = repository.getAllCars(); // Перезагружаем данные
        }

        // Выводим результаты в консоль
        printResults(cars, System.out);

        // Выводим результаты в файл
        try (PrintWriter writer = new PrintWriter(new FileWriter("output.txt"))) {
            printResults(cars, writer);
            System.out.println("Результаты сохранены в файл output.txt");
        } catch (IOException e) {
            System.out.println("Ошибка при работе с файлом output.txt: " + e.getMessage());
        }
    }

    private static void printResults(List<Car> cars, PrintStream stream) {
        PrintWriter writer = new PrintWriter(stream);
        printResults(cars, writer);
        writer.flush();
    }

    private static void printResults(List<Car> cars, PrintWriter writer) {
        writer.println("Автомобили в базе:");
        writer.printf("%-8s %-9s %-8s %-7s %s\n", "Number", "Model", "Color", "Mileage", "Cost");
        cars.forEach(car -> writer.println(car.toString()));

        // Заданные параметры из условия
        String colorToFind = "Black";
        long mileageToFind = 0L;
        long n = 700000;
        long m = 800000;
        String modelToFind1 = "Toyota";
        String modelToFind2 = "Volvo";

        // 1) Номера автомобилей по цвету или пробегу
        writer.println("\nНомера автомобилей по цвету или пробегу:");
        String numbers = cars.stream()
                .filter(car -> car.getColor().equals(colorToFind) || car.getMileage() == mileageToFind)
                .map(Car::getNumber)
                .collect(Collectors.joining(" "));
        writer.println(numbers);

        // 2) Количество уникальных моделей в ценовом диапазоне
        long uniqueModelsCount = cars.stream()
                .filter(car -> car.getCost() >= n && car.getCost() <= m)
                .map(Car::getModel)
                .distinct()
                .count();
        writer.println("Уникальные автомобили: " + uniqueModelsCount + " шт.");

        // 3) Цвет автомобиля с минимальной стоимостью
        String minCostColor = cars.stream()
                .min((c1, c2) -> Long.compare(c1.getCost(), c2.getCost()))
                .map(Car::getColor)
                .orElse("Не найден");
        writer.println("Цвет автомобиля с минимальной стоимостью: " + minCostColor);

        // 4) Средняя стоимость искомой модели
        DecimalFormat df = new DecimalFormat("#,##0.00");
        double avg1 = cars.stream()
                .filter(car -> car.getModel().equals(modelToFind1))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0.0);
        writer.println("Средняя стоимость модели " + modelToFind1 + ": " + df.format(avg1));

        double avg2 = cars.stream()
                .filter(car -> car.getModel().equals(modelToFind2))
                .mapToLong(Car::getCost)
                .average()
                .orElse(0.0);
        writer.println("Средняя стоимость модели " + modelToFind2 + ": " + df.format(avg2));
    }
}