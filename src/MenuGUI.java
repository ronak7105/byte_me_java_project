import javax.swing.*;
import java.awt.*;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import javax.swing.table.DefaultTableModel;

public class MenuGUI extends JFrame {
    private JTable menuTable;
    private DefaultTableModel tableModel;

    public MenuGUI() {
        setTitle("Canteen Menu");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Price");
        tableModel.addColumn("Category");
        tableModel.addColumn("Available");

        menuTable = new JTable(tableModel);

        JScrollPane scrollPane = new JScrollPane(menuTable);
        add(scrollPane, BorderLayout.CENTER);

        loadMenuItemsFromFile();
        JButton toOrdersButton = new JButton("Go to Orders");
        toOrdersButton.addActionListener(e -> {
            dispose();
            SwingUtilities.invokeLater(() -> new PendingOrdersGUI().setVisible(true));
        });
        add(toOrdersButton, BorderLayout.SOUTH);

        setVisible(true);
    }

    private void loadMenuItemsFromFile() {
        try (BufferedReader reader = new BufferedReader(new FileReader("menu_items.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length == 4) {
                    String name = parts[0];
                    double price = Double.parseDouble(parts[1]);
                    String category = parts[2];
                    boolean available = Boolean.parseBoolean(parts[3]);

                    tableModel.addRow(new Object[]{name, price, category, available});
                }
            }
        } catch (IOException e) {
            JOptionPane.showMessageDialog(this, "Error reading menu items from file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(MenuGUI::new);
    }
}
