import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class CarsRepositoryImpl implements CarsRepository {
    private final String fileName;

    public CarsRepositoryImpl(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public List<Car> getAllCars() {
        List<Car> cars = new ArrayList<>();
        File file = new File(fileName);

        // Создаем файл, если он не существует
        if (!file.exists()) {
            try {
                if (file.createNewFile()) {
                    System.out.println("Файл " + fileName + " создан. Добавьте данные в файл.");
                }
            } catch (IOException e) {
                System.out.println("Ошибка при создании файла: " + e.getMessage());
            }
            return cars; // Возвращаем пустой список
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(fileName))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split("\\|");
                if (parts.length == 5) {
                    try {
                        Car car = new Car(
                                parts[0].trim(),
                                parts[1].trim(),
                                parts[2].trim(),
                                Long.parseLong(parts[3].trim()),
                                Long.parseLong(parts[4].trim())
                        );
                        cars.add(car);
                    } catch (NumberFormatException e) {
                        System.out.println("Ошибка формата числа в строке: " + line);
                    }
                } else {
                    System.out.println("Некорректная строка в файле: " + line);
                }
            }
        } catch (IOException e) {
            System.out.println("Ошибка чтения файла: " + e.getMessage());
        }
        return cars;
    }

    @Override
    public void saveCars(List<Car> cars) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(fileName))) {
            for (Car car : cars) {
                writer.write(String.format("%s|%s|%s|%d|%d\n",
                        car.getNumber(),
                        car.getModel(),
                        car.getColor(),
                        car.getMileage(),
                        car.getCost()));
            }
        } catch (IOException e) {
            System.out.println("Ошибка записи в файл: " + e.getMessage());
        }
    }
}