package game.ui;

import domain.item.Item;

import java.awt.*;

public class ShopSlot {
    private static final int SLOT_SIZE = 50;
    private static final int PADDING = 5;
    private Rectangle bounds;
    private Item item;
    private boolean selected;

    public ShopSlot(int x, int y) {
        this.bounds = new Rectangle(x, y, SLOT_SIZE, SLOT_SIZE);
        this.selected = false;
    }

    // 아이템을 설정하는 메소드
    public void setItem(Item item) {
        this.item = item;
    }

    public Item getItem() {
        return item;
    }

    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    // 선택 상태를 설정하는 메소드
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // 슬롯을 그리는 메소드
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
            // 아이템 이미지 표시
            Image itemSprite = item.getSprite(SLOT_SIZE - 2*PADDING, SLOT_SIZE - 2*PADDING);
            g.drawImage(itemSprite,
                    bounds.x + PADDING,
                    bounds.y + PADDING,
                    null);

            // 가격 표시 (유로 기호 포함)
            g.setColor(Color.BLACK);
            String priceStr = String.format("€%.2f", item.getPrice()); // 가격을 유로 형식으로 표시
            FontMetrics fm = g.getFontMetrics();
            int textX = bounds.x + bounds.width - fm.stringWidth(priceStr) - 5;
            int textY = bounds.y + bounds.height - 5;
            g.drawString(priceStr, textX, textY);

            // 아이템 이름 표시 (슬롯의 상단에)
            g.setColor(Color.BLACK);
            g.drawString(item.getName(), bounds.x + 5, bounds.y + 15);
        }
    }
}
