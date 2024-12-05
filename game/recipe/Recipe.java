package game.recipe;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.Color;
import java.util.List;
import java.util.ArrayList;

public class Recipe {
    private String name;            // 메뉴 이름
    private List<String> ingredients;
    private int difficulty;         // 재료의 개수
    private int baseReward;
    private ImageIcon sprite;       // 레시피 이미지 추가

    public Recipe(String name, List<String> ingredients, int baseReward) {
        this.name = name;
        this.ingredients = new ArrayList<>(ingredients);
        this.difficulty = ingredients.size();
        this.baseReward = baseReward;
        loadSprite();  // sprite를 로드
    }

    // Sprite 로딩
    private void loadSprite() {
        try {
            // "sprites" 폴더 내의 "recipes" 폴더에서 이미지를 로드
            String imagePath = "/sprites/menus/" + name.toLowerCase() + ".png";
            sprite = new ImageIcon(getClass().getResource(imagePath));
        } catch (Exception e) {
            System.err.println("Failed to load sprite for recipe " + name + ": " + e.getMessage());
            // 이미지 로드 실패시 기본 이미지 사용
            sprite = new ImageIcon(getClass().getResource("/sprites/menus/default.png"));
        }
    }

    // Sprite 크기 변경
    public Image getSprite(int width, int height) {
        if (sprite == null || sprite.getImage() == null) {
            System.out.println("Sprite is null for recipe: " + name);
            return null;
        }

        Image img = sprite.getImage();

        // 원본 이미지를 BufferedImage로 변환
        BufferedImage originalImage = new BufferedImage(
                img.getWidth(null),
                img.getHeight(null),
                BufferedImage.TYPE_INT_ARGB
        );
        Graphics2D g = originalImage.createGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();

        // 새로운 크기의 BufferedImage 생성
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
    public ImageIcon getSprite() { return sprite; }  // Sprite 반환 메서드 추가
}
