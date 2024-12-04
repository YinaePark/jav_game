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
        HarvestItem crop = createCrop(cropName);

        if (crop == null) {
            System.out.println("Invalid crop.");
            return;
        }

        // 농작물을 심는 데 드는 비용 확인
        double plantingCost = crop.getPrice();
        if (!player.spendMoney(plantingCost)) {
            System.out.println("Not enough money to plant " + cropName + ".");
            return; // 돈 부족
        }

        // 농작물 심기
        farm.plantCrop(crop);
    }

    // crop 이름에 해당하는 HarvestItem 객체 생성
    private HarvestItem createCrop(String cropName) {
        switch (cropName.toLowerCase()) {
            case "potato":
                return new HarvestItem("Potato", 10, 500); // 가격 10, 성장 시간 5
            case "carrot":
                return new HarvestItem("Carrot", 8, 20);  // 가격 8, 성장 시간 4
            default:
                return null; // 알 수 없는 작물 이름 처리
        }
    }
}