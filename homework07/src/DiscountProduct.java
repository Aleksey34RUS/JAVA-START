import java.time.LocalDate;
import java.util.Objects;

public class DiscountProduct extends Product {
    private double discount;
    private LocalDate expiryDate;

    public DiscountProduct(String name, double basePrice, double discount, LocalDate expiryDate) {
        super(name, basePrice);
        setDiscount(discount);
        this.expiryDate = expiryDate;
    }

    public double getDiscount() {
        return discount;
    }

    public void setDiscount(double discount) {
        if (discount < 0) {
            throw new IllegalArgumentException("Скидка не может быть отрицательной");
        }
        if (discount > super.getPrice()) {
            throw new IllegalArgumentException("Скидка не может превышать цену продукта");
        }
        this.discount = discount;
    }

    public LocalDate getExpiryDate() {
        return expiryDate;
    }

    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }

    @Override
    public double getPrice() {
        if (isDiscountActive()) {
            double discountedPrice = super.getPrice() - discount;
            return discountedPrice < 0 ? 0 : discountedPrice;
        }
        return super.getPrice();
    }

    private boolean isDiscountActive() {
        LocalDate today = LocalDate.now();
        return today.isBefore(expiryDate) || today.isEqual(expiryDate);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        if (!super.equals(o)) return false;
        DiscountProduct that = (DiscountProduct) o;
        return Double.compare(discount, that.discount) == 0 &&
                Objects.equals(expiryDate, that.expiryDate);
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), discount, expiryDate);
    }
}