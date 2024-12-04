package domain.item;

public class ShopItem extends Item implements Sellable {
    private double price;

    public ShopItem(String name, double price) {
        super(name);
        this.price = price;
    }

    @Override
    public double calculateSellPrice() {
        return price; // 상점에서의 가격
    }

    @Override
    public double getPrice() {
        return price;
    }
}
