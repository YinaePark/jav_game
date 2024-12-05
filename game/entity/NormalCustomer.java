package game.entity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import game.recipe.RecipeManager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class NormalCustomer extends Customer {
    private static final int MAX_WAITING_MINUTES = 300;  // 5min
    private static final int BASE_REWARD = 100;         // base reward
    private static final double SATISFACTION_MULTIPLIER = 1.2; // satisfaction multiplier
    
    public NormalCustomer(int x, int y) {
        super(x, y);
    }
    
    @Override
    protected void loadSprites() {
        try {
            File file = new File("sprites/normal_customer.png");
            if (file.exists()) {
                spriteSheet = ImageIO.read(file);
                int frameWidth = spriteSheet.getWidth() / 3;  // 3 columns
                int frameHeight = spriteSheet.getHeight() / 4; // 4 rows
                
                // initialize sprite arrays
                frontSprites = new BufferedImage[4];
                backSprites = new BufferedImage[4];
                sideSprites = new BufferedImage[4];
                
                // Load front animations
                for (int i = 0; i < 4; i++) {
                    frontSprites[i] = spriteSheet.getSubimage(0, i * frameHeight, frameWidth, frameHeight);
                }
                
                // Load back animations
                for (int i = 0; i < 4; i++) {
                    backSprites[i] = spriteSheet.getSubimage(frameWidth, i * frameHeight, frameWidth, frameHeight);
                }
                
                // Load side animations
                for (int i = 0; i < 4; i++) {
                    sideSprites[i] = spriteSheet.getSubimage(frameWidth * 2, i * frameHeight, frameWidth, frameHeight);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading normal customer sprite: " + e.getMessage());
            spriteSheet = null;
        }
    }
    
    @Override
    protected void initializeCustomer() {
        this.maxWaitingTime = MAX_WAITING_MINUTES;
        this.currentWaitingTime = 0;
        this.satisfactionLevel = 5;
        
        // choose 3 random menus
        Random random = new Random();
        orderedMenus = new ArrayList<>();
        // available menus
        String[] availableMenus = {"onion soup", "salad", "tomato pasta", "sandwich", "steak", "escargot"};
        
        while (orderedMenus.size() < 3) {
            String menu = availableMenus[random.nextInt(availableMenus.length)];
            if (!orderedMenus.contains(menu)) {
                orderedMenus.add(menu);
            }
        }
    }
    
    @Override
    protected void updateSatisfaction(int correctIngredients) {
        // need to implement
    }
    
    @Override
    protected int calculateReward() {
        // calculate reward based on satisfaction level
        double reward = BASE_REWARD * (satisfactionLevel * SATISFACTION_MULTIPLIER);
        
        // if satisfaction level is 1 or lower, penalize the reward
        if (satisfactionLevel <= 1) {
            reward = -BASE_REWARD;  // lose money
        }
        
        return (int) reward;
    }
    
    @Override
    protected int checkIngredients(String menu, List<String> ingredients) {
        RecipeManager recipeManager = RecipeManager.getInstance();
        return recipeManager.checkIngredients(menu, ingredients);
    }
}