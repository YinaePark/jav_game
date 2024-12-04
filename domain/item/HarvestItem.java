package domain.item;

public class HarvestItem extends Item implements Growable, Sellable {
    private double price;
    private int growthTime;
    private int timeElapsed; // 경과 시간
    private boolean isHarvested;

    public HarvestItem(String name, double price, int growthTime) {
        super(name);
        this.price = price;
        this.growthTime = growthTime;
        this.timeElapsed = 0;
        this.isHarvested = false;
    }

    // Growable 인터페이스 구현
    @Override
    public void plant() {
        System.out.println(name + " has been planted.");
        this.timeElapsed = 0;
        this.isHarvested = false;
    }

    @Override
    public boolean isReadyToHarvest() {
        return this.timeElapsed >= this.growthTime;
    }

    @Override
    public void harvest() {
        if (isReadyToHarvest()) {
            System.out.println(name + " is ready to harvest.");
            this.isHarvested = true;
        } else {
            System.out.println(name + " is not ready to harvest yet.");
        }
    }

    // Sellable 인터페이스 구현
    @Override
    public double calculateSellPrice() {
        return price;
    }

    @Override
    public double getPrice() {
        return price;
    }

    // Vegetable을 요리에 사용
    @Override
    public void useInDish() {
        if (isHarvested) {
            System.out.println(name + " is used in the dish.");
        } else {
            System.out.println(name + " cannot be used, it needs to be harvested first.");
        }
    }

    // 경과 시간 추가
    public void passTime(int hours) {
        this.timeElapsed += hours;
    }

}
