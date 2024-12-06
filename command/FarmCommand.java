package command;

import domain.Farm;
import domain.item.HarvestItem;

public class FarmCommand implements Command {
    private Farm farm;

    public FarmCommand(Farm farm) {
        this.farm = farm;
    }


    @Override
    public void execute(String[] args) {
        // get planted crops list in the farm
        if (farm.getCrops().isEmpty()) {
            System.out.println("No crops have been planted yet.");
        } else {
            for (HarvestItem crop : farm.getCrops()) {
                String cropStatus = crop.isReadyToHarvest()
                        ? "Ready to Harvest"
                        : "Growing (" + crop.getGrowthProgress() + "%)";

                System.out.println("- " + crop.getName() + ": " + cropStatus);
            }
        }
    }
}
