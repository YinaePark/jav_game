package command;
import domain.item.Item;
import game.tile.FarmTile;
import domain.Farm;
import domain.item.HarvestItem;
import domain.player.Player;
import game.ui.GamePanel;

import javax.swing.JOptionPane;

public class PlantCommand implements Command {
    private Player player;
    private Farm farm;
    private final FarmTile tile;
    private final String cropType;
    private GamePanel gamePanel; // GamePanel 참조 추가


    public PlantCommand(Player player, Farm farm, FarmTile tile, String cropType, GamePanel gamePanel) {
        this.player = player;
        this.farm = farm;
        this.tile = tile;
        this.cropType = cropType;
        this.gamePanel = gamePanel; // GamePanel 초기화

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

        // 인벤토리에서 선택한 작물 검색
        Item selectedItem = player.getInventory().stream()
                .filter(item -> item.getName().equalsIgnoreCase(cropType))
                .findFirst()
                .orElse(null);

        if (selectedItem == null) {
            JOptionPane.showMessageDialog(null,
                    "You don't have enough " + cropType + " seeds to plant!",
                    "Insufficient Seeds",
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

        // 인벤토리에서 작물 제거
        if (!player.removeItem(cropType)) {
            JOptionPane.showMessageDialog(null,
                    "You don't have enough " + cropType + " seeds to plant!",
                    "Insufficient Seeds",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }


        // 작물 심기
        tile.setCrop(crop);
        farm.plantCrop(crop);

        // InventoryPanel 갱신
        if (gamePanel != null && gamePanel.getInventoryPanel() != null) {
            gamePanel.getInventoryPanel().updateInventory(player.getInventory());
        }

        JOptionPane.showMessageDialog(null,
                "Successfully planted " + cropType + "!",
                "Planting Success",
                JOptionPane.INFORMATION_MESSAGE);
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