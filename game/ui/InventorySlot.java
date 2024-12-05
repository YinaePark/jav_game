package game.ui;

import domain.item.Item;
import java.awt.*;

public class InventorySlot {
    private static final int SLOT_SIZE = 50;
    private static final int PADDING = 5;
    private Rectangle bounds;
    private Item item;
    private int count;
    private boolean selected;

    public InventorySlot(int x, int y) {
        this.bounds = new Rectangle(x, y, SLOT_SIZE, SLOT_SIZE);
        this.selected = false;
    }

    public void setItem(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    public Item getItem() {
        return item;
    }

    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public void draw(Graphics g) {
        // 슬롯 배경
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);
        
        // 선택된 경우 테두리 강조
        if (selected) {
            g.setColor(Color.YELLOW);
            g.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
        }
        
        // 기본 테두리
        g.setColor(Color.DARK_GRAY);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        if (item != null) {
            // 아이템 표시
            Image itemSprite = item.getSprite(SLOT_SIZE - 2*PADDING, SLOT_SIZE - 2*PADDING);
            g.drawImage(itemSprite, 
                    bounds.x + PADDING, 
                    bounds.y + PADDING, 
                    null);
            
            // 수량 표시
            g.setColor(Color.BLACK);
            String countStr = String.valueOf(count);
            FontMetrics fm = g.getFontMetrics();
            int textX = bounds.x + bounds.width - fm.stringWidth(countStr) - 5;
            int textY = bounds.y + bounds.height - 5;
            g.drawString(countStr, textX, textY);
        }
    }
}
