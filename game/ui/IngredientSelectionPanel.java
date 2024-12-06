package game.ui;

import game.entity.NormalCustomer;
import domain.item.Item;
import domain.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A dialog panel that allows the player to select ingredients for a customer.
 * The player can choose ingredients from their inventory and submit them.
 */
public class IngredientSelectionPanel extends JDialog {
    public static final int MAX_INGREDIENTS = 5;  // Maximum number of ingredients that can be selected
    private InventoryPanel inventoryPanel;
    private Player player;
    private Item[] selectedIngredients;
    private JPanel ingredientSlotsPanel;
    private ActionListener submitListener;
    private NormalCustomer normalCustomer;
    private GamePanel gamePanel;
    private GameWindow gameWindow;

    /**
     * Constructor to initialize the ingredient selection panel.
     * @param owner The parent frame
     */
    public IngredientSelectionPanel(Frame owner, Player player, NormalCustomer normalCustomer, GamePanel gamePanel, GameWindow gameWindow) {
        super(owner, "Select Ingredients", true);
        this.player = player;
        this.normalCustomer = normalCustomer;
        this.gamePanel = gamePanel;
        this.selectedIngredients = new Item[MAX_INGREDIENTS];  // Initialize the selected ingredients array
        this.gameWindow = gameWindow;
        initialize();
    }

    /**
     * Handles the submit button click event. It updates the customer's satisfaction, calculates the reward,
     * and awards the player the corresponding reward.
     */
    private void onSubmitButtonClicked() {
        if (player != null && selectedIngredients != null) {
            List<String> selectedIngredientNames = new ArrayList<>();
            // Collect the names of selected ingredients
            for (Item ingredient : selectedIngredients) {
                if (ingredient != null) {
                    selectedIngredientNames.add(ingredient.getName());
                }
            }
            // Update customer satisfaction based on the selected ingredients
            normalCustomer.updateSatisfaction(selectedIngredientNames);

            // Calculate the reward for the player
            int reward = normalCustomer.calculateReward();
            player.earnMoney(reward);
            JOptionPane.showMessageDialog(this, "You have earned " + reward + " euros!");

            // Remove the customer from the game window
            gameWindow.removeCustomer(normalCustomer);
        }

        // Close the dialog
        dispose();
    }

    /**
     * Initializes the panel by setting up the inventory panel, ingredient slots, and submit button.
     */
    private void initialize() {
        setLayout(new BorderLayout());

        // Create the inventory panel to display the player's items
        inventoryPanel = new InventoryPanel(player, null);  // GamePanel is not needed here, passing null
        add(inventoryPanel, BorderLayout.CENTER);
        inventoryPanel.updateInventory(player.getInventory());  // Update the inventory display

        // Create the ingredient slots panel with a fixed grid layout
        ingredientSlotsPanel = new JPanel(new GridLayout(1, MAX_INGREDIENTS));
        for (int i = 0; i < MAX_INGREDIENTS; i++) {
            final int index = i;
            JLabel slotLabel = new JLabel("Slot " + (i + 1));
            slotLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));  // Add border around each slot
            slotLabel.setPreferredSize(new Dimension(80, 80));  // Set fixed size for the slot
            slotLabel.setHorizontalAlignment(SwingConstants.CENTER);  // Center text and icon within the slot
            // Add a mouse listener to toggle the ingredient selection when clicked
            slotLabel.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mouseClicked(java.awt.event.MouseEvent e) {
                    toggleIngredientSlot(index);
                }
            });
            ingredientSlotsPanel.add(slotLabel);
        }
        add(ingredientSlotsPanel, BorderLayout.SOUTH);

        // Add the submit button at the top
        JButton submitButton = new JButton("Submit");
        submitButton.addActionListener(e -> onSubmitButtonClicked());  // Call the onSubmitButtonClicked method when clicked
        add(submitButton, BorderLayout.NORTH);

        setSize(400, 500);  // Set dialog window size
        setLocationRelativeTo(null);  // Center the dialog on the screen
    }

    /**
     * Updates the display of a specific ingredient slot based on the selected item.
     */
    private void updateIngredientSlot(int slotIndex, Item selectedItem) {
        JLabel slotLabel = (JLabel) ingredientSlotsPanel.getComponent(slotIndex);

        if (selectedItem != null) {
            // Display the item icon and name if an item is selected
            slotLabel.setIcon(new ImageIcon(selectedItem.getSprite(50, 50)));  // Display resized item sprite
            slotLabel.setText(selectedItem.getName());  // Display item name
            slotLabel.setHorizontalTextPosition(SwingConstants.CENTER);  // Align text horizontally
            slotLabel.setVerticalTextPosition(SwingConstants.BOTTOM);  // Align text below the icon
        } else {
            // Clear the slot if no item is selected
            slotLabel.setIcon(null);
            slotLabel.setText("");  // Remove the item name
        }

        slotLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));  // Reapply the border around the slot
    }

    /**
     * Toggles the ingredient in the selected slot: if an ingredient is already present, remove it;
     * otherwise, add the selected item from the inventory to the slot.
     * @param slotIndex The index of the slot to toggle
     */
    private void toggleIngredientSlot(int slotIndex) {
        // If the slot already contains an item, remove it from the slot and return it to the player's inventory
        if (selectedIngredients[slotIndex] != null) {
            Item itemToRemove = selectedIngredients[slotIndex];
            player.addItem(itemToRemove);  // Add the item back to the player's inventory
            selectedIngredients[slotIndex] = null;  // Clear the slot
            updateIngredientSlot(slotIndex, null);  // Update the slot UI to reflect the removal
            inventoryPanel.updateInventory(player.getInventory());  // Update the inventory display
            JOptionPane.showMessageDialog(this, itemToRemove.getName() + " has been removed from Slot " + (slotIndex + 1));

            // Deselect the currently selected item in the inventory
            inventoryPanel.setSelectedItem(null);
        } else {
            // If the slot is empty, place the selected item from the inventory into the slot
            Item selectedItem = inventoryPanel.getSelectedItem();
            if (selectedItem != null) {
                selectedIngredients[slotIndex] = selectedItem;
                player.removeItem(selectedItem.getName());  // Remove the item from the player's inventory
                updateIngredientSlot(slotIndex, selectedItem);  // Update the slot UI to show the item
                inventoryPanel.updateInventory(player.getInventory());  // Update the inventory display
                JOptionPane.showMessageDialog(this, selectedItem.getName() + " has been added to Slot " + (slotIndex + 1));
                inventoryPanel.setSelectedItem(null);  // Deselect the item in the inventory
            } else {
                JOptionPane.showMessageDialog(this, "No item selected.");
            }
        }
    }

    public void addSubmitListener(ActionListener listener) {
        this.submitListener = listener;
    }

    public Item getSelectedIngredient(int index) {
        return selectedIngredients[index];
    }
}
