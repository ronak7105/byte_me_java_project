import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

class Customer {
    private static String name;
    private List<Order> orderHistory = new ArrayList<>();
    private Order currentOrder;
    private Map<FoodItem, Integer> cart = new HashMap<>();
    private String address;
    private static final String FILE_EXTENSION = ".txt";
    private static final String CART_FILE = "cart_" + name + ".dat";

    public Customer(String name) {
        this.name = name;
        loadOrderHistory();
        loadCart();
    }

    public void browseMenu(List<FoodItem> menu) {
        System.out.println("\n--- Menu ---");
        menu.forEach(System.out::println);
        while (true){
            System.out.println("\nOptions: ");
            System.out.println("1. Search item by name");
            System.out.println("2. Filter by category");
            System.out.println("3. Sort by price");
            System.out.println("4. Exit browse menu");
            System.out.print("Enter your choice: ");
            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> {
                    System.out.print("Enter name or keyword: ");
                    String keyword = scanner.nextLine().trim().toLowerCase();
                    menu.stream()
                            .filter(item -> item.getName().toLowerCase().contains(keyword))
                            .forEach(System.out::println);
                }
                case 2 -> {
                    System.out.print("Enter category: ");
                    String category = scanner.nextLine().trim().toLowerCase();
                    menu.stream()
                            .filter(item -> item.getCategory().equalsIgnoreCase(category))
                            .forEach(System.out::println);
                }
                case 3 -> {
                    System.out.print("Sort by price (asc/desc): ");
                    String sortOrder = scanner.nextLine().trim().toLowerCase();
                    List<FoodItem> sortedMenu = menu.stream()
                            .sorted(Comparator.comparing(FoodItem::getPrice))
                            .collect(Collectors.toList());
                    if (sortOrder.equals("desc")) {
                        Collections.reverse(sortedMenu);
                    }
                    sortedMenu.forEach(System.out::println);
                }
                case 4 -> {
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    public void manageCart(Admin admin) {
        while (true) {
            System.out.println("\n--- Manage Cart ---");
            System.out.println("1. Add Item");
            System.out.println("2. Modify Quantity");
            System.out.println("3. Remove Item");
            System.out.println("4. View Cart Total");
            System.out.println("5. Place order");
            System.out.println("6. Exit Cart");
            System.out.print("Enter your choice: ");
            int choice = Main.scanner.nextInt();
            Main.scanner.nextLine();

            switch (choice) {
                case 1 -> addItemToCart(admin);
                case 2 -> modifyCartItem();
                case 3 -> removeItemFromCart();
                case 4 -> viewCartTotal();
                case 5 -> placeOrder(admin);
                case 6 -> {return;}
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    public void addItemToCart(Admin admin) {
        System.out.print("Enter item name: ");
        String name = Main.scanner.nextLine();
        FoodItem item = admin.findItemByName(name);

        if (item != null && item.isAvailable()) {
            System.out.print("Enter quantity: ");
            int quantity = Main.scanner.nextInt();
            Main.scanner.nextLine();
            cart.put(item, cart.getOrDefault(item, 0) + quantity);
            saveCart();
            System.out.println("Item added to cart.");
        } else {
            System.out.println("Item not found or unavailable.");
        }
    }

    private void modifyCartItem() {
        System.out.print("Enter item name to modify quantity: ");
        String name = Main.scanner.nextLine();
        Optional<FoodItem> item = cart.keySet().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst();

        if (item.isPresent()) {
            System.out.print("Enter new quantity: ");
            int quantity = Main.scanner.nextInt();
            Main.scanner.nextLine();
            if (quantity > 0) {
                cart.put(item.get(), quantity);
                saveCart();
                System.out.println("Quantity updated.");
            } else {
                cart.remove(item.get());
                saveCart();
                System.out.println("Item removed from cart.");
            }
        } else {
            System.out.println("Item not found in cart.");
        }
    }

    private void removeItemFromCart() {
        System.out.print("Enter item name to remove: ");
        String name = Main.scanner.nextLine();
        Optional<FoodItem> item = cart.keySet().stream().filter(i -> i.getName().equalsIgnoreCase(name)).findFirst();

        if (item.isPresent()) {
            cart.remove(item.get());
            saveCart();
            System.out.println("Item removed from cart.");
        } else {
            System.out.println("Item not found in cart.");
        }
    }

    private void viewCartTotal() {
        double total = cart.entrySet().stream()
                .mapToDouble(e -> e.getKey().getPrice() * e.getValue())
                .sum();
        System.out.println("Total Cart Value: Rs. " + total);
    }

    public Order getCurrentOrder() {
        return currentOrder;
    }

    public void placeOrder(Admin admin) {
        if (cart.isEmpty()) {
            System.out.println("Cart is empty! Add items before placing an order.");
            return;
        }

        currentOrder = new Order(name);
        cart.forEach(currentOrder::addItem);
        admin.addOrder(currentOrder);
        orderHistory.add(currentOrder);
        saveOrderHistory();

        System.out.print("Any special requests? (Enter 'none' if none): ");
        String specialRequests = Main.scanner.nextLine().trim();
        if (!specialRequests.equalsIgnoreCase("none")) {
            currentOrder.setSpecialRequests(specialRequests);
        }
        System.out.println("Enter payment details: ");
        String pay = Main.scanner.nextLine();
        System.out.println("Enter delivery address: ");
        this.address = Main.scanner.nextLine();

        admin.dailySales += currentOrder.calculateTotal();

        System.out.println("Order placed successfully!");
        cart.clear();
    }

    public String getAddress() {
        return address;
    }


    public void addOrderToHistory(Order order) {
        orderHistory.add(order);
    }

    public void viewOrderStatus() {
        if (currentOrder != null) {
            if (Objects.equals(currentOrder.getStatus(), "Pending")) {
                System.out.println("Current Order Status: Order Received");
            }
            else{
                System.out.println("Current Order Status: " + currentOrder.getStatus());
            }
        } else {
            System.out.println("No active order.");
        }
    }

    public void cancelOrder() {
        if (currentOrder != null && !Objects.equals(currentOrder.getStatus(), "Completed")) {
            currentOrder.setStatus("Cancelled");
            System.out.println("Order cancelled successfully!");
        } else {
            System.out.println("No active order to cancel.");
        }
    }

    public void viewOrderHistory() {
        System.out.println("\n--- Order History ---");
        if (orderHistory.isEmpty()) {
            System.out.println("No orders found.");
        } else {
            for (Order order : orderHistory) {
                System.out.println("Order by " + name);
                System.out.println("Items Ordered:");
                for (Map.Entry<FoodItem, Integer> entry : order.getItems().entrySet()) {
                    System.out.println("- " + entry.getKey().getName() + " x" + entry.getValue() +
                            " @ Rs. " + entry.getKey().getPrice() + " each");
                }
                System.out.println("Total Price: Rs. " + order.calculateTotal());
                System.out.println("Order Time: " + order.getOrderTime());
                System.out.println("----------------------------------");
            }
        }
    }


    public void saveOrderHistory() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(name + FILE_EXTENSION))) {
            out.writeObject(orderHistory);
        } catch (IOException e) {
            System.out.println("Error saving order history: " + e.getMessage());
        }
    }

    private void loadOrderHistory() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(name + FILE_EXTENSION))) {
            orderHistory = (List<Order>) in.readObject();
        }
        catch (IOException | ClassNotFoundException e) {}
    }

    private void saveCart() {
        try (ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(CART_FILE))) {
            out.writeObject(cart);
        } catch (IOException e) {
            System.out.println("Error saving cart: " + e.getMessage());
        }
    }

    private void loadCart() {
        try (ObjectInputStream in = new ObjectInputStream(new FileInputStream(CART_FILE))) {
            cart = (Map<FoodItem, Integer>) in.readObject();
        } catch (IOException | ClassNotFoundException e) {}
    }

    public Map<FoodItem, Integer> getCart() {
        return cart;
    }
}
