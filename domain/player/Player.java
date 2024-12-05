package domain.player;

import domain.item.HarvestItem;
import domain.item.Item;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class Player {
    private double money;  // 돈
    private List<Item> inventory;  // 인벤토리
    private static final int MAX_INVENTORY_SIZE = 25;  // 인벤토리 최대 개수
    private final List<Consumer<List<Item>>> inventoryChangeListeners; // 인벤토리 변경 리스너

    public Player(double initialMoney) {
        this.money = initialMoney;
        this.inventory = new ArrayList<>();
        this.inventoryChangeListeners = new ArrayList<>();

        Item onion = new Item("onion") {
            @Override
            public double getPrice() {
                return 0;
            }
        };
        Item tomato  = new Item("tomato") {
            @Override
            public double getPrice() {
                return 0;
            }
        };
        addItem(tomato);
        addItem(onion);

    }

    public int getMaxInventorySize() {return MAX_INVENTORY_SIZE;}

    // 돈 차감
    public boolean spendMoney(double amount) {
        if (amount > money) {
            System.out.println("Not enough money.");
            return false;  // 돈이 부족하면 차감하지 않음
        }
        money -= amount;
        return true;  // 차감 성공
    }

    // 돈 추가
    public void earnMoney(double amount) {
        money += amount;
    }

    // 현재 돈 조회
    public double getMoney() {
        return money;
    }

    public List<Item> getInventory() {
        return new ArrayList<>(inventory);
    }

    // 아이템을 인벤토리에 추가 (인벤토리 최대 개수 체크)
    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            System.out.println("Inventory is full! Cannot add more items.");
            return false;  // 인벤토리가 가득 차면 아이템을 추가할 수 없음
        }
        inventory.add(item);
        notifyInventoryChange(); // 변경 알림
        return true;  // 아이템 추가 성공
    }

    // 인벤토리에서 이름이 같은 아이템 제거
    public boolean removeItem(String itemName) {
        for (Item inventoryItem : inventory) {
            if (inventoryItem.getName().equalsIgnoreCase(itemName)) {
                inventory.remove(inventoryItem); // 첫 번째로 발견된 아이템 제거
                System.out.println("Successfully removed.");
                notifyInventoryChange(); // 변경 알림
                printInventory();
                return true; // 성공적으로 제거한 경우 true 반환
            }
        }
        return false; // 해당 이름의 아이템이 없는 경우 false 반환
    }


    // 인벤토리에서 아이템 개수 조회
    public int getItemCount(Item item) {
        return (int) inventory.stream().filter(i -> i.equals(item)).count();
    }

    // 요리 판매: 5개의 재료를 사용하여 요리 판매
    public void sellDish(List<Item> dishIngredients) {
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
        earnMoney(100);
        System.out.println("Dish sold for 100 money!");
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
    // 인벤토리 상태 출력
    public void printInventory() {
        System.out.println("Inventory: ");
        for (Item item : inventory) {
            System.out.println("- " + item.getName());
        }
        System.out.println("MONEY : " + getMoney());
    }

    // 인벤토리 최대 개수 조회
    public int getInventorySize() {
        return inventory.size();
    }
    // 리스너 등록
    public void addInventoryChangeListener(Consumer<List<Item>> listener) {
        inventoryChangeListeners.add(listener);
    }

    // 리스너에 변경 알림
    private void notifyInventoryChange() {
        for (Consumer<List<Item>> listener : inventoryChangeListeners) {
            listener.accept(new ArrayList<>(inventory));
        }
    }

}
