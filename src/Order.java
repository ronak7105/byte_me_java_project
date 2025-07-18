import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;

class Order implements Serializable {
    private static final long serialVersionUID = 1L;
    private String customerName;
    private Map<FoodItem, Integer> items = new HashMap<>();
    private String status = "Pending";
    private String specialRequests;
    private final LocalDateTime orderTime;

    public Order(String customerName) {
        this.customerName = customerName;
        this.orderTime = LocalDateTime.now();
    }

    public void setSpecialRequests(String specialRequests) {
        this.specialRequests = specialRequests;
    }

    public String getSpecialRequests() {
        return specialRequests;
    }

    public LocalDateTime getOrderTime() {
        return orderTime;
    }

    public void addItem(FoodItem item, int quantity) {
        items.put(item, quantity);
    }

    public int calculateTotal() {
        int total = 0;
        for (Map.Entry<FoodItem, Integer> entry : items.entrySet()) {
            total += entry.getKey().getPrice() * entry.getValue();
        }
        return total;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCustomerName() {
        return customerName;
    }

    public Map<FoodItem, Integer> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Order by " + customerName + " - Status: " + status;
    }

}
