package game.entity;

import game.recipe.Recipe;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public abstract class Customer {
    protected int x, y;
    protected static final int SIZE = 40;
    protected BufferedImage spriteSheet;
    protected BufferedImage[] sprites;
    protected int currentFrame = 0;
    protected int animationDelay = 0;
    protected static final int ANIMATION_SPEED = 12;
    protected boolean isWaiting = true;
    protected BufferedImage[] frontSprites;
    protected BufferedImage[] backSprites;
    protected BufferedImage[] sideSprites;
    protected Direction facing = Direction.DOWN;
    protected boolean facingLeft = true;
    protected boolean isMoving = false;
    protected long spawnTime;  // The time the customer was created
    protected long remainingTime; // Remaining time for the customer to wait
    protected List<String> orderedMenus;  // menu list
    protected int satisfactionLevel;      // satisfaction level(0~5)
    protected int maxWaitingTime;         // maximum waiting time
    protected int currentWaitingTime;     // current waiting time
    public boolean isWaitingTooLong() {
        return currentWaitingTime >= maxWaitingTime;
    }
    public boolean isOrderComplete() {
        return false;
    }
    private List<Recipe> assignedRecipes;

    protected enum Direction {
        DOWN, UP, SIDE
    }

    public Customer(int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();
        this.assignedRecipes = null;
        this.satisfactionLevel = 5;
        this.maxWaitingTime = 30000;
        loadSprites();
        initializeCustomer();
    }

    public long getRemainingTime(){
        remainingTime = maxWaitingTime - (System.currentTimeMillis() - spawnTime);
        return remainingTime > 0 ? remainingTime : 0; // Prevent negative remaining time
    }


    public List<Recipe> getAssignedRecipes() {
        return assignedRecipes;
    }
    public void assignRecipes(List<Recipe> recipes) {
        this.assignedRecipes = recipes;
    }

    // abstract methods
    protected abstract void loadSprites();  // load sprites for customer
    public abstract void initializeCustomer();
    public abstract void updateSatisfaction(List<String> ingredients);
    public abstract int calculateReward();

    /**
     * Update the customer state: checks waiting time and updates animation.
     * If the customer has waited too long, their satisfaction decreases.
     */
    public void update() {
        if (isWaiting) {
            currentWaitingTime = (int)(System.currentTimeMillis() - spawnTime);
            if (currentWaitingTime >= maxWaitingTime) {
                decreaseSatisfaction();
            }
        }
        updateAnimation();
    }
    /**
     * Updates the animation frames when the customer is moving.
     * If the customer is idle, resets the frame to 0.
     */
    protected void updateAnimation() {
        if (isMoving) {
            animationDelay++;
            if (animationDelay >= ANIMATION_SPEED) {
                currentFrame = (currentFrame + 1) % 4;
                animationDelay = 0;
            }
        } else {
            currentFrame = 0;  // idle frame
        }
    }
    /**
     * Draw the customer on the screen at their current position.
     * Includes rendering the sprite and the remaining wait time.
     * @param g Graphics object to render the customer
     */
    public void draw(Graphics g) {
        if (spriteSheet != null) {
            BufferedImage currentSprite = null;

            // determine which sprite to draw
            switch (facing) {
                case DOWN:
                    currentSprite = frontSprites[currentFrame];
                    break;
                case UP:
                    currentSprite = backSprites[currentFrame];
                    break;
                case SIDE:
                    currentSprite = sideSprites[currentFrame];
                    break;
            }

            if (currentSprite != null) {
                if (facing == Direction.SIDE && facingLeft) {
                    // flip sprite horizontally
                    g.drawImage(currentSprite, x + SIZE, y, -SIZE, SIZE, null);
                } else {
                    g.drawImage(currentSprite, x, y, SIZE, SIZE, null);
                }
            }
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, SIZE, SIZE);
        }
        long remainingTimeInSeconds = getRemainingTime() / 1000;
        String timeText = String.format("%d s", remainingTimeInSeconds);

        // Positioning the time label above the customer
        int textX = x + SIZE / 2 - 15; // Centered horizontally
        int textY = y - 10;

        // Draw a circle background for the time label
        int circleRadius = 15;
        g.setColor(Color.BLACK);
        g.fillOval(textX, textY-20, circleRadius * 2, circleRadius * 2); // 동그라미 배경
        if (remainingTimeInSeconds > 10) {
            g.setColor(Color.WHITE);
        }
        else {g.setColor(Color.RED);}
        g.drawString(timeText, textX , textY);
    }

    protected void decreaseSatisfaction() {
        if (satisfactionLevel > 0) {
            satisfactionLevel--;
        }
    }
    /**
     * Evaluates the food based on the customer's menu order and the provided ingredients.
     */
    public int evaluateFood(String menu, List<String> ingredients) {
        if (!orderedMenus.contains(menu)) {
            return -1;
        }
        updateSatisfaction(ingredients);
        return calculateReward();
    }

    // Getter/Setter methods for customer properties
    public List<String> getOrderedMenus() { return orderedMenus; }
    public int getSatisfactionLevel() { return satisfactionLevel; }
    public boolean isWaiting() { return isWaiting; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getCurrentWaitingTime() { return currentWaitingTime; }

    /**
     * Checks if the given mouse coordinates are within the customer’s bounds.
     */
    public boolean contains(int mouseX, int mouseY) {
        return mouseX >= x && mouseX <= x + SIZE && mouseY >= y && mouseY <= y + SIZE;
    }
}