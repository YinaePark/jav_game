//package domain;
//
//import java.util.List;
//
//public class Dish {
//    private String name;
//    private List<Ingredient> ingredients;
//
//    public Dish(String name, List<Ingredient> ingredients) {
//        this.name = name;
//        this.ingredients = ingredients;
//    }
//
//    public String getName() {
//        return name;
//    }
//
//    public List<Ingredient> getIngredients() {
//        return ingredients;
//    }
//
//    public double calculateDishValue() {
//        double value = 0;
//        for (Ingredient ingredient : ingredients) {
//            value += ingredient.getPrice();
//        }
//        return value;
//    }
//
//    public boolean isCorrectDish(List<Ingredient> givenIngredients) {
//        return givenIngredients.equals(ingredients);
//    }
//}
