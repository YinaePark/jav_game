package domain.item;

import javax.swing.ImageIcon;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.Graphics2D;
import java.awt.RenderingHints;

import java.awt.Color;

public abstract class Item implements UsableInDish {
    protected String name;
    protected ImageIcon sprite;

    public Item(String name) {
        this.name = name;
        loadSprite();
    }

    private void loadSprite() {
        try {
            // sprites 폴더 내의 items 폴더에서 이미지를 로드
            String imagePath = "/sprites/items/" + name.toLowerCase() + ".png";
            sprite = new ImageIcon(getClass().getResource(imagePath));
        } catch (Exception e) {
            System.err.println("Failed to load sprite for " + name + ": " + e.getMessage());
            // 이미지 로드 실패시 기본 이미지 사용
            sprite = new ImageIcon(getClass().getResource("/sprites/items/default.png"));
        }
    }

    public Image getSprite(int width, int height) {
        if (sprite == null || sprite.getImage() == null) {
            System.out.println("Sprite is null for: " + name);
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


    public String getName() {
        return name;
    }

    @Override
    public void useInDish() {
        System.out.println(name + " is used in the dish.");
    }

    public abstract double getPrice(); // 가격을 반환하는 메서드 (상점용)

    // Get color for item visualization
    public Color getColor() {
        switch (name.toLowerCase()) {
            case "tomato": return Color.RED;
            case "carrot": return Color.ORANGE;
            case "corn": return Color.YELLOW;
            case "lettuce": return Color.GREEN;
            case "onion": return Color.WHITE;
            case "olive": return Color.GRAY;
            case "wheat": return Color.YELLOW;
            case "truffle": return Color.BLACK;
            default: return Color.GREEN;
        }
    }
}