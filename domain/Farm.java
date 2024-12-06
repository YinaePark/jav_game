package domain;

import domain.item.HarvestItem;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a farm where crops can be planted and harvested.
 * Manages the crops in plots and their lifecycle.
 */
public class Farm {
    private List<HarvestItem> crops = new ArrayList<>();
    private final int plotCount = 48;

    /**
     * Plants a crop in an available plot.
     * If no plots are available, the operation fails with a message.
     *
     * @param crop The crop to be planted
     */
    public void plantCrop(HarvestItem crop) {
        if (crops.size() < plotCount) {
            crop.plant();
            crops.add(crop);
            System.out.println(crop.getName() + " has been planted.");
        } else {
            System.out.println("No available plots.");
        }
    }

    /**
     * Harvests a crop by its name if it is ready to be harvested.
     * The crop is removed from the farm upon successful harvest.
     *
     * @param cropName The name of the crop to harvest
     * @return The harvested crop, or null if the crop is not ready or not found
     */
    public HarvestItem harvestCrop(String cropName) {
        for (HarvestItem crop : crops) {
            if (crop.getName().equalsIgnoreCase(cropName)) {
                if (crop.isReadyToHarvest()) {
                    crops.remove(crop);
                    return crop;
                } else {
                    return null;
                }
            }
        }
        System.out.println("No crop with the name " + cropName + " is planted.");
        return null;
    }


    /**
     * Retrieves all crops that are ready to be harvested.
     *
     * @return A list of crops that can be harvested
     */
    public List<HarvestItem> getReadyToHarvestCrops() {
        List<HarvestItem> readyCrops = new ArrayList<>();
        for (HarvestItem crop : crops) {
            if (crop.isReadyToHarvest()) {
                readyCrops.add(crop);
            }
        }
        return readyCrops;
    }

    public int getRemainingPlots() {
        return plotCount - crops.size();
    }

    public List<HarvestItem> getCrops() {
        return crops;
    }
}
