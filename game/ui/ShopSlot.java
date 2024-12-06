package game.ui;

import domain.item.Item;

import java.awt.*;

public class ShopSlot {
    private static final int SLOT_SIZE = 50;
    private static final int PADDING = 5;
    private Rectangle bounds;
    private Item item;
    private boolean selected;

    // Constructor to initialize the position of the slot
    public ShopSlot(int x, int y) {
        this.bounds = new Rectangle(x, y, SLOT_SIZE, SLOT_SIZE);
        this.selected = false;
    }

    // Set the item for this slot
    public void setItem(Item item) {
        this.item = item;
    }

    // Get the item in this slot
    public Item getItem() { return item; }

    // Check if a given point is inside the bounds of this slot
    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    // Set the selection state of the slot
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Draw the slot and its contents (item, price, and name)
    public void draw(Graphics g) {
        // Draw the background of the slot
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

        // If the slot is selected, draw a yellow border around it
        if (selected) {
            g.setColor(Color.YELLOW);
            g.drawRect(bounds.x - 2, bounds.y - 2, bounds.width + 4, bounds.height + 4);
        }

        // Draw the regular dark border around the slot
        g.setColor(Color.DARK_GRAY);
        g.drawRect(bounds.x, bounds.y, bounds.width, bounds.height);

        if (item != null) {
            // Draw the item image inside the slot, with padding
            Image itemSprite = item.getSprite(SLOT_SIZE - 2*PADDING, SLOT_SIZE - 2*PADDING);
            g.drawImage(itemSprite,
                    bounds.x + PADDING,
                    bounds.y + PADDING,
                    null);

            // Display the price of the item in euros, formatted to two decimal places
            g.setColor(Color.BLACK);
            String priceStr = String.format("€%.2f", item.getPrice()); // Format price with euro sign
            FontMetrics fm = g.getFontMetrics();
            int textX = bounds.x + bounds.width - fm.stringWidth(priceStr) - 5;
            int textY = bounds.y + bounds.height - 5;
            g.drawString(priceStr, textX, textY); // Draw price in the bottom-right corner

            // Display the item name at the top of the slot
            g.setColor(Color.BLACK);
            g.drawString(item.getName(), bounds.x + 5, bounds.y + 15); // Draw the item name with some padding
        }
    }
}