package domain;

import domain.item.HarvestItem;
import java.util.ArrayList;
import java.util.List;

public class Farm {
    private List<HarvestItem> crops = new ArrayList<>();
    private final int plotCount = 5;

    public void plantCrop(HarvestItem crop) {
        if (crops.size() < plotCount) {
            crops.add(crop);
            System.out.println(crop.getName() + " has been planted.");
        } else {
            System.out.println("No available plots.");
        }
    }

    public void harvestCrop(HarvestItem crop) {
        if (crops.contains(crop)) {
            crops.remove(crop);
            System.out.println(crop.getName() + " has been harvested.");
        } else {
            System.out.println("This crop is not planted.");
        }
    }

    public List<HarvestItem> getCrops() {
        return crops;
    }
}
