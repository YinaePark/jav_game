package game.entity;

import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import game.recipe.Recipe;
import game.recipe.RecipeManager;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Random;
import java.util.List;

public class NormalCustomer extends Customer {
    private static final int MAX_WAITING_MINUTES = 30000; // Maximum waiting time (1/2 minute)
    private Random random;

    // Constructor
    public NormalCustomer(int x, int y) {
        super(x, y);
    }

    /**
     * Selects a random image path for the customer sprite.
     * @return the path to the selected image.
     */
    protected String loadRandomCustomerImage(){
        random = new Random();  // Random instance for image selection
        String[] customerImagePaths = {
                "sprites/player/customer1.png",
                "sprites/player/customer2.png",
                "sprites/player/customer3.png",
                "sprites/player/customer4.png",
                "sprites/player/customer5.png",
        };
        return customerImagePaths[random.nextInt(customerImagePaths.length)];
    }

    /*
     * Loads customer sprites for animations in different directions.
     */
    @Override
    protected void loadSprites() {
        try {
            File file = new File(loadRandomCustomerImage());
            if (file.exists()) {
                spriteSheet = ImageIO.read(file);

                // Calculate dimensions for sprite frames
                int frameWidth = spriteSheet.getWidth() / 3; // 3 columns
                int frameHeight = spriteSheet.getHeight() / 4; // 4 rows

                // initialize animation frames
                frontSprites = new BufferedImage[4];
                backSprites = new BufferedImage[4];
                sideSprites = new BufferedImage[4];

                // Load front-facing animations
                for (int i = 0; i < 4; i++) {
                    frontSprites[i] = spriteSheet.getSubimage(0, i * frameHeight, frameWidth, frameHeight);
                }

                // Load back-facing animations
                for (int i = 0; i < 4; i++) {
                    backSprites[i] = spriteSheet.getSubimage(frameWidth, i * frameHeight, frameWidth, frameHeight);
                }

                // Load side-facing animations
                for (int i = 0; i < 4; i++) {
                    sideSprites[i] = spriteSheet.getSubimage(frameWidth * 2, i * frameHeight, frameWidth, frameHeight);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading normal customer sprite: " + e.getMessage());
            spriteSheet = null;
        }
    }

    /*
     * Initializes customer-specific attributes, such as:
     * - Maximum waiting time.
     * - Satisfaction level.
     * - Randomly ordered menus.
     */
    @Override
    public void initializeCustomer() {
        this.maxWaitingTime = MAX_WAITING_MINUTES;
        this.currentWaitingTime = 0;
        this.satisfactionLevel = 100;

        // Fetch all recipes from RecipeManager
        RecipeManager recipeManager = RecipeManager.getInstance();
        List<String> availableMenus = new ArrayList<>(recipeManager.getAllRecipeNames());

        orderedMenus = new ArrayList<>();
        Random random = new Random();

        // Select three unique random menus
        while (orderedMenus.size() < 3 && !availableMenus.isEmpty()) {
            String menu = availableMenus.get(random.nextInt(availableMenus.size()));
            if (!orderedMenus.contains(menu)) {
                orderedMenus.add(menu);
            }
        }
    }

    /**
     * Updates the satisfaction level of the customer based on provided ingredients.
     * @param ingredients List of ingredients given by the player.
     */
    @Override
    public void updateSatisfaction(List<String> ingredients) {
        RecipeManager recipeManager = RecipeManager.getInstance();

        // Evaluate the first ordered menu
        String menu = orderedMenus.get(0);
        Recipe recipe = recipeManager.getRecipe(menu);

        if (recipe == null) {
            satisfactionLevel = 0; // Set satisfaction to 0 if recipe not found
            return;
        }

        List<String> recipeIngredients = recipe.getIngredients();

        // Criterion 1: Check if main ingredient is present (50 points)
        String mainIngredient = recipeIngredients.get(0);
        int score = userIngredientContainsMainIngredient(mainIngredient, ingredients) ? 50 : 0;

        // Criterion 2: Penalize for ingredient count difference (-10 points per mismatch)
        int ingredientDifference = Math.abs(recipeIngredients.size() - ingredients.size());
        score -= ingredientDifference * 10;

        // Criterion 3: Match additional ingredients (up to 50 points)
        int additionalIngredientMatch = calculateAdditionalIngredientsMatch(recipeIngredients, ingredients);
        score += additionalIngredientMatch * 50;

        // Clamp the score between 0 and 100
        score = Math.max(0, Math.min(score, 100));
        satisfactionLevel = score; // Convert to satisfaction level
    }

    /**
     * Calculates the reward for the player based of satisfaction level.
     * @return The calculated reward value.
     */
    @Override
    public int calculateReward() {
        RecipeManager recipeManager = RecipeManager.getInstance();
        String menu = orderedMenus.get(0);
        Recipe recipe = recipeManager.getRecipe(menu);

        if (recipe == null) {
            return 0; // No reward if recipe not found
        }

        // Base reward from the recipe
        int baseReward = recipe.getBaseReward();

        // Calculate satisfaction score (e.g., satisfaction * 2 - 100)
        double satisfactionScore = satisfactionLevel * 2 - 100;

        // Final reward calculation
        double reward = baseReward * (satisfactionScore / 100);
        return (int) reward;
    }

    // Helper method: Check if main ingredient is present
    private boolean userIngredientContainsMainIngredient(String mainIngredient, List<String> ingredients) {
        return ingredients.contains(mainIngredient);
    }

    // Helper method: Calculate additional ingredient match rate
    private int calculateAdditionalIngredientsMatch(List<String> recipeIngredients, List<String> userIngredients) {
        List<String> additionalRecipeIngredients = recipeIngredients.subList(1, recipeIngredients.size());

        int matchCount = 0;
        for (String ingredient : additionalRecipeIngredients) {
            if (userIngredients.contains(ingredient)) {
                matchCount++;
            }
        }

        // Calculate match rate and round to the nearest integer
        return (int) Math.round((double) matchCount / (additionalRecipeIngredients.size()));
    }
}