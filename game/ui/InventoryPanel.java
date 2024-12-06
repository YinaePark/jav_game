package game.ui;

import domain.item.Item;
import domain.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;
/**
 * A panel that displays the player's inventory, allowing them to select and view items.
 * Each inventory slot can contain a specific item and quantity.
 */
public class InventoryPanel extends JPanel {
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int PANEL_PADDING = 10;

    private InventorySlot[][] slots;
    private Player player;
    private Item selectedItem;
    private GamePanel gamePanel;

    public InventoryPanel(Player player, GamePanel gamePanel) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.slots = new InventorySlot[ROWS][COLS];

        setPreferredSize(new Dimension(300, 300));
        initializeSlots();
        addMouseListener(new InventoryMouseListener());

        // Add listener to detect inventory changes
        player.addInventoryChangeListener(this::updateInventory);
    }

    /**
     * Initializes the inventory slots by creating them and setting their positions.
     */
    private void initializeSlots() {
        int slotSize = 50;
        int spacing = 5;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = PANEL_PADDING + j * (slotSize + spacing);
                int y = PANEL_PADDING + i * (slotSize + spacing);
                slots[i][j] = new InventorySlot(x, y);
            }
        }
    }

    /**
     * Updates the inventory display with the given list of items.
     * This method groups items by name and updates the slots accordingly.
     */
    public void updateInventory(List<Item> items) {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].setItem(null, 0);
            }
        }

        // Group items by their names
        Map<String, List<Item>> itemGroups = new HashMap<>();
        for (Item item : items) {
            itemGroups.computeIfAbsent(item.getName(), k -> new ArrayList<>()).add(item);
        }

        // Place grouped items into the inventory slots
        int slotIndex = 0;
        for (Map.Entry<String, List<Item>> entry : itemGroups.entrySet()) {
            if (slotIndex >= ROWS * COLS) break;

            int row = slotIndex / COLS;
            int col = slotIndex % COLS;
            slots[row][col].setItem(entry.getValue().get(0), entry.getValue().size());
            slotIndex++;
        }

        repaint();
    }

    /**
     * Paints the component, drawing the inventory background and the slots.
     * @param g The graphics context to use for painting
     */
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(200, 200, 200));
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].draw(g);
            }
        }
    }

    /**
     * Mouse listener to detect item selection from the inventory grid.
     */
    private class InventoryMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();

            // Deselect any previously selected item
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (slots[i][j].contains(point)) {
                        // Clear the previous selection
                        clearSelection();

                        // Select the clicked slot and update the selected item
                        slots[i][j].setSelected(true);
                        selectedItem = slots[i][j].getItem();

                        // Notify the game panel about the selected item
                        if(gamePanel != null){
                            gamePanel.setSelectedItem(selectedItem);
                        }
                        repaint();
                        return;
                    }
                }
            }
        }
    }

    /**
     * Clears the selection in all inventory slots.
     */
    private void clearSelection() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].setSelected(false);
            }
        }
    }
    public void setSelectedItem(Item item) {
        this.selectedItem = item;
    }
    public Item getSelectedItem() {
        return selectedItem;
    }
}
