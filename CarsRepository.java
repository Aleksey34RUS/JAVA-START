import java.util.List;

public interface CarsRepository {
    List<Car> getAllCars();
    void saveCars(List<Car> cars);
}