import java.util.*;

public class Main{
    static Scanner scanner = new Scanner(System.in);
    private static Admin admin = new Admin();
    private static Map<String, Customer> customers = new HashMap<>();

    public static void main(String[] args) {
        System.out.println("Welcome to Byte Me!");

        while (true) {
            System.out.println("\nAre you an Admin or a Customer?");
            System.out.print("Enter 'admin' or 'customer' (or 'exit' to quit): ");
            String userType = scanner.nextLine().trim().toLowerCase();

            if (userType.equals("admin")) {
                adminMenu();
            } else if (userType.equals("customer")) {
                customerMenu();
            } else if (userType.equals("exit")) {
                System.out.println("Exiting Byte Me!");
                break;
            } else {
                System.out.println("Invalid input. Please try again.");
            }
        }
    }

    private static void adminMenu() {
        while (true) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Manage Menu Items");
            System.out.println("2. Manage Orders");
            System.out.println("3. Generate Daily Sales Report");
            System.out.println("4. Exit Admin Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> admin.manageMenuItems();
                case 2 -> admin.manageOrders();
                case 3 -> admin.generateSalesReport();
                case 4 -> {
                    System.out.println("Exiting Admin Menu.");
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }

    private static void customerMenu() {
        System.out.print("\nEnter your name: ");
        String name = scanner.nextLine().trim();
        Customer customer = customers.getOrDefault(name, new Customer(name));
        customers.put(name, customer);

        while (true) {
            System.out.println("\n--- Customer Menu ---");
            System.out.println("1. View and Browse Menu");
            System.out.println("2. Manage Cart");
            System.out.println("3. View Order Status");
            System.out.println("4. Cancel Order");
            System.out.println("5. View Order History");
            System.out.println("6. Exit Customer Menu");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1 -> customer.browseMenu(admin.getMenu());
                case 2 -> customer.manageCart(admin);
                case 3 -> customer.viewOrderStatus();
                case 4 -> customer.cancelOrder();
                case 5 -> customer.viewOrderHistory();
                case 6 -> {
                    System.out.println("Exiting Customer Menu.");
                    return;
                }
                default -> System.out.println("Invalid choice, try again.");
            }
        }
    }
}