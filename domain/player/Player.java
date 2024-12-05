package domain.player;

import domain.item.HarvestItem;
import domain.item.Item;
import domain.item.crops.Onion;
import domain.item.crops.Tomato;
import game.entity.NormalCustomer;

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
    // 요리 판매: 5개의 재료를 사용하여 요리 판매
    public void sellDish(List<Item> dishIngredients, NormalCustomer customer) {
        if (dishIngredients.size() != 5) {
            System.out.println("A dish requires exactly 5 ingredients.");
            return;
        }

        // 인벤토리에서 재료가 있는지 확인
        for (Item item : dishIngredients) {
            if (!inventory.contains(item)) {
                System.out.println("Missing ingredient: " + item.getName());
                return;  // 부족한 재료가 있으면 판매하지 않음
            }
        }

        // 재료 제거
        for (Item item : dishIngredients) {
            removeItem(item.getName());
        }

        // 판매한 요리에 대한 보상 추가 (예시로 100 단위로 설정)
        int reward = customer.calculateReward();
        earnMoney(reward);
        System.out.println("Dish sold for "+ reward + " euros!");
        notifyInventoryChange(); // 변경 알림

    }

    // 농작물 심기: 단지 농장에 심는 작업만 수행
    public boolean plantCrop(HarvestItem cropItem, double plantingCost) {
        if (spendMoney(plantingCost)) {
            System.out.println("Planted " + cropItem.getName() + "!");
            return true;
        }
        return false;
    }
    // 농작물 수확: 수확 가능한 경우 인벤토리에 추가
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
