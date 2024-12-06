package game.ui;

import domain.item.Item;
import domain.item.ShopItemManager;
import domain.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

public class ShopPanel extends JPanel {
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int PANEL_PADDING = 10;

    private ShopSlot[][] slots;
    private Player player;
    private GamePanel gamePanel;  // GamePanel 참조
    private boolean isVisible = false;  // ShopPanel 보이기/숨기기 여부

    public ShopPanel(Player player, GamePanel gamePanel) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.slots = new ShopSlot[ROWS][COLS];

        setPreferredSize(new Dimension(300, 300));
        initializeSlots();
        addMouseListener(new ShopMouseListener());
        updateShop(ShopItemManager.getAllItems()); // Load available items into the shop
    }

    private void initializeSlots() {
        int slotSize = 50;
        int spacing = 5;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = PANEL_PADDING + j * (slotSize + spacing);
                int y = PANEL_PADDING + i * (slotSize + spacing);
                slots[i][j] = new ShopSlot(x, y);
            }
        }
    }

    // Paint the ShopPanel with background and slots
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Set background color to dark gray
        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());

        // Draw each shop slot on the panel
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].draw(g); // Draw each slot
            }
        }
    }


    private class ShopMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();

            // Check each slot for a click
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (slots[i][j].contains(point)) { // Check if the click was inside the slot
                        Item selectedItem = slots[i][j].getItem(); // Get the item in the clicked slot

                        // Show input dialog for entering item quantity
                        String input = JOptionPane.showInputDialog(ShopPanel.this,
                                "Enter quantity for " + selectedItem.getName() + ":");
                        if (input != null) {
                            try {
                                int quantity = Integer.parseInt(input);
                                if (quantity > 0 && player.getMoney() >= selectedItem.getPrice() * quantity) {
                                    // Proceed with purchase if valid quantity and sufficient funds
                                    player.spendMoney(selectedItem.getPrice() * quantity);
                                    for (int q = 0; q < quantity; q++) {
                                        player.addItem(selectedItem);  // Add item to player's inventory
                                    }
                                    JOptionPane.showMessageDialog(ShopPanel.this, "Purchased " + quantity + " " + selectedItem.getName());
                                } else {
                                    // Show error if insufficient funds or invalid quantity
                                    JOptionPane.showMessageDialog(ShopPanel.this, "Insufficient funds or invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException ex) {
                                // Show error if the input is not a valid number
                                JOptionPane.showMessageDialog(ShopPanel.this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        }
    }

    // Update the shop with new available items
    public void updateShop(List<Item> availableItems) {
        int index = 0;
        for (Item item : availableItems) {
            if (index >= ROWS * COLS) break; // Stop if there are more items than available slots
            int row = index / COLS;
            int col = index % COLS;
            slots[row][col].setItem(item); // Set item in the respective slot
            index++;
        }
    }
}