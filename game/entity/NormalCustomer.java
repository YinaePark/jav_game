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

import javax.swing.*;
import java.awt.*;
import java.util.Random;

public class NormalCustomer extends Customer {
    private static final int MAX_WAITING_MINUTES = 300;  // 5min
    private static final int BASE_REWARD = 100;         // base reward
    private static final double SATISFACTION_MULTIPLIER = 1.2; // satisfaction multiplier
    
    private Image[] customerImages; //5개의 손님 이미지 배열
    private Image currentImage;
    private Random random = new Random();

    private BufferedImage spriteSheet;
    private BufferedImage[] frontSprites;
    private BufferedImage[] backSprites;
    private BufferedImage[] sideSprites;

    public NormalCustomer(int x, int y) {
        super(x, y);
        initializeCustomer(); 
        loadSprites();
    }
    
    @Override
    protected void loadSprites() {
        try {
            String spritePath = "sprites/player/" + getCustomerSpriteFileName();
            File file = new File(spritePath);
            if (file.exists()) {
                spriteSheet = ImageIO.read(file);
                int frameWidth = spriteSheet.getWidth() / 3;  // 3 columns
                int frameHeight = spriteSheet.getHeight() / 3; // 3 rows
                
                // initialize sprite arrays
                frontSprites = new BufferedImage[3];
                backSprites = new BufferedImage[3];
                sideSprites = new BufferedImage[3];
                
                // Load front animations
                for (int i = 0; i < 3; i++) {
                    frontSprites[i] = spriteSheet.getSubimage(0, i * frameHeight, frameWidth, frameHeight);
                }
                
                // Load back animations
                for (int i = 0; i < 3; i++) {
                    backSprites[i] = spriteSheet.getSubimage(frameWidth, i * frameHeight, frameWidth, frameHeight);
                }
                
                // Load side animations
                for (int i = 0; i < 3; i++) {
                    sideSprites[i] = spriteSheet.getSubimage(frameWidth * 2, i * frameHeight, frameWidth, frameHeight);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading normal customer sprite: " + e.getMessage());
            spriteSheet = null;
        }
    }

    private String getCustomerSpriteFileName() {
        // customer 이미지를 기반으로 스프라이트 시트 이름을 결정
        if (currentImage.equals(customerImages[0])) {
            return "customer1_sprite.png";  // customer1 스프라이트 시트
        } else if (currentImage.equals(customerImages[1])) {
            return "customer2_sprite.png";  // customer2 스프라이트 시트
        } else if (currentImage.equals(customerImages[2])) {
            return "customer3_sprite.png";  // customer3 스프라이트 시트
        } else if (currentImage.equals(customerImages[3])) {
            return "customer4_sprite.png";  // customer4 스프라이트 시트
        } else {
            return "customer5_sprite.png";  // customer5 스프라이트 시트
        }
    }

    @Override
    protected void initializeCustomer() {
        this.maxWaitingTime = MAX_WAITING_MINUTES;
        this.currentWaitingTime = 0;
        this.satisfactionLevel = 5;
        
        //손님 이미지 파일 경로 배열에 저장
        customerImages = new Image [5];
        customerImages[0] = new ImageIcon("sprites/player/customer1.png").getImage();
        customerImages[1] = new ImageIcon("sprites/player/customer2.png").getImage();
        customerImages[2] = new ImageIcon("sprites/player/customer3.png").getImage();
        customerImages[3] = new ImageIcon("sprites/player/customer4.png").getImage();
        customerImages[4] = new ImageIcon("sprites/player/customer5.png").getImage();
    
        // 랜덤하게 하나의 이미지 선택
        currentImage = customerImages[random.nextInt(customerImages.length)];

        loadSprites();
        
        // choose 3 random menus
        Random random = new Random();
        orderedMenus = new ArrayList<>();
        // available menus
        String[] availableMenus = {"onion soup", "salad", "tomato pasta", "sandwich", "steak", "escargot", "hamburger", "carbonara pasta", "cream gnocchi", "truffle pasta"};
        
        while (orderedMenus.size() < 3) {
            String menu = availableMenus[random.nextInt(availableMenus.length)];
            if (!orderedMenus.contains(menu)) {
                orderedMenus.add(menu);
            }
        }
    }
    
    @Override
    protected void updateSatisfaction(List<String> ingredients) {
        // RecipeManager에서 레시피의 상세 정보 가져오기
        RecipeManager recipeManager = RecipeManager.getInstance();
        
        // 주문한 메뉴 중 첫 번째 메뉴의 평가
        String menu = orderedMenus.get(0);
        Recipe recipe = recipeManager.getRecipe(menu);
        
        if (recipe == null) {
            satisfactionLevel = 0;
            return;
        }
        
        List<String> recipeIngredients = recipe.getIngredients();
        
        // 첫 번째 기준: 주재료 확인 (50점)
        String mainIngredient = recipeIngredients.get(0);
        int score = userIngredientContainsMainIngredient(mainIngredient, ingredients) ? 50 : 0;
        
        // 두 번째 기준: 재료 개수 차이 (-10점씩)
        int ingredientDifference = Math.abs(recipeIngredients.size() - ingredients.size());
        score -= ingredientDifference * 10;
        
        // 세 번째 기준: 주재료 제외한 재료 일치율 (50점)
        int additionalIngredientMatch = calculateAdditionalIngredientsMatch(recipeIngredients, ingredients);
        score += additionalIngredientMatch * 50;
        
        // 점수 0 이하일 경우 0으로 설정
        score = Math.max(0, Math.min(score, 100));
        
        // 만족도 수준으로 변환 (0~5)
        satisfactionLevel = score / 20;

    }
    
    @Override
    protected int calculateReward() {
        // RecipeManager에서 레시피의 상세 정보 가져오기
        RecipeManager recipeManager = RecipeManager.getInstance();
        String menu = orderedMenus.get(0);
        Recipe recipe = recipeManager.getRecipe(menu);
        
        if (recipe == null) {
            return 0;
        }
        
        // 레시피의 기본 보상값
        int baseReward = recipe.getBaseReward();
        
        // 만족도 점수 계산: (만족도**2 - 100) 
        double satisfactionScore = Math.pow(satisfactionLevel * 20, 2) - 100;
        
        // 최종 보상 계산
        double reward = baseReward * (satisfactionScore / 100);
        
        // 만족도가 낮으면 음수 보상
        if (satisfactionLevel <= 1) {
            reward = -BASE_REWARD;
    }
    
    return (int) reward;

}

// 주재료 포함 여부 확인
private boolean userIngredientContainsMainIngredient(String mainIngredient, List<String> ingredients) {
    return ingredients.contains(mainIngredient);
}

// 주재료 제외한 재료 일치율 계산
private int calculateAdditionalIngredientsMatch(List<String> recipeIngredients, List<String> userIngredients) {
    // 주재료 제외
    List<String> additionalRecipeIngredients = recipeIngredients.subList(1, recipeIngredients.size());
    
    int matchCount = 0;
    for (String ingredient : additionalRecipeIngredients) {
        if (userIngredients.contains(ingredient)) {
            matchCount++;
        }
    }
    
    // 일치율 계산 (소수점 반올림)
    return (int) Math.round((double) matchCount / (additionalRecipeIngredients.size()));
}
}