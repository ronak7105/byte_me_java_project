import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class PendingOrdersGUI extends JFrame {
    private JTable ordersTable;
    private DefaultTableModel tableModel;

    public PendingOrdersGUI() {
        setTitle("Pending Orders");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Customer Name");
        tableModel.addColumn("Items Ordered");
        tableModel.addColumn("Status");

        ordersTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(ordersTable);
        add(scrollPane);

        loadPendingOrdersFromFile();

        JButton toMenuButton = new JButton("Go to Menu");
        toMenuButton.addActionListener(e -> {
            dispose();
            new MenuGUI();
        });
        add(toMenuButton, BorderLayout.SOUTH);
    }

    private void loadPendingOrdersFromFile() {
        File file = new File("pending_orders.txt");
        if (!file.exists()) {
            JOptionPane.showMessageDialog(this, "No pending orders found.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            List<String> orderData = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("Customer: ")) {
                    if (!orderData.isEmpty()) {
                        displayOrderInTable(orderData);
                        orderData.clear();
                    }
                    orderData.add(line.substring(10));
                } else if (line.startsWith("Status: ")) {
                    orderData.add(line.substring(8));
                } else if (line.startsWith("  - ")) {
                    orderData.add(line.substring(4));
                }
            }
            if (!orderData.isEmpty()) {
                displayOrderInTable(orderData);
            }
        } catch (IOException e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to load pending orders.", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void displayOrderInTable(List<String> orderData) {
        String customerName = orderData.get(0);
        String status = orderData.get(1);
        String items = String.join(", ", orderData.subList(2, orderData.size()));
        tableModel.addRow(new Object[] { customerName, items, status });
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new PendingOrdersGUI().setVisible(true);
        });
    }
}
