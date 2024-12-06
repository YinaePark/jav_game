package command;

import domain.Farm;
import domain.item.HarvestItem;
import domain.player.Player;
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
        int crobYield = 1;
        StringBuilder harvestResult = new StringBuilder();

        // Check all tiles
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                FarmTile tile = tiles[i][j];
                if (isInteractable(i, j) && tile.hasCrop() && tile.getCrop().isReadyToHarvest()) {
                    HarvestItem crop = tile.getCrop();

                    // harvest according to probability
                    double chance = random.nextDouble();

                    // 50% chance to fail (chance < 0.5)
                    if (chance < 0.5) {
                        harvestResult.append(crop.getName()).append(" harvest failed.\n");
                        tile.setCrop(null);
                        continue;
                    }
                    // crop yield based on probability
                    if (chance < 0.7) {
                        crobYield = 1;
                    } else if (chance < 0.8) {
                        crobYield = 2;
                    } else if (chance < 1.0) {
                        crobYield = 3;
                    }

                    // add harvested crop to player inventory
                    if (player.harvestCrop(crop)) {
                        for (int k = 1; k < crobYield; k++) {
                            player.harvestCrop(crop);
                        }
                        harvestResult.append(crobYield).append(" ").append(crop.getName())
                                .append(" are harvested!!\n");

                        // tile and farm initialization
                        tile.setCrop(null);
                        farm.harvestCrop(crop.getName());

                        harvestResult.append(crop.getName()).append(" has been harvested and added to your inventory.\n");
                        harvestedAnything = true;
                    } else {
                        harvestResult.append("Inventory is full! Cannot harvest ").append(crop.getName()).append("\n");
                    }
                }
            }
        }

        if (!harvestedAnything) {
            harvestResult.append("No crops are ready to harvest in range.\n");
        }
        JOptionPane.showMessageDialog(null, harvestResult.toString());
        // update inventory
        if (harvestedAnything && gamePanel != null) {
            gamePanel.updateInventoryIfVisible();
        }
    }
}
