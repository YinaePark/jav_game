package game.entity;

import java.awt.*;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import java.io.File;
import java.io.IOException;

public class PlayerRenderer {
    private int x, y;
    private static final int SIZE = 40;
    private BufferedImage spriteSheet;
    private BufferedImage[] frontSprites;
    private BufferedImage[] backSprites;
    private BufferedImage[] sideSprites;
    private int currentFrame = 0;
    private int animationDelay = 0;
    private static final int ANIMATION_SPEED = 8;
    private Direction facing = Direction.DOWN;
    private boolean isMoving = false;
    private boolean facingLeft = true;
    private double targetX;
    private double targetY;
    private static final double MOVEMENT_SPEED = 6.0;
    private boolean isMovingToTarget = false;

    private enum Direction {
        DOWN, UP, SIDE
    }
    
    public PlayerRenderer(int x, int y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        loadSprites();
    }

    private void loadSprites() {
        try {
            File file = new File("sprites/player.png");
            if (file.exists()) {
                spriteSheet = ImageIO.read(file);
                int frameWidth = spriteSheet.getWidth() / 3;
                int frameHeight = spriteSheet.getHeight() / 4;
                
                // init sprite arrays
                frontSprites = new BufferedImage[4];
                backSprites = new BufferedImage[4];
                sideSprites = new BufferedImage[4];
                
                for (int i = 0; i < 4; i++) {
                    frontSprites[i] = spriteSheet.getSubimage(0, i * frameHeight, frameWidth, frameHeight);
                }
            
                for (int i = 0; i < 4; i++) {
                    backSprites[i] = spriteSheet.getSubimage(frameWidth, i * frameHeight, frameWidth, frameHeight);
                }
                
                for (int i = 0; i < 4; i++) {
                    sideSprites[i] = spriteSheet.getSubimage(frameWidth * 2, i * frameHeight, frameWidth, frameHeight);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading sprite: " + e.getMessage());
            spriteSheet = null;
        }
    }
    
    public void move(int dx, int dy) {
        if (dx != 0 || dy != 0) {
            isMoving = true;

            int currentTileX = x / SIZE;
            int currentTileY = y / SIZE;

            int nextTileX = currentTileX + (dx > 0 ? 1 : dx < 0 ? -1 : 0);
            int nextTileY = currentTileY + (dy > 0 ? 1 : dy < 0 ? -1 : 0);

            targetX = nextTileX * SIZE + (SIZE - this.SIZE) / 2;
            targetY = nextTileY * SIZE + (SIZE - this.SIZE) / 2;

            targetX = Math.max(0, Math.min(targetX, 800 - SIZE));
            targetY = Math.max(0, Math.min(targetY, 600 - SIZE));

            isMovingToTarget = true;
            
            // set facing direction
            if (Math.abs(dx) > Math.abs(dy)) {
                facing = Direction.SIDE;
                facingLeft = dx < 0;  // true if moving left
            } else {
                facing = dy > 0 ? Direction.DOWN : Direction.UP;
            }
        }
    }

    public void update() {
        if (isMovingToTarget) {
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);
            
            if (distance < MOVEMENT_SPEED) {
                x = (int) targetX;
                y = (int) targetY;
                isMovingToTarget = false;
                isMoving = false;
            } else {
                double moveX = (dx / distance) * MOVEMENT_SPEED;
                double moveY = (dy / distance) * MOVEMENT_SPEED;
                x += (int) moveX;
                y += (int) moveY;
            }
        }
        
        updateAnimation();
    }
    
    public void updateAnimation() {
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
            g.setColor(Color.RED);
            g.fillRect(x, y, SIZE, SIZE);
        }
    }

    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return SIZE; }
    public boolean isMovingToTarget() { return isMovingToTarget; }
}