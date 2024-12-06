package game.ui;

import javax.swing.*;
import java.awt.event.*;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import core.CommandRegistry;
import domain.Farm;
import domain.Player;
import game.entity.PlayerRenderer;
import game.entity.Customer;
import game.entity.NormalCustomer;
import game.recipe.Recipe;
import game.recipe.RecipeManager;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    private PlayerRenderer playerRenderer;
    private Player player;
    private Farm farm;
    private boolean[] keyState = new boolean[256];
    private InventoryPanel inventoryPanel;
    private ShopPanel shopPanel;
    private JPanel mainContainer;
    private JPanel rightPanel;
    private boolean isInventoryVisible = false;
    private boolean isShopVisible = false;

    private List<Customer> customers;
    private static final int CUSTOMER_SPAWN_Y = 600;
    private static final int CUSTOMER_TARGET_Y = 300;
    private Random random = new Random();

    private Timer customerSpawnTimer;
    private boolean isCustomerPresent = false;
    private int customerCount = 0;

    public GameWindow() {
        setTitle("Farming Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);

        // Initialize main container with BorderLayout
        mainContainer = new JPanel(new BorderLayout());
        customers = new ArrayList<>();
        player = new Player(20);
        playerRenderer = new PlayerRenderer(50, 50);
        farm = new Farm();

        // Command registry setup
        CommandRegistry registry = new CommandRegistry(player, farm);
        registry.registerDefaults();
        gamePanel = new GamePanel(playerRenderer, player, farm, registry, customers);
        gamePanel.setGameWindow(this);
        mainContainer.add(gamePanel, BorderLayout.CENTER);

        // Initialize inventory and shop panels
        inventoryPanel = new InventoryPanel(player, gamePanel);
        inventoryPanel.setPreferredSize(new Dimension(300, 600));
        inventoryPanel.setVisible(false);
        mainContainer.add(inventoryPanel, BorderLayout.EAST);
        shopPanel = new ShopPanel(player, gamePanel);  // ShopPanel 생성
        shopPanel.setPreferredSize(new Dimension(300, 600));
        shopPanel.setVisible(false);

        // Create a new right panel to hold both Inventory and Shop panels vertically
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(inventoryPanel);
        rightPanel.add(shopPanel);
        mainContainer.add(rightPanel, BorderLayout.EAST);
        setContentPane(mainContainer);

        // Set up timers and key listener
        setupTimers();
        setupKeyListener();
        setSize(800, 600);
        setLocationRelativeTo(null);
    }
    /**
     * Updates the inventory display if it is visible.
     */
    public void updateInventoryIfNeeded() {
        if (isInventoryVisible && inventoryPanel != null) {
            inventoryPanel.updateInventory(player.getInventory());
            inventoryPanel.repaint();
        }
    }

    /**
     * Toggles the visibility of the inventory panel and adjusts the window size accordingly.
     */
    private void toggleInventory() {
        isInventoryVisible = !isInventoryVisible;
        inventoryPanel.setVisible(isInventoryVisible);

        // Adjust window size based on inventory visibility
        if (isInventoryVisible) {
            setSize(1100, 600);  // Resize when inventory is shown
            updateInventoryIfNeeded(); // Update inventory display
        } else {
            setSize(800, 600);  // Reset size when inventory is hidden
        }
        // Hide shop if visible
        if (shopPanel.isVisible()) {
            shopPanel.setVisible(false);
        }
        gamePanel.requestFocus();
    }

    /**
     * Toggles the visibility of the shop panel and adjusts the window size accordingly.
     */
    private void toggleShop() {
        if (shopPanel != null) {
            isShopVisible = !isShopVisible;
            shopPanel.setVisible(isShopVisible);

            if (isShopVisible) {
                setSize(1100, 600);  // 샵이 보일 때 크기
            } else {
                setSize(800, 600);  // 샵이 숨겨질 때 크기
            }
            if (inventoryPanel.isVisible()) {
                inventoryPanel.setVisible(false);
            }
            mainContainer.revalidate();
            mainContainer.repaint();
        }
        gamePanel.requestFocus();
    }


    private void setupTimers() {
        Timer gameTimer = new Timer(16, e -> {
            updatePlayerMovement();
            playerRenderer.update();
            updateCustomers();
            gamePanel.repaint();
        });
        gameTimer.start();

        customerSpawnTimer = new Timer(10000, e -> {
            if (!isCustomerPresent && customerCount < 3) {
                spawnNewCustomer();
            }
        });
        customerSpawnTimer.start();
    }

    private void setupKeyListener() {
        keyState = new boolean[256];
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyState[e.getKeyCode()] = true;

                // I key
                if (e.getKeyCode() == KeyEvent.VK_I) {
                    toggleInventory();
                }
                // O key
                if (e.getKeyCode() == KeyEvent.VK_O) {
                    toggleShop();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                keyState[e.getKeyCode()] = false;
            }
        });

        // Set initial focus to game panel
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
    }

    private void updateCustomers() {
        for (int i=0; i< customers.size(); i++) {
            Customer customer = customers.get(i);
            customer.update();

            // If a customer has waited too long, remove them
            if (customer.isWaitingTooLong()){
                customers.remove(i);
                System.out.println("A customer has left due to waiting too long.");
                i--; // list index modify
                isCustomerPresent = false;
                startCustomerCooldown();
            }

            // If the customer completed their order
            if (customer.isOrderComplete()){
                customers.remove(i);
                customerCount++;
                isCustomerPresent = false;
                System.out.println("Order complete. Customer left.");
                i--;
                startCustomerCooldown();
            }
        }

        // Notify when 3 customers have been served
        if (customerCount>=3) {
            System.out.println("3 customers served.");

        }
    }
    public void removeCustomer(Customer customer) {
        customers.remove(customer);
        spawnNewCustomer();
        repaint();
    }

    private void startCustomerCooldown() {
        Timer cooldownTimer = new Timer (10000, e -> {
            if (!isCustomerPresent){
                spawnNewCustomer();
            }
        });
        cooldownTimer.setRepeats(false);
        cooldownTimer.start();
    }

    /**
     * Spawns a new customer at a random X position and moves them down to the target Y position.
     */
    private void spawnNewCustomer() {
        int spawnX = random.nextInt(180) + 70;
        NormalCustomer newCustomer = new NormalCustomer(spawnX, CUSTOMER_SPAWN_Y) {
            private double currentY = CUSTOMER_SPAWN_Y;
            private static final double MOVE_SPEED = 2.0;

            @Override
            public void update() {
                if (currentY > CUSTOMER_TARGET_Y) {
                    currentY -= MOVE_SPEED;
                    y = (int) currentY;

                    facing = Direction.UP;
                    isMoving = true;
                } else {
                    isMoving = false;
                    facing = Direction.DOWN;
                    y = CUSTOMER_TARGET_Y;
                }

                super.update();
                if (spawnTime == 0){
                    spawnTime = System.currentTimeMillis(); // 손님 등장 시간 기록
                }
            }
            @Override
            public boolean isWaitingTooLong() {
                return System.currentTimeMillis() - spawnTime > maxWaitingTime;
            }
            @Override
            public boolean isOrderComplete() {
                return false;
            }
        };

        RecipeManager recipeManager = RecipeManager.getInstance();
        List<Recipe> recipes = recipeManager.getRandomRecipes(3); // 랜덤으로 3개 선택
        newCustomer.assignRecipes(recipes);
        customers.add(newCustomer);
        isCustomerPresent = true;
        System.out.println("New customer added at position: " + spawnX + ", " + CUSTOMER_SPAWN_Y);  // 디버깅용
    }
    /**
     * Updates the player's movement based on key input.
     */
    private void updatePlayerMovement() {
        int dx = 0;
        int dy = 0;
        if (keyState[KeyEvent.VK_A]) dx -= 1;
        if (keyState[KeyEvent.VK_D]) dx += 1;
        if (keyState[KeyEvent.VK_W]) dy -= 1;
        if (keyState[KeyEvent.VK_S]) dy += 1;

        // Move the player if they're not already moving to a target
        if (!playerRenderer.isMovingToTarget() && (dx != 0 || dy != 0)) {
            playerRenderer.move(dx, dy);
        }
    }
}