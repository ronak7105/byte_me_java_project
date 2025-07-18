import java.io.Serializable;

class FoodItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String name;
    private double price;
    private String category;
    private boolean available;

    public FoodItem(String name, double price, String category, boolean available) {
        this.name = name;
        this.price = price;
        this.category = category;
        this.available = available;
    }

    public String getName() {
        return name;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }

    @Override
    public String toString() {
        return name + " - Rs. " + price + " (" + category + ") - " + (available ? "Available" : "Not Available");
    }

    public double getPrice() {
        return price;
    }

    public String getCategory() {
        return category;
    }

    public boolean isAvailable() {
        return available;
    }

}
