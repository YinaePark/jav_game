package domain;

import domain.item.HarvestItem;
import domain.item.Item;
import domain.item.crops.Onion;
import domain.item.crops.Tomato;
import game.entity.NormalCustomer;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

/**
 * Represents a player with money, inventory, and various actions like planting, harvesting, and selling items.
 */
public class Player {
    private double money; // Current money the player has
    private List<Item> inventory; // Player's inventory
    private static final int MAX_INVENTORY_SIZE = 25;
    private final List<Consumer<List<Item>>> inventoryChangeListeners; // Listeners for inventory changes

    public Player(double initialMoney) {
        this.money = initialMoney;
        this.inventory = new ArrayList<>();
        this.inventoryChangeListeners = new ArrayList<>();

        // Set default inventory with initial items
        addItem(new Onion());
        addItem(new Tomato());
    }

    // Money management methods
    public boolean spendMoney(double amount) {
        if (amount > money) {
            System.out.println("Not enough money.");
            return false;
        }
        money -= amount;
        return true;
    }

    public void earnMoney(double amount) {
        money += amount;
    }

    public double getMoney() {
        return money;
    }

    // Inventory management methods
    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }

    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            System.out.println("Inventory is full! Cannot add more items.");
            return false;
        }
        inventory.add(item);
        notifyInventoryChange();
        return true;
    }

    public boolean removeItem(String itemName) {
        for (Item inventoryItem : inventory) {
            if (inventoryItem.getName().equalsIgnoreCase(itemName)) {
                inventory.remove(inventoryItem);
                System.out.println("Successfully removed: " + itemName);
                notifyInventoryChange();
                return true;
            }
        }
        return false;
    }

    public int getItemCount(Item item) {
        return (int) inventory.stream().filter(i -> i.equals(item)).count();
    }

    /**
     * Sells a dish by removing ingredients from inventory and rewarding the player.
     * @param dishIngredients Ingredients needed for the dish
     * @param customer        Customer buying the dish
     */
    public void sellDish(List<Item> dishIngredients, NormalCustomer customer) {
        if (dishIngredients.size() != 5) {
            System.out.println("A dish requires exactly 5 ingredients.");
            return;
        }

        // Validate and process ingredients
        if (!hasAllIngredients(dishIngredients)) {
            System.out.println("Missing ingredients. Cannot sell the dish.");
            return;
        }
        removeIngredients(dishIngredients);

        // Reward player
        int reward = customer.calculateReward();
        earnMoney(reward);
        System.out.println("Dish sold for " + reward + " euros!");
    }

    /**
     * Checks if the inventory contains all required ingredients.
     * @param ingredients List of required ingredients
     * @return true if all ingredients are available, false otherwise
     */
    private boolean hasAllIngredients(List<Item> ingredients) {
        for (Item item : ingredients) {
            if (!inventory.contains(item)) {
                System.out.println("Missing ingredient: " + item.getName());
                return false;
            }
        }
        return true;
    }

    /**
     * Removes a list of ingredients from the inventory.
     * @param ingredients List of ingredients to remove
     */
    private void removeIngredients(List<Item> ingredients) {
        for (Item item : ingredients) {
            removeItem(item.getName());
        }
    }

    // Crop-related methods
    public boolean plantCrop(HarvestItem cropItem, double plantingCost) {
        if (spendMoney(plantingCost)) {
            System.out.println("Planted " + cropItem.getName() + "!");
            return true;
        }
        return false;
    }

    public boolean harvestCrop(HarvestItem cropItem) {
        if (!cropItem.isReadyToHarvest()) {
            System.out.println(cropItem.getName() + " is not ready to harvest yet.");
            return false;
        }

        if (addItem(cropItem)) {
            System.out.println("Harvested " + cropItem.getName() + " and added it to your inventory!");
            return true;
        } else {
            System.out.println("Failed to harvest. Inventory is full.");
            return false;
        }
    }

    // Debugging and inventory display
    public void printInventory() {
        System.out.println("Inventory:");
        for (Item item : inventory) {
            System.out.println("- " + item.getName());
        }
        System.out.println("Money: " + getMoney());
    }

    // Inventory listeners
    public void addInventoryChangeListener(Consumer<List<Item>> listener) {
        inventoryChangeListeners.add(listener);
    }

    private void notifyInventoryChange() {
        for (Consumer<List<Item>> listener : inventoryChangeListeners) {
            listener.accept(new ArrayList<>(inventory));
        }
    }
}
