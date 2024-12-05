package game.recipe;

import java.util.*;

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
    public List<Recipe> getRandomRecipes(int count) {
        List<Recipe> allRecipes = new ArrayList<>(recipes.values()); // 모든 레시피 가져오기
        List<Recipe> randomRecipes = new ArrayList<>();
        Random random = new Random();

        while (randomRecipes.size() < count && !allRecipes.isEmpty()) {
            int index = random.nextInt(allRecipes.size());
            randomRecipes.add(allRecipes.remove(index)); // 선택된 레시피 추가 후 제거
        }

        return randomRecipes;
    }

    private void initializeRecipes() {
        // 3 ingredients recipes
        addRecipe("onion_soup", Arrays.asList("onion", "cheese", "butter"), 6);
        addRecipe("salad", Arrays.asList("lettuce", "tomato", "olive"), 5);
        addRecipe("tomato_pasta", Arrays.asList("wheat", "tomato", "meat"), 9);
        addRecipe("sandwich", Arrays.asList("wheat", "lettuce", "tomato"), 7);
        addRecipe("steak", Arrays.asList("meat", "onion", "butter"), 10);
        addRecipe("escargot", Arrays.asList("snail", "onion", "butter"), 11);

        // 4 ingredients recipes
        addRecipe("hamburger", Arrays.asList("meat", "wheat", "lettuce", "tomato"), 9);

        // 5 ingredients recipes
        addRecipe("carbonara_pasta", Arrays.asList("wheat", "egg", "cheese", "meat", "onion"), 11);
        addRecipe("cream_gnocchi", Arrays.asList("wheat", "milk", "potato", "butter", "cheese"), 12);
        addRecipe("truffle_pasta", Arrays.asList("truffle", "cheese", "milk", "butter", "wheat"), 16);
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

    public List<String> getAllRecipeNames() {
        return new ArrayList<>(recipes.keySet());
    }
}