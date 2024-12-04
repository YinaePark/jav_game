package game.inventory;

import game.item.Item;
import java.util.*;

public class Inventory {
    private Map<Item, Integer> items = new HashMap<>();
    private static final int MAX_SLOTS = 20;
    
    public boolean addItem(Item item, int quantity) {
        if (items.size() >= MAX_SLOTS && !items.containsKey(item)) {
            return false;  // inventory is full
        }
        
        items.merge(item, quantity, Integer::sum);
        return true;
    }
    
    public boolean removeItem(Item item, int quantity) {
        if (!items.containsKey(item) || items.get(item) < quantity) {
            return false;
        }
        
        int newQuantity = items.get(item) - quantity;
        if (newQuantity <= 0) {
            items.remove(item);
        } else {
            items.put(item, newQuantity);
        }
        return true;
    }
    
    public int getItemQuantity(Item item) {
        return items.getOrDefault(item, 0);
    }
    
    public Map<Item, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }
}