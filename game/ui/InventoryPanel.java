package game.ui;

import domain.item.Item;
import domain.player.Player;
import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.util.*;
import java.util.List;

public class InventoryPanel extends JPanel {
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int PANEL_PADDING = 10;
    
    private InventorySlot[][] slots;
    private Player player;
    private Item selectedItem;
    private GamePanel gamePanel;  // 메인 게임 패널 참조

    public InventoryPanel(Player player, GamePanel gamePanel) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.slots = new InventorySlot[ROWS][COLS];
        
        setPreferredSize(new Dimension(300, 300));
        initializeSlots();
        addMouseListener(new InventoryMouseListener());
    }

    private void initializeSlots() {
        int slotSize = 50;
        int spacing = 5;
        
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = PANEL_PADDING + j * (slotSize + spacing);
                int y = PANEL_PADDING + i * (slotSize + spacing);
                slots[i][j] = new InventorySlot(x, y);
            }
        }
    }

    public void updateInventory() {
        // 모든 슬롯 초기화
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].setItem(null, 0);
            }
        }

        // 인벤토리 아이템을 종류별로 그룹화
        Map<String, List<Item>> itemGroups = new HashMap<>();
        for (Item item : getPlayerInventory()) {
            itemGroups.computeIfAbsent(item.getName(), k -> new ArrayList<>()).add(item);
        }

        // 그룹화된 아이템을 슬롯에 배치
        int slotIndex = 0;
        for (Map.Entry<String, List<Item>> entry : itemGroups.entrySet()) {
            if (slotIndex >= ROWS * COLS) break;
            
            int row = slotIndex / COLS;
            int col = slotIndex % COLS;
            slots[row][col].setItem(entry.getValue().get(0), entry.getValue().size());
            slotIndex++;
        }
        
        repaint();
    }

    private List<Item> getPlayerInventory() {
        // Player 클래스에서 inventory를 가져오는 메서드 필요
        return player.getInventory();  // 이 메서드는 Player 클래스에 추가해야 함
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.setColor(new Color(200, 200, 200));
        g.fillRect(0, 0, getWidth(), getHeight());
        
        // 모든 슬롯 그리기
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].draw(g);
            }
        }
    }

    private class InventoryMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();
            
            // 이전 선택 해제
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (slots[i][j].contains(point)) {
                        // 다른 모든 슬롯의 선택 해제
                        clearSelection();
                        
                        // 새로운 슬롯 선택
                        slots[i][j].setSelected(true);
                        selectedItem = slots[i][j].getItem();
                        
                        // GamePanel에 선택된 아이템 정보 전달
                        gamePanel.setSelectedItem(selectedItem);
                        
                        repaint();
                        return;
                    }
                }
            }
        }
    }

    private void clearSelection() {
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].setSelected(false);
            }
        }
    }

    public Item getSelectedItem() {
        return selectedItem;
    }
}