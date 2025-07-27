import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Person {
    private String name;
    private double money;
    private List<Product> products;

    public Person(String name, double money) {
        setName(name);
        setMoney(money);
        this.products = new ArrayList<>();
    }

    // Геттеры и сеттеры с валидацией
    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Имя не может быть пустым");
        }
        if (name.length() < 3) {
            throw new IllegalArgumentException("Имя не может быть короче 3 символов");
        }
        this.name = name;
    }

    public double getMoney() {
        return money;
    }

    public void setMoney(double money) {
        if (money < 0) {
            throw new IllegalArgumentException("Деньги не могут быть отрицательными");
        }
        this.money = money;
    }

    public List<Product> getProducts() {
        return products;
    }

    // Логика покупки продукта
    public void buyProduct(Product product) {
        if (money >= product.getPrice()) {
            products.add(product);
            money -= product.getPrice();
            System.out.println(name + " купил(а) " + product.getName());
        } else {
            System.out.println(name + " не может позволить себе " + product.getName());
        }
    }

    // Переопределение стандартных методов
    @Override
    public String toString() {
        if (products.isEmpty()) {
            return name + " - Ничего не куплено";
        }
        return name + " - " + String.join(", ", products.stream().map(Product::toString).toArray(String[]::new));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Person person = (Person) o;
        return Double.compare(money, person.money) == 0 &&
                Objects.equals(name, person.name) &&
                Objects.equals(products, person.products);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, money, products);
    }
}