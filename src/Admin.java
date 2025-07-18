
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;
import java.util.stream.Stream;

class Admin {
    public List<FoodItem> menu = new ArrayList<>();
    private ArrayList<Order> orderQueue = new ArrayList<Order>();
    private List<Order> completedOrders = new ArrayList<>();
    private Map<String, Customer> customerMap = new HashMap<>();
    private Map<FoodItem, Integer> popularItems = new HashMap<>();
    public int dailySales = 0;

    public void manageMenuItems() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Manage Menu Items ---");
            System.out.println("1. Add New Item");
            System.out.println("2. Update Item");
            System.out.println("3. Remove Item");
            System.out.println("4. Exit Menu Management");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> addNewItem(scanner);
                case 2 -> updateItem(scanner);
                case 3 -> removeItem(scanner);
                case 4 -> { return; }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void addNewItem(Scanner scanner) {
        System.out.print("Enter food name: ");
        String name = scanner.nextLine();
        System.out.print("Enter price: ");
        double price = scanner.nextDouble();
        scanner.nextLine();
        System.out.print("Enter category: ");
        String category = scanner.nextLine();
        System.out.print("Is it available (true/false)? ");
        boolean available = scanner.nextBoolean();
        scanner.nextLine();

        menu.add(new FoodItem(name, price, category, available));

//        menu.add(new FoodItem("coke",100,"drink",true));
//        menu.add(new FoodItem("wafers",10,"snack",true));
//        menu.add(new FoodItem("chips",50,"snack",true));
//        menu.add(new FoodItem("pepsi",100,"drink",false));

        writeMenuToFile();
        System.out.println("Item added successfully!");
    }

    private void updateItem(Scanner scanner) {
        System.out.print("Enter item name to update: ");
        String name = scanner.nextLine();
        FoodItem item = findItemByName(name);
        if (item == null) {
            System.out.println("Item not found!");
            return;
        }
        System.out.print("Enter new price: ");
        item.setPrice(scanner.nextDouble());
        scanner.nextLine();
        System.out.print("Is it available (true/false)? ");
        item.setAvailable(scanner.nextBoolean());
        scanner.nextLine();
        System.out.println("Item updated successfully!");
        writeMenuToFile();
    }

    private void removeItem(Scanner scanner) {
        System.out.print("Enter item name to remove: ");
        String name = scanner.nextLine();
        FoodItem item = findItemByName(name);
        if (item != null) {
            menu.remove(item);
            updatePendingOrders(item);
            System.out.println("Item removed successfully!");
        } else {
            System.out.println("Item not found!");
        }
        writeMenuToFile();
    }

