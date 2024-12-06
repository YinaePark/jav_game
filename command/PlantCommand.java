package command;
import domain.item.Item;
import domain.item.crops.*;
import game.tile.FarmTile;
import domain.Farm;
import domain.item.HarvestItem;
import domain.Player;
import game.ui.GamePanel;

import javax.swing.JOptionPane;

public class PlantCommand implements Command {
    private Player player;
    private Farm farm;
    private final FarmTile tile;
    private final String cropType;
    private GamePanel gamePanel;


    public PlantCommand(Player player, Farm farm, FarmTile tile, String cropType, GamePanel gamePanel) {
        this.player = player;
        this.farm = farm;
        this.tile = tile;
        this.cropType = cropType;
        this.gamePanel = gamePanel; // GamePanel 초기화

    }
    @Override
    public void execute(String[] args) {
        // Step 1: Validate the tile
        String validationError = validateTile();
        if (validationError != null) {
            JOptionPane.showMessageDialog(null,
                    validationError,
                    "Planting Error",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 2: Find the selected crop in the player's inventory
        Item selectedItem = getSelectedCropFromInventory();
        if (selectedItem == null) {
            JOptionPane.showMessageDialog(null,
                    "You don't have enough " + cropType + " seeds to plant!",
                    "Insufficient Seeds",
                    JOptionPane.WARNING_MESSAGE);
            return;
        }

        // Step 3: Create and plant the crop
        String plantingResult = plantCropOnTile();
        if (plantingResult != null) {
            JOptionPane.showMessageDialog(null,
                    plantingResult,
                    "Planting Success",
                    JOptionPane.INFORMATION_MESSAGE);
        }
    }

    /**
     * Validates whether the tile is suitable for planting.
     *
     * @return A validation error message if the tile is invalid; null otherwise.
     */
    private String validateTile() {
        if (!tile.isTilled()) {
            return "Cannot plant here. The tile is not tilled.";
        }
        if (tile.hasCrop()) {
            return "Cannot plant here. The tile already has a crop.";
        }
        return null;
    }

    /**
     * Searches for the selected crop type in the player's inventory.
     *
     * @return The Item representing the crop seed, or null if not found.
     */
    private Item getSelectedCropFromInventory() {
        return player.getInventory().stream()
                .filter(item -> item.getName().equalsIgnoreCase(cropType))
                .findFirst()
                .orElse(null);
    }

    /**
     * Plants the crop on the tile and updates relevant data structures.
     *
     * @return A message indicating the result of the planting.
     */
    private String plantCropOnTile() {
        // Create the crop
        HarvestItem crop = createCrop(cropType);
        if (crop == null) {
            return "Invalid crop type: " + cropType;
        }

        // Remove crop seed from inventory
        if (!player.removeItem(cropType)) {
            return "You don't have enough " + cropType + " seeds to plant!";
        }

        // Plant the crop on the tile
        tile.setCrop(crop);
        farm.plantCrop(crop);

        // Update inventory panel
        if (gamePanel != null && gamePanel.getInventoryPanel() != null) {
            gamePanel.getInventoryPanel().updateInventory(player.getInventory());
        }

        return "Successfully planted " + cropType + "!";
    }


    private HarvestItem createCrop(String cropName) {
        switch (cropName.toLowerCase()) {
            case "onion":
                return new Onion();
            case "olive":
                return new Olive();
            case "tomato":
                return new Tomato();
            case "lettuce":
                return new Lettuce();
            case "wheat":
                return new Wheat();
            case "truffle":
                return new Truffle();
            default:
                return null;
        }
    }
}