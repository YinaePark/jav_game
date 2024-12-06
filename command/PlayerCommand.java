package command;

import domain.player.Player;
import javax.swing.*;
import java.awt.*;

/**
 * Command to display the player's status, including money and inventory.
 */
public class PlayerCommand implements Command {
    private final Player player;

    public PlayerCommand(Player player) {
        this.player = player;
    }

    @Override
    public void execute(String[] args) {
        // Create and configure the dialog
        JDialog playerDialog = new JDialog();
        playerDialog.setTitle("Player Status");
        playerDialog.setSize(300, 200);
        playerDialog.setLocationRelativeTo(null);
        playerDialog.setModal(true);

        // Main container panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel to display player information
        JPanel infoPanel = new JPanel(new GridLayout(0, 1, 5, 5));

        // Display player's money
        JLabel moneyLabel = new JLabel("Money: $" + player.getMoney());
        moneyLabel.setFont(new Font("Arial", Font.BOLD, 16));
        infoPanel.add(moneyLabel);

        // Inventory label
        JLabel inventoryLabel = new JLabel("Inventory:");
        inventoryLabel.setFont(new Font("Arial", Font.BOLD, 14));
        infoPanel.add(inventoryLabel);

        // Inventory list
        DefaultListModel<String> inventoryModel = new DefaultListModel<>();
        player.getInventory().forEach(item -> inventoryModel.addElement(item.toString())); // Add items to inventory list

        JList<String> inventoryList = new JList<>(inventoryModel);
        JScrollPane scrollPane = new JScrollPane(inventoryList);

        // Add components to the main panel
        mainPanel.add(infoPanel, BorderLayout.NORTH); // Add player info at the top
        mainPanel.add(scrollPane, BorderLayout.CENTER); // Add inventory list in the center

        // Close button
        JButton closeButton = new JButton("Close");
        closeButton.addActionListener(e -> playerDialog.dispose());
        mainPanel.add(closeButton, BorderLayout.SOUTH); // Add close button at the bottom

        // Add the main panel to the dialog and display it
        playerDialog.add(mainPanel);
        playerDialog.setVisible(true);
    }
}
