package domain.item;

import domain.item.crops.*;

import java.util.*;

public class ShopItemManager {
    private static List<Item> allItems = new ArrayList<>();
    public ShopItemManager() {
        this.allItems = new ArrayList<>();
        initializeItems();
    }    // 상점에 판매할 아이템들을 초기화합니다.
    private static void initializeItems() {
        allItems.add(new Olive());
        allItems.add(new Lettuce());
        allItems.add(new Tomato());
        allItems.add(new Onion());
        allItems.add(new Truffle());
        allItems.add(new Wheat());

    }


    public static List<Item> getAllItems() {
        initializeItems();
        return allItems;
    }
}
