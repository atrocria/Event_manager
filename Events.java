import javax.swing.*;
import javax.swing.table.DefaultTableModel;

import java.awt.*;

public class Events extends JFrame {
    private JTextField nameField = new JTextField(20);
    private JButton addButton = new JButton("Add Event");
    private JTable eventTable = new JTable();

    // 1. You must "create" the DAO so the button can use it
    private EventDAO eventDAO = new EventDAO();

    public Events() {
        setTitle("Event Management System");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new FlowLayout());

        add(new JLabel("Event Name:"));
        add(nameField);
        add(addButton);

        // Task 5: CREATE Operation trigger
        addButton.addActionListener(e -> {
            String name = nameField.getText();

            // Check if name is empty before saving
            if (name.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Please enter a name!");
                return;
            }

            // 2. This calls the DAO to talk to XAMPP
            eventDAO.addEvent(new Event(name, "2026-10-12"));

            JOptionPane.showMessageDialog(this, "Event Added to Database!");
            nameField.setText(""); // Clear the field for next entry
        });

        // 1. Create the column names
        String[] columns = {"ID", "Event Name", "Date"};
        DefaultTableModel model = new DefaultTableModel(columns, 0);
        eventTable.setModel(model);

        // 2. Put the table in a "ScrollPane" (so you can scroll if there are many events)
        JScrollPane scrollPane = new JScrollPane(eventTable);
        scrollPane.setPreferredSize(new Dimension(450, 200));

        // 3. Add it to the window
        add(scrollPane);
    }
}