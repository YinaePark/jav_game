package game.recipe;

import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Arrays;
import java.util.ArrayList;

public class RecipeManager {
    private static RecipeManager instance;
    private Map<String, Recipe> recipes;

    private RecipeManager() {
        recipes = new HashMap<>();
        initializeRecipes();
    }

    public static RecipeManager getInstance() {
        if (instance == null) {
            instance = new RecipeManager();
        }
        return instance;
    }

    private void initializeRecipes() {
        // 3 ingredients recipes
        addRecipe("onion soup", Arrays.asList("onion", "water", "salt"), 100);

        // 4 ingredients recipes


        // 5 ingredients recipes
    }

    private void addRecipe(String name, List<String> ingredients, int baseReward) {
        recipes.put(name, new Recipe(name, ingredients, baseReward));
    }

    public Recipe getRecipe(String name) {
        return recipes.get(name);
    }

    public int checkIngredients(String recipeName, List<String> userIngredients) {
        Recipe recipe = recipes.get(recipeName);
        if (recipe == null) return 0;

        List<String> correctIngredients = recipe.getIngredients();
        int correct = 0;
        
        for (String ingredient : userIngredients) {
            if (correctIngredients.contains(ingredient)) {
                correct++;
            }
        }
        
        return correct;
    }

    // Get recipes by difficulty
    public List<Recipe> getRecipesByDifficulty(int difficulty) {
        List<Recipe> result = new ArrayList<>();
        for (Recipe recipe : recipes.values()) {
            if (recipe.getDifficulty() == difficulty) {
                result.add(recipe);
            }
        }
        return result;
    }
}