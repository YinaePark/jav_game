package command;

import domain.Farm;
import domain.item.HarvestItem;
import domain.player.Player;

import java.util.List;

public class HarvestCommand implements Command {
    private Player player;
    private Farm farm;

    public HarvestCommand(Player player, Farm farm) {
        this.player = player;
        this.farm = farm;
    }

    @Override
    public void execute(String[] args) {
        // 수확 가능한 농작물 가져오기
        List<HarvestItem> readyCrops = farm.getReadyToHarvestCrops();

        if (readyCrops.isEmpty()) {
            System.out.println("No crops are ready to harvest.");
            return;
        }

        // 수확 가능한 농작물 수확
        for (HarvestItem crop : readyCrops) {
            // 플레이어가 농작물을 인벤토리에 추가
            player.harvestCrop(crop);

            // 농장에서 농작물 제거
            farm.harvestCrop(crop.getName());  // 농작물 이름으로 수확하여 리스트에서 제거

            // 수확 완료 메시지
            System.out.println(crop.getName() + " has been harvested and added to your inventory.");
        }
    }
}
