package game.ui;

import domain.item.Item;
import domain.item.ItemManager;
import domain.player.Player;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class ShopPanel extends JPanel {
    private static final int ROWS = 5;
    private static final int COLS = 5;
    private static final int PANEL_PADDING = 10;

    private ShopSlot[][] slots;
    private Player player;
    private GamePanel gamePanel;  // GamePanel 참조
    private boolean isVisible = false;  // ShopPanel 보이기/숨기기 여부

    public ShopPanel(Player player, GamePanel gamePanel) {
        this.player = player;
        this.gamePanel = gamePanel;
        this.slots = new ShopSlot[ROWS][COLS];

        setPreferredSize(new Dimension(300, 300));
        initializeSlots();
        addMouseListener(new ShopMouseListener());
        updateShop(ItemManager.getAllItems());

    }

    private void initializeSlots() {
        int slotSize = 50;
        int spacing = 5;

        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = PANEL_PADDING + j * (slotSize + spacing);
                int y = PANEL_PADDING + i * (slotSize + spacing);
                slots[i][j] = new ShopSlot(x, y);
            }
        }

    }

    public void toggleVisibility() {
        isVisible = !isVisible;
        repaint();
    }

    // ShopPanel 클래스 내부에서 paintComponent 수정
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // ShopPanel 배경을 흰색으로 설정
        g.setColor(Color.darkGray);
        g.fillRect(0, 0, getWidth(), getHeight());

        // 슬롯들을 그린다.
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                slots[i][j].draw(g);
            }
        }
    }


    private class ShopMouseListener extends MouseAdapter {
        @Override
        public void mouseClicked(MouseEvent e) {
            Point point = e.getPoint();

            // 슬롯 클릭 시
            for (int i = 0; i < ROWS; i++) {
                for (int j = 0; j < COLS; j++) {
                    if (slots[i][j].contains(point)) {
                        Item selectedItem = slots[i][j].getItem();

                        // 수량을 선택하는 다이얼로그 띄우기
                        String input = JOptionPane.showInputDialog(ShopPanel.this,
                                "Enter quantity for " + selectedItem.getName() + ":");
                        if (input != null) {
                            try {
                                int quantity = Integer.parseInt(input);
                                if (quantity > 0 && player.getMoney() >= selectedItem.getPrice() * quantity) {
                                    // 상품 구매
                                    player.spendMoney(selectedItem.getPrice() * quantity);
                                    player.addItem(selectedItem);
                                    JOptionPane.showMessageDialog(ShopPanel.this, "Purchased " + quantity + " " + selectedItem.getName());
                                } else {
                                    JOptionPane.showMessageDialog(ShopPanel.this, "Insufficient funds or invalid quantity", "Error", JOptionPane.ERROR_MESSAGE);
                                }
                            } catch (NumberFormatException ex) {
                                JOptionPane.showMessageDialog(ShopPanel.this, "Invalid number format.", "Error", JOptionPane.ERROR_MESSAGE);
                            }
                        }
                    }
                }
            }
        }
    }

    // ShopPanel에 보여질 상품을 설정하는 메소드
    public void updateShop(List<Item> availableItems) {
        int index = 0;
        for (Item item : availableItems) {
            if (index >= ROWS * COLS) break;
            int row = index / COLS;
            int col = index % COLS;
            slots[row][col].setItem(item);
            index++;
        }
    }


}
