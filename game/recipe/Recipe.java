package game.recipe;

import java.util.List;
import java.util.ArrayList;

public class Recipe {
    private String name;            // menu name
    private List<String> ingredients;
    private int difficulty;         // number of ingredients
    private int baseReward;

    public Recipe(String name, List<String> ingredients, int baseReward) {
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
        this.difficulty = ingredients.size();
        this.baseReward = baseReward;
    }

    // Getters
    public String getName() { return name; }
    public List<String> getIngredients() { return new ArrayList<>(ingredients); }
    public int getDifficulty() { return difficulty; }
    public int getBaseReward() { return baseReward; }
}