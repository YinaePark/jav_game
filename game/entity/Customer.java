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
    protected long spawnTime;  // 고객이 생성된 시간
    protected long remainingTime; // 남은 시간

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
    private List<Recipe> assignedRecipes; // 고객에게 할당된 요리 목록

    protected enum Direction {
        DOWN, UP, SIDE
    }

    public Customer(int x, int y) {
        this.x = x;
        this.y = y;
        this.spawnTime = System.currentTimeMillis();  // 고객이 생성된 시간
        this.assignedRecipes = null; // 초기에는 null
        this.satisfactionLevel = 5;  // initial satisfaction level
        this.maxWaitingTime = 30000;
        loadSprites();
        initializeCustomer();
    }

    public long getRemainingTime(){
        remainingTime = maxWaitingTime - (System.currentTimeMillis() - spawnTime);
        return remainingTime > 0 ? remainingTime : 0; // 0이하로 가지 않게 처리
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
    public abstract void updateSatisfaction(List<String> ingredients);
    public abstract int calculateReward();

    public void update() {
        if (isWaiting) {
            currentWaitingTime = (int)(System.currentTimeMillis() - spawnTime);
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
        long remainingTimeInSeconds = getRemainingTime() / 1000;
        String timeText = String.format("%d s", remainingTimeInSeconds);

        int textX = x + SIZE / 2 - 15; // 고객의 중앙 위치
        int textY = y - 10;

        // 동그라미 그리기
        int circleRadius = 15; // 동그라미 반지름
        g.setColor(Color.BLACK); // 동그라미 색상
        g.fillOval(textX, textY-20, circleRadius * 2, circleRadius * 2); // 동그라미 배경
        if (remainingTimeInSeconds > 10) {
            g.setColor(Color.WHITE);
        }
        else {g.setColor(Color.RED);}
        g.drawString(timeText, textX , textY);
        return;
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