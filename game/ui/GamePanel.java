package game.ui;

import command.*;
import core.CommandRegistry;
import domain.Farm;
import domain.item.Item;
import domain.player.Player;
import game.entity.Customer;
import game.entity.NormalCustomer;
import game.entity.PlayerRenderer;
import game.recipe.Recipe;
import game.recipe.RecipeManager;
import game.tile.FarmTile;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.ImageObserver;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import javax.imageio.ImageIO;
import javax.swing.*;


public class GamePanel extends JPanel {
    private GameWindow gameWindow;
    private CommandRegistry registry;
    private PlayerRenderer playerRenderer;
    private Player player;
    private Farm farm;
    private List<Customer> customers;
    private Item selectedItem;
    private InventoryPanel inventoryPanel;


    private static final int TILE_SIZE = 40;
    private FarmTile[][] tiles;
    private String selectedCrop = "tomato";

    private Image backgroundImage;

    private enum InputType {
        MOUSE_LEFT(MouseEvent.BUTTON1, "till"),
        MOUSE_RIGHT(MouseEvent.BUTTON3, "plant"),
        KEY_H(KeyEvent.VK_H, "help"),
        KEY_Q(KeyEvent.VK_Q, "quit"),
        KEY_F(KeyEvent.VK_F, "farm"),
        KEY_SPACE(KeyEvent.VK_SPACE, "harvest");

        private final int inputCode;
        private final String commandName;

        InputType(int inputCode, String commandName) {
            this.inputCode = inputCode;
            this.commandName = commandName;
        }

        public static InputType fromMouseButton(int button) {
            for (InputType type : values()) {
                if (type.inputCode == button && type.name().startsWith("MOUSE_")) {
                    return type;
                }
            }
            return null;
        }

        public static InputType fromKeyCode(int keyCode) {
            for (InputType type : values()) {
                if (type.inputCode == keyCode && type.name().startsWith("KEY_")) {
                    return type;
                }
            }
            return null;
        }

        public String getCommandName() {
            return commandName;
        }
    }

    private static class Position {
        final int x;
        final int y;

        private Position(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public static Position fromMouseEvent(MouseEvent e, int tileSize) {
            return new Position(e.getX() / tileSize, e.getY() / tileSize);
        }

        public boolean isValidFarmPosition() {
            return x < 8 && y < 6;
        }
    }

    private boolean isInteractable(int tileX, int tileY) {
        int playerCenterX = playerRenderer.getX() + (playerRenderer.getSize() / 2);
        int playerCenterY = playerRenderer.getY() + (playerRenderer.getSize() / 2);

        int playerTileX = playerCenterX / TILE_SIZE;
        int playerTileY = playerCenterY / TILE_SIZE;

        return Math.abs(tileX - playerTileX) <= 1 &&
               Math.abs(tileY - playerTileY) <= 1;
    }

    public GamePanel(PlayerRenderer playerRenderer, Player player, Farm farm, CommandRegistry registry, List<Customer> customers) {
        this.playerRenderer = playerRenderer;
        this.player = player;
        this.farm = farm;
        this.registry = registry;
        this.customers = customers;
        this.inventoryPanel = new InventoryPanel(player, this);


        initializeTiles();
        setupMouseListener();
        setupKeyListener();
        setFocusable(true);
        setBackground(Color.GREEN.darker());

        loadBackgroundImage();
    }

    private void loadBackgroundImage() {
        try {
            backgroundImage = ImageIO.read(new File("sprites/background/background2.png"));
        } catch (IOException e) {
            e.printStackTrace();
            backgroundImage = null;
        }

    }

    public void setSelectedItem(Item item) {
        this.selectedItem = item;
        repaint();
    }

    public InventoryPanel getInventoryPanel() {
        return inventoryPanel;
    }
    public void setGameWindow(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
    }

    public void updateInventoryIfVisible() {
        if (gameWindow != null) {
            gameWindow.updateInventoryIfNeeded();
        }
    }

