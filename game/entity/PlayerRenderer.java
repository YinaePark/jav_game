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

    // Enum to represent the possible directions the player can face
    private enum Direction {
        DOWN, UP, SIDE
    }

    // Constructor that initializes the player position and loads the sprite sheet
    public PlayerRenderer(int x, int y) {
        this.x = x;
        this.y = y;
        this.targetX = x;
        this.targetY = y;
        loadSprites(); // Load the sprite sheet and create individual sprites for different directions
    }

    // Loads the sprite sheet and extracts individual sprites for each animation direction
    private void loadSprites() {
        try {
            File file = new File("sprites/player/player.png");
            if (file.exists()) {
                spriteSheet = ImageIO.read(file);
                int frameWidth = spriteSheet.getWidth() / 3;
                int frameHeight = spriteSheet.getHeight() / 4;
                
                // Initialize sprite arrays for each direction
                frontSprites = new BufferedImage[4];
                backSprites = new BufferedImage[4];
                sideSprites = new BufferedImage[4];

                // Extract front (downward) facing sprites
                for (int i = 0; i < 4; i++) {
                    frontSprites[i] = spriteSheet.getSubimage(0, i * frameHeight, frameWidth, frameHeight);
                }

                // Extract back (upward) facing sprites
                for (int i = 0; i < 4; i++) {
                    backSprites[i] = spriteSheet.getSubimage(frameWidth, i * frameHeight, frameWidth, frameHeight);
                }

                // Extract side (left/right) facing sprites
                for (int i = 0; i < 4; i++) {
                    sideSprites[i] = spriteSheet.getSubimage(frameWidth * 2, i * frameHeight, frameWidth, frameHeight);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading sprite: " + e.getMessage());
            spriteSheet = null;
        }
    }

    // Moves the player by dx and dy (change in x and y)
    public void move(int dx, int dy) {
        if (dx != 0 || dy != 0) {
            isMoving = true;

            // Calculate the target position based on movement
            int currentTileX = x / SIZE;
            int currentTileY = y / SIZE;

            int nextTileX = currentTileX + (dx > 0 ? 1 : dx < 0 ? -1 : 0);
            int nextTileY = currentTileY + (dy > 0 ? 1 : dy < 0 ? -1 : 0);

            targetX = nextTileX * SIZE + (SIZE - this.SIZE) / 2;
            targetY = nextTileY * SIZE + (SIZE - this.SIZE) / 2;

            // Keep the player within the bounds of the screen
            targetX = Math.max(0, Math.min(targetX, 800 - SIZE));
            targetY = Math.max(0, Math.min(targetY, 600 - SIZE));

            isMovingToTarget = true;

            // Set facing direction based on movement
            if (Math.abs(dx) > Math.abs(dy)) {
                facing = Direction.SIDE;
                facingLeft = dx < 0;  // true if moving left
            } else {
                facing = dy > 0 ? Direction.DOWN : Direction.UP;
            }
        }
    }

    // Updates the player's position towards the target and manages animation
    public void update() {
        if (isMovingToTarget) {
            double dx = targetX - x;
            double dy = targetY - y;
            double distance = Math.sqrt(dx * dx + dy * dy);

            // Move towards the target position
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
            currentFrame = 0;  // Idle frame when not moving
        }
    }

    // Draws the player at its current position with the correct animation
    public void draw(Graphics g) {
        if (spriteSheet != null) {
            BufferedImage currentSprite = null;

            // Select the correct sprite based on the direction the player is facing
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

            // Draw the sprite on the screen, flipping it horizontally if facing left
            if (currentSprite != null) {
                if (facing == Direction.SIDE && facingLeft) {
                    // flip sprite horizontally
                    g.drawImage(currentSprite, x + SIZE, y, -SIZE, SIZE, null);
                } else {
                    g.drawImage(currentSprite, x, y, SIZE, SIZE, null);
                }
            }
        } else {
            // Fallback if sprite sheet is not loaded
            g.setColor(Color.RED);
            g.fillRect(x, y, SIZE, SIZE);
        }
    }

    // Getters for player position and size
    public int getX() { return x; }
    public int getY() { return y; }
    public int getSize() { return SIZE; }
    public boolean isMovingToTarget() { return isMovingToTarget; }
}