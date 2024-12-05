package command;

import domain.Farm;
import domain.item.HarvestItem;
import domain.player.Player;
import game.tile.FarmTile;
import game.ui.GamePanel;
import game.entity.PlayerRenderer;

public class HarvestCommand implements Command {
    private Player player;
    private Farm farm;
    private FarmTile[][] tiles;
    private PlayerRenderer playerRenderer;
    private GamePanel gamePanel;
    private static final int TILE_SIZE = 40; // GamePanel과 동일한 값 사용

    public HarvestCommand(Player player, Farm farm, FarmTile[][] tiles, PlayerRenderer playerRenderer, GamePanel gamePanel) {
        this.player = player;
        this.farm = farm;
        this.tiles = tiles;
        this.playerRenderer = playerRenderer;
        this.gamePanel = gamePanel;
    }

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

        // 모든 타일을 확인
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                FarmTile tile = tiles[i][j];
                
                // 타일이 상호작용 가능한 범위 내에 있고, 작물이 있으며, 수확 가능한 상태인지 확인
                if (isInteractable(i, j) && tile.hasCrop() && tile.getCrop().isReadyToHarvest()) {
                    HarvestItem crop = tile.getCrop();
                    
                    // 플레이어 인벤토리에 추가 시도
                    if (player.harvestCrop(crop)) {
                        // 수확 성공 시 타일 초기화
                        tile.setCrop(null);
                        // 농장에서도 작물 제거
                        farm.harvestCrop(crop.getName());
                        
                        System.out.println(crop.getName() + " has been harvested and added to your inventory.");
                        harvestedAnything = true;
                    } else {
                        System.out.println("Inventory is full! Cannot harvest " + crop.getName());
                    }
                }
            }
        }

        if (!harvestedAnything) {
            System.out.println("No crops are ready to harvest in range.");
        }

        if (harvestedAnything && gamePanel != null) {
            gamePanel.updateInventoryIfVisible();
        }
    }
}