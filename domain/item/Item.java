package domain.item;


public abstract class Item implements UsableInDish {
    protected String name;

    public Item(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    @Override
    public void useInDish() {
        System.out.println(name + " is used in the dish.");
    }

    public abstract double getPrice(); // 가격을 반환하는 메서드 (상점용)
}
