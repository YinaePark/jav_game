package game.item;

public class Item {
    private final String name;
    private final String type;  // "SEED", "CROP", "TOOL"
    private final int price;
    
    public Item(String name, String type, int price) {
        this.name = name;
        this.type = type;
        this.price = price;
    }
    
    public String getName() { return name; }
    public String getType() { return type; }
    public int getPrice() { return price; }
}