    private void initializeTiles() {
        tiles = new FarmTile[8][6];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                tiles[i][j] = new FarmTile();
            }
        }
    }

    private void setupMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                handleMouseClick(e);
                handleCustomerClick(e);

            }
        });
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
        public void keyPressed(KeyEvent e) {
            InputType inputType = InputType.fromKeyCode(e.getKeyCode());
            if (inputType != null) {
                Command command;
                if (inputType == InputType.KEY_SPACE) {
                    command = new HarvestCommand(player, farm, tiles, playerRenderer, GamePanel.this);
                } else {
                    command = registry.getCommand(inputType.getCommandName());
                }

                if (command != null) {
                    command.execute(new String[]{});
                    repaint();
                }
            }
        }
        });
    }

    private void handleMouseClick(MouseEvent e) {
        Position clickPosition = Position.fromMouseEvent(e, TILE_SIZE);

        if (!clickPosition.isValidFarmPosition()) {
            return;
        }

        InputType inputType = InputType.fromMouseButton(e.getButton());
        if (inputType != null) {
            FarmTile tile = tiles[clickPosition.x][clickPosition.y];

            if (!isInteractable(clickPosition.x, clickPosition.y)) {
                System.out.println("Too far away!");
                return;
            }

            Command command = createMouseCommand(inputType, tile);
            if (command != null) {
                command.execute(new String[]{});
                repaint();
            }
        }
    }

    private Command createMouseCommand(InputType inputType, FarmTile tile) {
        switch (inputType) {
            case MOUSE_LEFT:
                return new TillCommand(tile);
            case MOUSE_RIGHT:
                if (selectedItem != null) {
                    return new PlantCommand(player, farm, tile, selectedItem.getName(), this);
                } else {
                    JOptionPane.showMessageDialog(this, "Please select a valid crop to plant.", "No Crop Selected", JOptionPane.WARNING_MESSAGE);
                    return null;
                }
            default:
                return null;
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        if (backgroundImage != null){
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), (ImageObserver) null);
        } else {
            g.setColor(Color.GREEN.darker());
            g.fillRect(0,0, getWidth(), getHeight());
        }
        // draw grid
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                int x = i * TILE_SIZE;
                int y = j * TILE_SIZE;
                FarmTile tile = tiles[i][j];

                // draw tile
                if (isInteractable(i, j)) {
                    g.setColor(new Color(120,120, 100, 150));
                } else {
                    g.setColor(new Color(80, 80, 80, 150));
                }
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                g.setColor(new Color(101, 67, 33, 150));
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);

                // if tilled, fill the tile with dark brown
                if (tile.isTilled()) {
                    g.setColor(new Color(139, 69, 19));
                    g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                }

                // 작물이 심어져 있다면 성장 상태에 따라 그리기
                if (tile.hasCrop()) {
                    int growthProgress = Math.min(100, Math.max(0, tile.getGrowthProgress()));
                    int size = calculateCropSize(growthProgress);
                    int alpha = Math.min(255, Math.max(0, calculateCropAlpha(growthProgress)));

                    // 작물 이미지 그리기
                    Image cropSprite = tile.getCrop().getSprite(size, size);

                    // 작물 크기 중앙 정렬
                    int cropX = x + (TILE_SIZE - size) / 2;
                    int cropY = y + (TILE_SIZE - size) / 2;

                    // 알파값 적용을 위한 AlphaComposite 설정
                    Graphics2D g2d = (Graphics2D) g;
                    AlphaComposite alphaComposite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alpha/255.0f);
                    g2d.setComposite(alphaComposite);

                    // 작물 그리기
                    g2d.drawImage(cropSprite, cropX, cropY, null);

                    // 알파값 원래대로 복구
                    g2d.setComposite(AlphaComposite.SrcOver);

                    drawProgressBar(g, x, y, TILE_SIZE, growthProgress);
                }
            }
        }

        // draw customers
        if (customers != null) {
            for (Customer customer : customers) {
                customer.draw(g);
            }
        }

        // draw player
        playerRenderer.draw(g);

        // draw selected item
        if (selectedItem != null) {
            int x = 10;  // 왼쪽 여백
            int y = getHeight() - 60; // 아래 여백
            int size = 40;

            Image itemSprite = selectedItem.getSprite(size, size);
            g.drawImage(itemSprite, x, y, null);
            g.setColor(Color.BLACK);
            g.drawString(selectedItem.getName(), x, y + size + 15);
        }

        // Draw money display
        g.setColor(new Color(0, 0, 0, 180));
        g.setFont(new Font("Arial", Font.BOLD, 16));
        String moneyText = String.format("€ %.2f", player.getMoney());
        FontMetrics metrics = g.getFontMetrics();
        int moneyWidth = metrics.stringWidth(moneyText);
        int moneyX = getWidth() - moneyWidth - 20; // 20 pixels from right edge
        int moneyY = 30; // 30 pixels from top

        // Draw background for better visibility
        g.setColor(new Color(255, 255, 255, 180));
        g.fillRect(moneyX - 5, moneyY - metrics.getAscent(), moneyWidth + 10, metrics.getHeight());

        // Draw money text
        g.setColor(new Color(0, 100, 0)); // Dark green color for money
        g.drawString(moneyText, moneyX, moneyY);

        // customer time
        for (Customer customer : customers) {
            customer.draw(g);
        }
    }

    private int calculateCropSize(int growthProgress) {
        int minSize = 10;
        int maxSize = TILE_SIZE - 10;
        return minSize + ((maxSize - minSize) * growthProgress / 100);
    }

    private int calculateCropAlpha(int growthProgress) {
        int minAlpha = 128;  // 50% 투명도
        int maxAlpha = 255;  // 완전 불투명
        return minAlpha + ((maxAlpha - minAlpha) * growthProgress / 100);
    }

    private void drawProgressBar(Graphics g, int x, int y, int tileSize, int progress) {
        int barHeight = 4;  // 막대바 높이
        int barWidth = tileSize - 6;  // 막대바 너비
        int barX = x + 3;  // 막대바 X 위치
        int barY = y + tileSize - barHeight - 3;  // 막대바 Y 위치

        // 막대바 배경 (회색)
        g.setColor(new Color(100, 100, 100, 180));
        g.fillRect(barX, barY, barWidth, barHeight);

        // 진행도 막대 (초록색)
        int progressWidth = (int)((barWidth * progress) / 100.0);
        g.setColor(new Color(50, 205, 50, 230));
        g.fillRect(barX, barY, progressWidth, barHeight);

        // 막대바 테두리
        g.setColor(new Color(0, 0, 0, 180));
        g.drawRect(barX, barY, barWidth, barHeight);
    }


    private void handleCustomerClick(MouseEvent e) {
        // 1. 클릭된 위치의 고객 찾기
        Customer customer = findCustomerAtPosition(e.getX(), e.getY());
        if (customer == null) {
            return; // 고객이 없다면 처리 종료
        }
        // 2. 요리 선택 다이얼로그 표시
        showDishSelectionDialog(customer);
    }

    private Customer findCustomerAtPosition(int x, int y) {
        for (Customer customer : customers) {
            if (customer.contains(x, y)) {
                return customer;
            }
        }
        return null; // 클릭된 위치에 고객이 없을 경우
    }

    private void showDishSelectionDialog(Customer customer) {
        List<Recipe> customerRecipes = customer.getAssignedRecipes();

        // ChoiceDishPanel 생성 
        JPanel panel = new JPanel(new BorderLayout());
        ChoiceDishPanel choiceDishPanel = new ChoiceDishPanel(customerRecipes, selectedDish -> {
            serveDishToCustomer(selectedDish, customer);
            ((JDialog) SwingUtilities.getWindowAncestor(panel)).dispose(); // 다이얼로그 닫기
        });

        // 다이얼로그 구성
        panel.add(choiceDishPanel, BorderLayout.CENTER);
        JDialog dialog = new JDialog();
        dialog.setTitle("Select a Dish for Customer");
        dialog.setModal(true);
        dialog.add(panel);
        dialog.pack();
        dialog.setLocationRelativeTo(this);
        dialog.setVisible(true);
    }


    private void serveDishToCustomer(String selectedDish, Customer customer) {
        // 선택한 레시피에 대해 재료 선택 다이얼로그 띄우기
        RecipeManager recipeManager = RecipeManager.getInstance();
        Recipe selectedRecipe = recipeManager.getRecipe(selectedDish);
        if (selectedRecipe == null) {
            // 해당 이름에 맞는 레시피가 없을 경우 처리
            System.out.println("Error: Recipe not found for " + selectedDish);
            return;
        }
        showIngredientSelectionDialog(selectedRecipe, customer);
    }

    private void showIngredientSelectionDialog(Recipe selectedDish, Customer customer) {
        // IngredientSelectionDialog 생성 (Player와 연결)
        IngredientSelectionPanel ingredientSelectionPanel = new IngredientSelectionPanel(gameWindow, player, (NormalCustomer) customer, this, gameWindow);

        // 다이얼로그 표시
        ingredientSelectionPanel.setVisible(true);


    }


}