    public FoodItem findItemByName(String name) {
        return menu.stream().filter(item -> item.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    private void updatePendingOrders(FoodItem item) {
        for (Order order : orderQueue) {
            if (order.getItems().containsKey(item)) {
                order.setStatus("Denied");
                orderQueue.removeFirst();
            }
        }
    }

    public void manageOrders() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            System.out.println("\n--- Manage Orders ---");
            System.out.println("1. View Pending Orders");
            System.out.println("2. Update Order Status");
            System.out.println("3. Process Refund");
            System.out.println("4. Process Next Order");
            System.out.println("5. Exit Order Management");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> viewPendingOrders();
                case 2 -> updateOrderStatus(scanner);
                case 3 -> processRefund(scanner);
                case 4 -> processNextOrder();
                case 5 -> { return; }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    private void viewPendingOrders() {
        if (orderQueue.isEmpty()) {
            System.out.println("No pending orders.");
            return;
        }
        System.out.println("\n--- Pending Orders ---");
        orderQueue.forEach(order -> {
            System.out.println("Customer: " + order.getCustomerName());
            Customer customer = customerMap.get(order.getCustomerName());
            if (customer != null) {
                System.out.println("Delivery Address: " + customer.getAddress());
            }
            System.out.println("Items:");
            order.getItems().forEach((item, quantity) ->
                    System.out.println("- " + item.getName() + " x" + quantity));
            if (order.getSpecialRequests() != null && !order.getSpecialRequests().isEmpty()) {
                System.out.println("Special Requests: " + order.getSpecialRequests());
            }
            System.out.println("Total: Rs. " + order.calculateTotal());
            System.out.println("------------------------");
        });
    }

    private void updateOrderStatus(Scanner scanner) {
        System.out.print("\nEnter customer name for order: ");
        String customerName = scanner.nextLine();

        Optional<Order> orderOpt = orderQueue.stream()
                .filter(o -> o.getCustomerName().equalsIgnoreCase(customerName))
                .findFirst();

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            System.out.println("Current status: " + order.getStatus());
            System.out.println("Select new status:");
            System.out.println("1. Preparing");
            System.out.println("2. Out for Delivery");
            System.out.println("3. Completed");
            System.out.println("4. Cancelled");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String newStatus = switch (choice) {
                case 1 -> "Preparing";
                case 2 -> "Out for Delivery";
                case 3 -> "Completed";
                case 4 -> "Cancelled";
                default -> order.getStatus();
            };

            order.setStatus(newStatus);

            Customer customer = customerMap.get(customerName);
            if (customer != null && customer.getCurrentOrder() == order) {
                customer.getCurrentOrder().setStatus(newStatus);
            }

            if ("Completed".equals(newStatus)) {
                completedOrders.add(order);
                orderQueue.remove(order);
                customer.addOrderToHistory(order);
            }
            else{
                writePendingOrdersToFile();
            }

            System.out.println("Order status updated successfully!");
        }
        else {
            System.out.println("Order not found for customer: " + customerName);
        }
    }

    private void processRefund(Scanner scanner) {
        System.out.println("\nEnter customer name for refund: ");
        String customerName = scanner.nextLine();

        Optional<Order> orderOpt = Stream.concat(orderQueue.stream(), completedOrders.stream())
                .filter(o -> o.getCustomerName().equalsIgnoreCase(customerName))
                .findFirst();

        if (orderOpt.isPresent()) {
            Order order = orderOpt.get();
            if (order.getStatus().equals("Cancelled")) {
                System.out.println("Processing refund of Rs. " + order.calculateTotal());
                dailySales -= order.calculateTotal();
                order.setStatus("Refunded");
                System.out.println("Refund processed successfully!");
                orderQueue.removeFirst();
            } else {
                System.out.println("Order must be cancelled before processing refund.");
            }
        } else {
            System.out.println("Order not found!");
        }
    }

    public void processNextOrder() {
        if (orderQueue.isEmpty()) {
            System.out.println("No orders to process.");
            return;
        }

        Order order = orderQueue.getFirst();
        order.setStatus("Completed");

        completedOrders.add(order);
        Customer customer = customerMap.get(order.getCustomerName());
        if (customer != null) {
            customer.addOrderToHistory(order);
        } else {
            System.out.println("Customer not found for this order.");
        }

        System.out.println("Processed " + order.getCustomerName() + "'s order successfully!");
        orderQueue.removeFirst();
    }



    public void generateSalesReport() {
        System.out.println("\n--- Daily Sales Report ---");
        System.out.println("Total Sales: Rs. " + dailySales);
        System.out.println("Total Orders Processed: " + completedOrders.size());
        System.out.println("\n--- Most Popular Items ---");
        popularItems.entrySet().stream()
                .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                .forEach(entry ->
                        System.out.println(entry.getKey().getName() + " sold " + entry.getValue() + " times")
                );
    }

    public List<FoodItem> getMenu() {
        return menu;
    }

    public void addOrder(Order order) {
        orderQueue.add(order);
        customerMap.putIfAbsent(order.getCustomerName(), new Customer(order.getCustomerName()));
        updatePopularItems(order);
        writePendingOrdersToFile();
    }

    private void updatePopularItems(Order order) {
        for (Map.Entry<FoodItem, Integer> entry : order.getItems().entrySet()) {
            FoodItem item = entry.getKey();
            int quantity = entry.getValue();
            popularItems.put(item, popularItems.getOrDefault(item, 0) + quantity);
        }
    }

    public void writePendingOrdersToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("pending_orders.txt"))) {
            for (Order order : orderQueue) {
                writer.write("Customer: " + order.getCustomerName() + "\n");
                writer.write("Status: " + order.getStatus() + "\n");
                writer.write("Items:\n");
                for (Map.Entry<FoodItem, Integer> entry : order.getItems().entrySet()) {
                    writer.write("  - " + entry.getKey().getName() + " x" + entry.getValue() + "\n");
                }
                writer.write("-------------------------------\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeMenuToFile() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("menu_items.txt"))) {
            for (FoodItem item : menu) {
                writer.write(item.getName() + "," + item.getPrice() + "," + item.getCategory() + "," + item.isAvailable());
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("Error writing menu items to file: " + e.getMessage());
        }
    }

}
