package domain.player;

import domain.item.HarvestItem;
import domain.item.Item;
import domain.item.crops.Onion;
import domain.item.crops.Tomato;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Player {
    private double money;
    private List<Item> inventory;
    private static final int MAX_INVENTORY_SIZE = 25;
    private final List<Consumer<List<Item>>> inventoryChangeListeners; // inventory change listener

    public Player(double initialMoney) {
        this.money = initialMoney;
        this.inventory = new ArrayList<>();
        this.inventoryChangeListeners = new ArrayList<>();

        // set default inventory
        Item onion = new Onion();
        Item tomato  = new Tomato();
        addItem(tomato);
        addItem(onion);

    }

    public int getMaxInventorySize() {return MAX_INVENTORY_SIZE;}

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

    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }

    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            System.out.println("Inventory is full! Cannot add more items.");
            return false;
        }
        inventory.add(item);
        notifyInventoryChange(); // notify change
        return true;  // return true when success adding item
    }

    public boolean removeItem(String itemName) {
        for (Item inventoryItem : inventory) {
            if (inventoryItem.getName().equalsIgnoreCase(itemName)) {
                inventory.remove(inventoryItem); // remove first item with itemName
                System.out.println("Successfully removed.");
                notifyInventoryChange(); // notify change
                return true; // return true when success removing item
            }
        }
        return false; // if there's no item with itemName, return false
    }

    public int getItemCount(Item item) {
        return (int) inventory.stream().filter(i -> i.equals(item)).count();
    }

    // if harvest success, add item to inventory
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

    public void printInventory() {
        System.out.println("Inventory: ");
        for (Item item : inventory) {
            System.out.println("- " + item.getName());
        }
        System.out.println("MONEY : " + getMoney());
    }

    // add listener
    public void addInventoryChangeListener(Consumer<List<Item>> listener) {
        inventoryChangeListeners.add(listener);
    }

    // notify change to listener
    private void notifyInventoryChange() {
        for (Consumer<List<Item>> listener : inventoryChangeListeners) {
            listener.accept(new ArrayList<>(inventory));
        }
    }

}
