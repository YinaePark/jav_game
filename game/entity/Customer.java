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
    
    protected List<String> orderedMenus;  // menu list
    protected int satisfactionLevel;      // satisfaction level(0~5)
    protected int maxWaitingTime;         // maximum waiting time
    protected int currentWaitingTime;     // current waiting time
    public boolean isWaitingTooLong() {
        return false;
    }
    public boolean isOrderComplete() {
        return false;
    }
    private List<Recipe> assignedRecipes; // 고객에게 할당된 요리 목록

    protected enum Direction {
        DOWN, UP, SIDE
    }

    public Customer(int x, int y) {
        this.x = x;
        this.y = y;
        this.assignedRecipes = null; // 초기에는 null
        this.satisfactionLevel = 5;  // initial satisfaction level
        loadSprites();
        initializeCustomer();
    }

    public List<Recipe> getAssignedRecipes() {
        return assignedRecipes;
    }

    public void assignRecipes(List<Recipe> recipes) {
        this.assignedRecipes = recipes;
    }

    public boolean hasAssignedRecipes() {
        return assignedRecipes != null;
    }
    // abstract methods
    protected abstract void loadSprites();  // load sprites for customer
    protected abstract void initializeCustomer();
    protected abstract void updateSatisfaction(List<String> ingredients);
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
        
        updateSatisfaction(ingredients);
        
        return calculateReward();
    }
    
    // Getter/Setter
    public List<String> getOrderedMenus() { return orderedMenus; }
    public int getSatisfactionLevel() { return satisfactionLevel; }
    public boolean isWaiting() { return isWaiting; }
    public int getX() { return x; }
    public int getY() { return y; }
    public int getCurrentWaitingTime() { return currentWaitingTime; }
    // 추가된 contains 메서드
    public boolean contains(int mouseX, int mouseY) {
        // 마우스 클릭이 고객 영역 내에 있는지 확인
        return mouseX >= x && mouseX <= x + SIZE && mouseY >= y && mouseY <= y + SIZE;
    }

}