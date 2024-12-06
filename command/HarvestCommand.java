package command;

import domain.Farm;
import domain.item.HarvestItem;
import domain.player.Player;
import game.tile.FarmTile;
import game.ui.GamePanel;
import game.entity.PlayerRenderer;

import javax.swing.JOptionPane; // JOptionPane 추가
import java.util.Random;

public class HarvestCommand implements Command {
    private Player player;
    private Farm farm;
    private FarmTile[][] tiles;
    private PlayerRenderer playerRenderer;
    private GamePanel gamePanel;
    private static final int TILE_SIZE = 40; // GamePanel과 동일한 값 사용
    private Random random = new Random(); // 확률 처리를 위한 Random 객체

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
        int prob = 1;
        StringBuilder harvestResult = new StringBuilder(); // 결과를 모을 StringBuilder

        // 모든 타일을 확인
        for (int i = 0; i < tiles.length; i++) {
            for (int j = 0; j < tiles[i].length; j++) {
                FarmTile tile = tiles[i][j];

                // 타일이 상호작용 가능한 범위 내에 있고, 작물이 있으며, 수확 가능한 상태인지 확인
                if (isInteractable(i, j) && tile.hasCrop() && tile.getCrop().isReadyToHarvest()) {
                    HarvestItem crop = tile.getCrop();

                    // 확률에 따라 수확 처리
                    double chance = random.nextDouble();

                    // 50% 확률로 실패 (chance < 0.5)
                    if (chance < 0.5) {
                        harvestResult.append(crop.getName()).append(" harvest failed.\n");
                        // 수확 실패 시 타일 초기화
                        tile.setCrop(null);
                        continue; // 수확 실패
                    }

                    // 확률에 따른 배수 처리
                    if (chance < 0.7) {
                        prob = 1;
                    } else if (chance < 0.8) {
                        prob = 2;
                    } else if (chance < 1.0) {
                        prob = 3;
                    }

                    // 플레이어 인벤토리에 추가 시도
                    if (player.harvestCrop(crop)) {
                        for (int k = 1; k < prob; k++) {
                            player.harvestCrop(crop);
                        }

                        harvestResult.append(prob).append(" ").append(crop.getName())
                                .append(" are harvested!!\n");

                        // 수확 성공 시 타일 초기화
                        tile.setCrop(null);
                        // 농장에서도 작물 제거
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

        // 결과를 모달 창으로 표시
        JOptionPane.showMessageDialog(null, harvestResult.toString());

        // 인벤토리가 표시되면 업데이트
        if (harvestedAnything && gamePanel != null) {
            gamePanel.updateInventoryIfVisible();
        }
    }
}
