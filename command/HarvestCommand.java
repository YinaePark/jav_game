package command;

import domain.Farm;
import domain.item.HarvestItem;
import domain.Player;
import game.tile.FarmTile;
import game.ui.GamePanel;
import game.entity.PlayerRenderer;
import javax.swing.JOptionPane; // JOptionPane 추가
import java.util.Random;

/**
 * Command to handle harvesting crops from the farm.
 */
public class HarvestCommand implements Command {
    private Player player;
    private Farm farm;
    private FarmTile[][] tiles;
    private PlayerRenderer playerRenderer;
    private GamePanel gamePanel;
    private static final int TILE_SIZE = 40; // Tile size is same as GamePanel TILE_SIZE
    private Random random = new Random();

    public HarvestCommand(Player player, Farm farm, FarmTile[][] tiles, PlayerRenderer playerRenderer, GamePanel gamePanel) {
        this.player = player;
        this.farm = farm;
        this.tiles = tiles;
        this.playerRenderer = playerRenderer;
        this.gamePanel = gamePanel;
    }

    /**
     * Checks if a tile is within interaction range of the player.
     */
    private boolean isInteractable(int tileX, int tileY) {
        int playerCenterX = playerRenderer.getX() + (playerRenderer.getSize() / 2);
        int playerCenterY = playerRenderer.getY() + (playerRenderer.getSize() / 2);

        int playerTileX = playerCenterX / TILE_SIZE;
        int playerTileY = playerCenterY / TILE_SIZE;

        return Math.abs(tileX - playerTileX) <= 1 &&
                Math.abs(tileY - playerTileY) <= 1;
    }

@Override
public void execute(String[] args) {
    boolean harvestedAnything = false;
    StringBuilder harvestResult = new StringBuilder();

    // Check all tiles
    for (int i = 0; i < tiles.length; i++) {
        for (int j = 0; j < tiles[i].length; j++) {
            if (isInteractable(i, j)) {
                String result = processTile(tiles[i][j]);
                if (!result.isEmpty()) {
                    harvestResult.append(result);
                    harvestedAnything = true;
                }
            }
        }
    }

    if (!harvestedAnything) {
        harvestResult.append("No crops are ready to harvest in range.\n");
    }

    JOptionPane.showMessageDialog(null, harvestResult.toString());

    if (harvestedAnything && gamePanel != null) {
        gamePanel.updateInventoryIfVisible();
    }
}

    /**
     * Processes a single tile for harvesting.
     *
     * @param tile The tile to process.
     * @return A string message describing the harvest result, or an empty string if no action occurred.
     */
    private String processTile(FarmTile tile) {
        // no crop to harvest
        if (!tile.hasCrop() || !tile.getCrop().isReadyToHarvest()) {
            return "";
        }
        HarvestItem crop = tile.getCrop();
        double chance = random.nextDouble();

        // Check harvest success
        if (chance < 0.5) {
            tile.setCrop(null);
            farm.harvestCrop(crop.getName());
            return crop.getName() + " harvest failed.\n";
        }
        // Calculate crop yield
        int cropYield = calculateYield(chance);

        // Add crop to player's inventory
        if (player.harvestCrop(crop)) {
            for (int i = 1; i < cropYield; i++) {
                player.harvestCrop(crop); // additional harvest
            }
            // Clear tile and update farm
            tile.setCrop(null);
            farm.harvestCrop(crop.getName());

            return cropYield + " " + crop.getName() + " are harvested and added to inventory.\n";
        } else {
            return "Inventory is full! Cannot harvest " + crop.getName() + "\n";
        }
    }

    /**
     * Calculates the crop yield based on a random chance.
     *
     * @param chance The random chance value.
     * @return The number of crops yielded.
     */
    private int calculateYield(double chance) {
        if (chance < 0.7) {
            return 1;
        } else if (chance < 0.8) {
            return 2;
        } else {
            return 3;
        }
    }

}
