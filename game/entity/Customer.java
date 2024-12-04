package game.entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Random;

public abstract class Customer {
    protected int x, y;
    protected static final int SIZE = 40;
    protected BufferedImage spriteSheet;
    protected BufferedImage[] sprites;
    protected int currentFrame = 0;
    protected int animationDelay = 0;
    protected static final int ANIMATION_SPEED = 12;
    protected boolean isWaiting = true;
    
    protected List<String> orderedMenus;  // menu list
    protected int satisfactionLevel;      // satisfaction level(0~5)
    protected int maxWaitingTime;         // maximum waiting time
    protected int currentWaitingTime;     // current waiting time
    
    public Customer(int x, int y) {
        this.x = x;
        this.y = y;
        this.satisfactionLevel = 5;  // initial satisfaction level
        loadSprites();
        initializeCustomer();
    }
    
    // abstract methods
    protected abstract void loadSprites();  // load sprites for customer
    protected abstract void initializeCustomer();
    protected abstract void updateSatisfaction(int correctIngredients);
    protected abstract int calculateReward();
    
    public void update() {
        if (isWaiting) {
            currentWaitingTime++;
            if (currentWaitingTime >= maxWaitingTime) {
                decreaseSatisfaction();
            }
        }
        updateAnimation();
    }
    
    protected void updateAnimation() {
        animationDelay++;
        if (animationDelay >= ANIMATION_SPEED) {
            currentFrame = (currentFrame + 1) % sprites.length;
            animationDelay = 0;
        }
    }
    
    public void draw(Graphics g) {
        if (sprites != null && sprites.length > 0) {
            BufferedImage currentSprite = sprites[currentFrame];
            if (currentSprite != null) {
                g.drawImage(currentSprite, x, y, SIZE, SIZE, null);
            }
        } else {
            g.setColor(Color.BLUE);
            g.fillRect(x, y, SIZE, SIZE);
        }
    }
    
    protected void decreaseSatisfaction() {
        if (satisfactionLevel > 0) {
            satisfactionLevel--;
        }
    }
    
    public int evaluateFood(String menu, List<String> ingredients) {
        if (!orderedMenus.contains(menu)) {
            return -1;
        }
        
        int correctIngredients = checkIngredients(menu, ingredients);
        updateSatisfaction(correctIngredients);
        
        return calculateReward();
    }
    
    protected int checkIngredients(String menu, List<String> ingredients) {
        return 0; 
    }
    
    // Getter/Setter
    public List<String> getOrderedMenus() { return orderedMenus; }
    public int getSatisfactionLevel() { return satisfactionLevel; }
    public boolean isWaiting() { return isWaiting; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getCurrentWaitingTime() { return currentWaitingTime; }
}