package game.recipe;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

/**
 * Represents a recipe with a name, list of ingredients, difficulty, and a sprite image.
 */
public class Recipe {
    private String name;
    private List<String> ingredients;
    private int difficulty;
    private int baseReward;
    private ImageIcon sprite;

    public Recipe(String name, List<String> ingredients, int baseReward) {
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
        this.difficulty = ingredients.size();
        this.baseReward = baseReward;
        loadSprite();
    }

    /**
     * Loads the sprite image associated with the recipe.
     * Attempts to load the image from the "sprites/menus" folder.
     * If the image cannot be loaded, it defaults to a generic image.
     */
    private void loadSprite() {
        try {
            String imagePath = "/sprites/menus/" + name.toLowerCase() + ".png";
            sprite = new ImageIcon(getClass().getResource(imagePath));  // Load the image using the recipe name
        } catch (Exception e) {
            System.err.println("Failed to load sprite for recipe " + name + ": " + e.getMessage());
            sprite = new ImageIcon(getClass().getResource("/sprites/menus/default.png"));  // Default image on failure
        }
    }
    /**
     * Scales the sprite image to the given width and height.
     */
    public Image getSprite(int width, int height) {
        if (sprite == null || sprite.getImage() == null) {
            System.out.println("Sprite is null for recipe: " + name);
            return null;
        }

        Image img = sprite.getImage();

        // Convert original image to BufferedImage
        BufferedImage originalImage = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = originalImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        // Create a new BufferedImage for the scaled version
        BufferedImage scaledImage = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
        g = scaledImage.createGraphics();
        g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g.drawImage(originalImage, 0, 0, width, height, null);
        g.dispose();

        return scaledImage;
    }

    // Getters
    public String getName() { return name; }
    public List<String> getIngredients() { return new ArrayList<>(ingredients); }
    public int getDifficulty() { return difficulty; }
    public int getBaseReward() { return baseReward; }
    public ImageIcon getSprite() { return sprite; }
}
