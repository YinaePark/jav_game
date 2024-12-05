package domain.item;

public class ShopItem extends Item implements Purchasable {
    private double price;

    public ShopItem(String name, double price) {
        super(name);
        this.price = price;
    }
    @Override
    public double getPrice() {
        return price;
    }
}
