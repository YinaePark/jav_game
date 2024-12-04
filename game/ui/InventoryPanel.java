package game.ui;

import javax.swing.*;
import java.awt.*;
import game.inventory.Inventory;
import game.item.Item;
import java.util.Map;

public class InventoryPanel extends JPanel {
    private final Inventory inventory;
    private static final int SLOT_SIZE = 40;
    private static final int ROWS = 4;
    private static final int COLS = 5;
    
    public InventoryPanel(Inventory inventory) {
        this.inventory = inventory;
        setPreferredSize(new Dimension(COLS * SLOT_SIZE, ROWS * SLOT_SIZE));
        setBorder(BorderFactory.createTitledBorder("Inventory"));
        
        addMouseListener(new java.awt.event.MouseAdapter() {
            @Override
            public void mouseClicked(java.awt.event.MouseEvent e) {
                int slotX = e.getX() / SLOT_SIZE;
                int slotY = e.getY() / SLOT_SIZE;
                // process slot click
                handleSlotClick(slotX, slotY);
            }
        });
    }
    
    private void handleSlotClick(int x, int y) {
        int index = y * COLS + x;
        Item[] items = inventory.getItems().keySet().toArray(new Item[0]);
        if (index < items.length) {
            Item item = items[index];
            // TODO: handle item click
            System.out.println("Selected: " + item.getName());
        }
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // draw inventory slots
        g.setColor(Color.GRAY);
        for (int i = 0; i < ROWS; i++) {
            for (int j = 0; j < COLS; j++) {
                int x = j * SLOT_SIZE;
                int y = i * SLOT_SIZE;
                g.drawRect(x, y, SLOT_SIZE, SLOT_SIZE);
            }
        }
        
        // draw 
        int slot = 0;
        for (Map.Entry<Item, Integer> entry : inventory.getItems().entrySet()) {
            int x = (slot % COLS) * SLOT_SIZE;
            int y = (slot / COLS) * SLOT_SIZE;
            
            // 아이템 배경
            g.setColor(new Color(200, 200, 200));
            g.fillRect(x + 1, y + 1, SLOT_SIZE - 2, SLOT_SIZE - 2);
            
            // 아이템 이름
            g.setColor(Color.BLACK);
            g.setFont(new Font("Arial", Font.PLAIN, 10));
            String itemText = entry.getKey().getName();
            g.drawString(itemText, x + 2, y + SLOT_SIZE - 4);
            
            // 수량
            String quantity = String.valueOf(entry.getValue());
            g.drawString(quantity, x + SLOT_SIZE - 15, y + 15);
            
            slot++;
        }
    }
}