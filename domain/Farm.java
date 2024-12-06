package domain;

import domain.item.HarvestItem;
import java.util.ArrayList;
import java.util.List;

public class Farm {
    private List<HarvestItem> crops = new ArrayList<>();
    private final int plotCount = 500000;

    // 농작물 심기
    public void plantCrop(HarvestItem crop) {
        if (crops.size() < plotCount) {
            crop.plant(); // 심으면 심은 시점부터 성장 시작
            crops.add(crop);
            System.out.println(crop.getName() + " has been planted.");
        } else {
            System.out.println("No available plots.");
        }
    }

    // 농작물 수확
    public HarvestItem harvestCrop(String cropName) {
        for (HarvestItem crop : crops) {
            if (crop.getName().equalsIgnoreCase(cropName)) {
                if (crop.isReadyToHarvest()) {
                    crops.remove(crop);
                    System.out.println(crop.getName() + " has been harvested.");
                    return crop; // 수확된 농작물 반환
                } else {
                    System.out.println(crop.getName() + " is not ready to harvest yet.");
                    return null;
                }
            }
        }
        System.out.println("No crop with the name " + cropName + " is planted.");
        return null; // 해당 이름의 농작물이 없거나 수확 준비가 안 된 경우
    }

    // 농장 상태 출력
    public void printFarmStatus() {
        System.out.println("Farm Status:");
        for (int i = 0; i < plotCount; i++) {
            if (i < crops.size()) {
                HarvestItem crop = crops.get(i);
                System.out.println("Plot " + (i + 1) + ": " + crop.getName() +
                        " (Growth: " + crop.getGrowthProgress() + "%, Ready to Harvest: " + crop.isReadyToHarvest() + ")");
            } else {
                System.out.println("Plot " + (i + 1) + ": Empty");
            }
        }
    }
    // 농작물 중 수확 가능한 농작물만 반환
    public List<HarvestItem> getReadyToHarvestCrops() {
        List<HarvestItem> readyCrops = new ArrayList<>();
        for (HarvestItem crop : crops) {
            if (crop.isReadyToHarvest()) {
                readyCrops.add(crop);
            }
        }
        return readyCrops;
    }
    // 남은 플롯 개수 반환
    public int getRemainingPlots() {
        return plotCount - crops.size();
    }

    // 현재 농작물 목록 반환
    public List<HarvestItem> getCrops() {
        return crops;
    }
}
