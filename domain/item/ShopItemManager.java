package domain.item;

import java.util.*;

public class ShopItemManager {
    private static List<Item> allItems = new ArrayList<>();

    static {
        // 게임에서 사용되는 아이템을 초기화 (아이템 추가)
        allItems.add(new Item("tomato") {
            @Override
            public double getPrice() {
                return 10.0;
            }
        });
        allItems.add(new Item("wheat") {
            @Override
            public double getPrice() {
                return 15.0;
            }
        });
        allItems.add(new Item("olive") {
            @Override
            public double getPrice() {
                return 20.0;
            }
        });
        // 여기에 모든 아이템 추가...
    }

    public static List<Item> getAllItems() {
        return allItems;
    }
}
