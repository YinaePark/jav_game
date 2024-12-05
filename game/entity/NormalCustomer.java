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
    private static final int MAX_WAITING_MINUTES = 30000;  // 5min
    private static final int BASE_REWARD = 100;         // base reward
    private static final double SATISFACTION_MULTIPLIER = 1.2; // satisfaction multiplier
    private Random random;
    public NormalCustomer(int x, int y) {
        super(x, y);
    }

    protected String loadRandomCustomerImage(){
        random = new Random();  // random 객체 초기화는 생성자에서
        String[] customerImagePaths = {
                "sprites/player/customer1.png",
                "sprites/player/customer2.png",
                "sprites/player/customer3.png",
                "sprites/player/customer4.png",
                "sprites/player/customer5.png",
        };
        String randomImagePath = customerImagePaths[random.nextInt(customerImagePaths.length)];
        return randomImagePath;

    }


    @Override
    protected void loadSprites() {
        try {
            random = new Random();  // random 객체 초기화는 생성자에서
            File file = new File(loadRandomCustomerImage());
//            File file = new File("sprites/player/normal_customer.png");
            if (file.exists()) {
                spriteSheet = ImageIO.read(file);
                int frameWidth = spriteSheet.getWidth() / 3;  // 3 columns
                int frameHeight = spriteSheet.getHeight() / 4; // 4 rows

                // initialize sprite arrays
                frontSprites = new BufferedImage[4];
                backSprites = new BufferedImage[4];
                sideSprites = new BufferedImage[4];

                // Load front animations
                for (int i = 0; i < 4; i++) {
                    frontSprites[i] = spriteSheet.getSubimage(0, i * frameHeight, frameWidth, frameHeight);
                }

                // Load back animations
                for (int i = 0; i < 4; i++) {
                    backSprites[i] = spriteSheet.getSubimage(frameWidth, i * frameHeight, frameWidth, frameHeight);
                }

                // Load side animations
                for (int i = 0; i < 4; i++) {
                    sideSprites[i] = spriteSheet.getSubimage(frameWidth * 2, i * frameHeight, frameWidth, frameHeight);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading normal customer sprite: " + e.getMessage());
            spriteSheet = null;
        }
    }

    @Override
    protected void initializeCustomer() {
        this.maxWaitingTime = MAX_WAITING_MINUTES;
        this.currentWaitingTime = 0;
        this.satisfactionLevel = 5;

        // RecipeManager에서 레시피 목록 가져오기
        RecipeManager recipeManager = RecipeManager.getInstance();
        List<String> availableMenus = new ArrayList<>(recipeManager.getAllRecipeNames());

        orderedMenus = new ArrayList<>();
        Random random = new Random();

        // 3개의 랜덤 메뉴 선택
        while (orderedMenus.size() < 3 && !availableMenus.isEmpty()) {
            String menu = availableMenus.get(random.nextInt(availableMenus.size()));
            if (!orderedMenus.contains(menu)) {
                orderedMenus.add(menu);
            }
        }
    }

    @Override
    public void updateSatisfaction(List<String> ingredients) {
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

        // 만족도 수준으로 변환 (0~100)
        satisfactionLevel = score;

    }

    @Override
    public int calculateReward() {
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
        double satisfactionScore = satisfactionLevel * 2 - 100;

        // 최종 보상 계산
        double reward = baseReward * (satisfactionScore / 100);

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