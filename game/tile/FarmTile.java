package game.tile;

import domain.item.HarvestItem;

public class FarmTile {
    private boolean tilled = false;  // tilled or not
    private HarvestItem crop;

    public FarmTile() {
        this.tilled = false;
        this.crop = null;
    }

    public boolean isTilled() {
        return tilled;
    }

    public void setTilled(boolean tilled) {
        this.tilled = tilled;
    }

    public boolean hasCrop() {
        return crop != null;
    }

    public String getCropName() {
        return crop != null ? crop.getName() : null;
    }

    public HarvestItem getCrop() {
        return crop;
    }

    public void setCrop(HarvestItem crop) {
        this.crop = crop;
    }

    public int getGrowthProgress() {
        return crop != null ? crop.getGrowthProgress() : 0;
    }
}