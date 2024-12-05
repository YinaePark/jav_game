package game.ui;

import domain.item.Item;

import java.awt.*;

public class ShopSlot {
    private static final int SLOT_SIZE = 50;
    private static final int PADDING = 5;
    private Rectangle bounds;
    private Item item;

    public ShopSlot(int x, int y) {
        this.bounds = new Rectangle(x, y, SLOT_SIZE, SLOT_SIZE);
    }

    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    public void draw(Graphics g) {
        // 배경을 흰색으로 설정
        g.setColor(Color.WHITE);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // 슬롯 테두리 그리기 (테두리 색은 DARK_GRAY로 유지)
        g.setColor(Color.DARK_GRAY);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        if (item != null) {
            // 아이템 표시 (슬롯 내에 아이템을 그리는 부분)
            Image itemSprite = item.getSprite(SLOT_SIZE - 2*PADDING, SLOT_SIZE - 2*PADDING);
            g.drawImage(itemSprite,
                    bounds.x + PADDING,
                    bounds.y + PADDING,
                    null);
        }
    }

}
