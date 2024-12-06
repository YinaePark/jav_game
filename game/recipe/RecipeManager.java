package game.recipe;

import java.util.*;

public class RecipeManager {
    private static RecipeManager instance; // Singleton instance
    private Map<String, Recipe> recipes;   // Stores recipes by name

    // Private constructor for singleton
    private RecipeManager() {
        recipes = new HashMap<>();
        initializeRecipes(); // Initialize the recipe collection
    }

    /**
     * Returns the singleton instance of RecipeManager.
     * @return The shared instance of RecipeManager.
     */
    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    /**
     * Retrieves a specified number of random recipes from the available recipes.
     * @param count Number of recipes to retrieve.
     * @return A list of random Recipe objects.
     */
    public List<Recipe> getRandomRecipes(int count) {
        List<Recipe> allRecipes = new ArrayList<>(recipes.values()); // Get all recipes
        List<Recipe> randomRecipes = new ArrayList<>();
        Random random = new Random();

        // Randomly select recipes until the desired count is reached
        while (randomRecipes.size() < count && !allRecipes.isEmpty()) {
            int index = random.nextInt(allRecipes.size());
            randomRecipes.add(allRecipes.remove(index)); // Add and remove the selected recipe
        }
        return randomRecipes;
    }

    /**
     * Initialize predefined recipes with their ingredients and base rewards.
     */
    private void initializeRecipes() {
        // Add recipes with 3 ingredients
        addRecipe("onion_soup", Arrays.asList("onion", "cheese", "butter"), 6);
        addRecipe("salad", Arrays.asList("lettuce", "tomato", "olive"), 5);
        addRecipe("tomato_pasta", Arrays.asList("wheat", "tomato", "meat"), 9);
        addRecipe("sandwich", Arrays.asList("wheat", "lettuce", "tomato"), 7);
        addRecipe("steak", Arrays.asList("meat", "onion", "butter"), 10);
        addRecipe("escargot", Arrays.asList("snail", "onion", "butter"), 11);

        // Add recipes with 4 ingredients
        addRecipe("hamburger", Arrays.asList("meat", "wheat", "lettuce", "tomato"), 9);

        // Add recipes with 5 ingredients
        addRecipe("carbonara_pasta", Arrays.asList("wheat", "egg", "cheese", "meat", "onion"), 11);
        addRecipe("cream_gnocchi", Arrays.asList("wheat", "milk", "potato", "butter", "cheese"), 12);
        addRecipe("truffle_pasta", Arrays.asList("truffle", "cheese", "milk", "butter", "wheat"), 16);
    }

    /**
     * Adds a new recipe to the collection.
     * @param name The name of the recipe.
     * @param ingredients The list of ingredients required for the recipe.
     * @param baseReward The base reward for completing the recipe.
     */
    private void addRecipe(String name, List<String> ingredients, int baseReward) {
        recipes.put(name, new Recipe(name, ingredients, baseReward));
    }

    /**
     * Retrieves a recipe by its name.
     * @param name The name of the recipe.
     * @return The Recipe object, or null if not found.
     */
    public Recipe getRecipe(String name) {
        return recipes.get(name);
    }

    /**
     * Retrieves the names of all available recipes.
     * @return A list of recipe names.
     */
    public List<String> getAllRecipeNames() {
        return new ArrayList<>(recipes.keySet());
    }
}