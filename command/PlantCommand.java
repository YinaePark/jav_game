package command;
import game.tile.FarmTile;
import domain.Farm;
import domain.item.HarvestItem;
import domain.player.Player;
import javax.swing.JOptionPane;

public class PlantCommand implements Command {
    private Player player;
    private Farm farm;
    private final FarmTile tile;
    private final String cropType;

    public PlantCommand(Player player, Farm farm, FarmTile tile, String cropType) {
        this.player = player;
        this.farm = farm;
        this.tile = tile;
        this.cropType = cropType;
    }

    @Override
    public void execute(String[] args) {
        if (!tile.isTilled() || tile.hasCrop()) {
            JOptionPane.showMessageDialog(null, 
                "Cannot plant here. Make sure the tile is tilled and empty.",
                "Planting Error",
                JOptionPane.WARNING_MESSAGE);
            return;
        }

        // HarvestItem 생성
        HarvestItem crop = createCrop(cropType);
        if (crop == null) {
            JOptionPane.showMessageDialog(null,
                "Invalid crop type: " + cropType,
                "Invalid Crop",
                JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 비용 확인 및 처리 -- 상점에서 씨앗이나 작물을 구매할 때 돈을 차감하는거 아닌가 해서?!?! 주석처리 ㅎ
        // double plantingCost = crop.getPrice();
        // if (!player.spendMoney(plantingCost)) {
        //     JOptionPane.showMessageDialog(null,
        //         "Not enough money to plant " + cropType + "\nRequired: $" + plantingCost,
        //         "Insufficient Funds",
        //         JOptionPane.ERROR_MESSAGE);
        //     return;
        // }

        // 작물 심기
        tile.setCrop(crop);
        farm.plantCrop(crop);
    }

    private HarvestItem createCrop(String cropName) {
        switch (cropName.toLowerCase()) {
            case "onion":
                return new HarvestItem("Onion", 2, 5); // 가격 2, 성장 시간 5
            case "olive":
                return new HarvestItem("Olive", 3, 15);  
            case "tomato":
                return new HarvestItem("Tomato", 2, 10);
            case "lettuce":
                return new HarvestItem("Lettuce", 2, 3);
            case "wheat":
                return new HarvestItem("Wheat", 4, 4);
            case "truffle":
                return new HarvestItem("Truffle", 20, 600);        
            default:
                return null; // 알 수 없는 작물 이름 처리
        }
    }
}