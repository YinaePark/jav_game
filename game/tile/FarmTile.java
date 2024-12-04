package game.tile;

public class FarmTile {
    private boolean tilled = false;  // tilled or not
    private String crop = null;      // crop type
    private int growthStage = 0;     // growth stage

    public boolean isTilled() {
        return tilled;
    }

    public void till() {
        this.tilled = true;
    }

    public boolean hasCrop() {
        return crop != null;
    }

    public void plant(String cropType) {
        if (tilled && !hasCrop()) {
            this.crop = cropType;
            this.growthStage = 0;
        }
    }

    public String getCrop() {
        return crop;
    }

    public int getGrowthStage() {
        return growthStage;
    }
}