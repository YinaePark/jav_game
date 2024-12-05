package domain.item;

public class HarvestItem extends Item implements Growable, Purchasable {
    private double price; // 판매 가격
    private int growthTime; // 총 성장 시간 (시간 단위)
    private long plantedTimestamp; // 심은 시점의 타임스탬프 (밀리초 단위)
    private boolean isHarvested; // 수확 여부

    public HarvestItem(String name, double price, int growthTime) {
        super(name);
        this.price = price;
        this.growthTime = growthTime;
        this.plantedTimestamp = -1; // 초기값: 아직 심어지지 않음
        this.isHarvested = false;
    }

    // Growable 인터페이스 구현
    @Override
    public void plant() {
        System.out.println(getName() + " has been planted.");
        this.plantedTimestamp = System.currentTimeMillis(); // 심는 시점 기록
        this.isHarvested = false; // 심은 상태로 설정
    }

    @Override
    public boolean isReadyToHarvest() {
        return getTimeElapsed() >= this.growthTime; // 경과 시간이 성장 시간 이상인지 확인
    }

    @Override
    public void harvest() {
        if (isReadyToHarvest()) {
            System.out.println(getName() + " has been harvested.");
            this.isHarvested = true; // 수확 완료 상태로 설정
        } else {
            System.out.println(getName() + " is not ready to harvest yet.");
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

    @Override
    public void useInDish() {
        if (isHarvested) {
            System.out.println(getName() + " is used in the dish.");
        } else {
            System.out.println(getName() + " cannot be used, it needs to be harvested first.");
        }
    }

    // 심은 이후 경과 시간을 반환
    private int getTimeElapsed() {
        if (plantedTimestamp == -1 || isHarvested) {
            return 0; // 아직 심지 않았거나 수확 후에는 성장 멈춤
        }
        long currentTime = System.currentTimeMillis();
        long elapsedMillis = currentTime - plantedTimestamp;
        return (int) (elapsedMillis / 1000); // 시간 단위로 변환
    }

    // 성장 진행 상태 반환
    public int getGrowthProgress() {
        int elapsed = getTimeElapsed();
        // System.out.println("growthTime : "+this.growthTime);
        return (int) ((double) elapsed / this.growthTime * 100);
    }

    // 디버깅 또는 상태 확인용
    public void printStatus() {
        System.out.println("Name: " + getName());
        System.out.println("Growth Progress: " + getGrowthProgress() + "%");
        System.out.println("Ready to Harvest: " + isReadyToHarvest());
    }
}
