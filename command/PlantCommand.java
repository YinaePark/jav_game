package command;
import game.tile.FarmTile;
import domain.Farm;
import domain.item.HarvestItem;
import domain.player.Player;
public class PlantCommand implements Command {
    private Player player;
    private Farm farm;
    private final FarmTile tile;
    private final String cropType;

    // 생성자에서 player와 farm을 전달받고 이를 필드에 올바르게 할당
    public PlantCommand(Player player, Farm farm, FarmTile tile, String cropType) {
        this.player = player;  // 전달받은 player를 필드에 할당
        this.farm = farm;      // 전달받은 farm을 필드에 할당
        this.tile = tile;
        this.cropType = cropType;
    }



    @Override
    public void execute(String[] args) {
        if (tile.isTilled() && !tile.hasCrop()) {
            tile.plant(cropType);
            System.out.println("Successfully planted " + cropType);
        } else {
            System.out.println("Cannot plant here. Make sure the tile is tilled and empty.");
        }

        // crop 이름을 받아서 실제 HarvestItem 객체 생성
        String cropName = args[0];
        HarvestItem crop = (HarvestItem) createCrop(cropName);

        if (crop == null) {
            System.out.println("Invalid crop.");
            return;
        }

        // 농작물을 심는 데 드는 비용을 Item에서 가져옴
        double plantingCost = crop.getPrice();

        // 플레이어가 농작물을 심을 돈이 있는지 확인
        if (!player.spendMoney(plantingCost)) {
            return;  // 돈이 부족하면 심을 수 없음
        }

        // 농작물을 농장에 심기
        farm.plantCrop(crop);

        // 농작물을 인벤토리에 추가
        player.addItem(crop);
    }

    // crop 이름에 해당하는 HarvestItem 객체를 생성하는 메서드
    private HarvestItem createCrop(String cropName) {
        // cropName에 맞는 HarvestItem 객체를 반환 (예시)
        switch (cropName.toLowerCase()) {
            case "potato":
                return new HarvestItem("Potato", 10, 5);  // 예시로 감자, 가격 10, 성장 시간 5
            case "carrot":
                return new HarvestItem("Carrot", 8, 4);   // 예시로 당근, 가격 8, 성장 시간 4
            // 다른 농작물 추가 가능
            default:
                return null;  // 해당 농작물이 없으면 null 반환
        }
    }
}