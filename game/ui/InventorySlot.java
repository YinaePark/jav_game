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

    // Constructor to initialize the position of the slot
    public InventorySlot(int x, int y) {
        this.bounds = new Rectangle(x, y, SLOT_SIZE, SLOT_SIZE);
        this.selected = false; // Default state is not selected
    }

    // Set the item and its count for this slot
    public void setItem(Item item, int count) {
        this.item = item;
        this.count = count;
    }

    // Get the item in this slot
    public Item getItem() {
        return item;
    }

    // Check if a given point is inside the bounds of this slot
    public boolean contains(Point p) {
        return bounds.contains(p);
    }

    // Set the selection state of the slot
    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    // Draw the slot and its contents (item image and item count)
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
            
            // Display the count of the item in the slot
            g.setColor(Color.BLACK);
            String countStr = String.valueOf(count); // Convert item count to string
            FontMetrics fm = g.getFontMetrics();
            int textX = bounds.x + bounds.width - fm.stringWidth(countStr) - 5;
            int textY = bounds.y + bounds.height - 5;
            g.drawString(countStr, textX, textY); // Draw the count in the bottom-right corner
        }
    }
}
