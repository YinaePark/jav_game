package domain.player;

import domain.item.Item;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private double money;  // 돈
    private List<Item> inventory;  // 인벤토리
    private static final int MAX_INVENTORY_SIZE = 25;  // 인벤토리 최대 개수

    public Player(double initialMoney) {
        this.money = initialMoney;
        this.inventory = new ArrayList<>();
    }

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

    // 아이템을 인벤토리에 추가 (인벤토리 최대 개수 체크)
    public boolean addItem(Item item) {
        if (inventory.size() >= MAX_INVENTORY_SIZE) {
            System.out.println("Inventory is full! Cannot add more items.");
            return false;  // 인벤토리가 가득 차면 아이템을 추가할 수 없음
        }
        inventory.add(item);
        return true;  // 아이템 추가 성공
    }

    // 인벤토리에서 아이템을 제거
    public boolean removeItem(Item item) {
        return inventory.remove(item);
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
            removeItem(item);
        }

        // 판매한 요리에 대한 보상 추가 (예시로 100 단위로 설정)
        earnMoney(100);
        System.out.println("Dish sold for 100 money!");
    }

    // 농작물 심기: 농작물을 심을 때 돈이 차감될 수 있음 (예: 심는 데 10 돈)
    public boolean plantCrop(Item cropItem, double plantingCost) {
        if (spendMoney(plantingCost)) {
            addItem(cropItem);  // 농작물이 인벤토리에 추가
            System.out.println("Planted " + cropItem.getName() + "!");
            return true;
        }
        return false;
    }

    // 인벤토리 상태 출력
    public void printInventory() {
        System.out.println("Inventory: ");
        for (Item item : inventory) {
            System.out.println("- " + item.getName());
        }
    }

    // 인벤토리 최대 개수 조회
    public int getInventorySize() {
        return inventory.size();
    }

}
