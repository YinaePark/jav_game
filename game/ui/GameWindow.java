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
import domain.player.Player;
import game.entity.PlayerRenderer;
import game.entity.Customer;
import game.entity.NormalCustomer;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    private PlayerRenderer playerRenderer;
    private Player player;
    private Farm farm;
    private boolean[] keyState = new boolean[256];
    private InventoryPanel inventoryPanel;
    private JPanel mainContainer;
    private boolean isInventoryVisible = false;
    
    private List<Customer> customers;
    private static final int CUSTOMER_SPAWN_Y = 600;
    private static final int CUSTOMER_TARGET_Y = 300;
    private Random random = new Random();
    
    public GameWindow() {
        setTitle("Farming Game");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false);
        mainContainer = new JPanel(new BorderLayout());
        
        customers = new ArrayList<>();
        
        player = new Player(20);
        playerRenderer = new PlayerRenderer(50, 50);
        farm = new Farm();
        CommandRegistry registry = new CommandRegistry(player, farm);
        registry.registerDefaults();
    
        gamePanel = new GamePanel(playerRenderer, player, farm, registry, customers);
        gamePanel.setGameWindow(this);
        mainContainer.add(gamePanel, BorderLayout.CENTER);
    
        inventoryPanel = new InventoryPanel(player, gamePanel);
        inventoryPanel.setPreferredSize(new Dimension(300, 600));  // 고정된 높이 설정
        inventoryPanel.setVisible(false);
        mainContainer.add(inventoryPanel, BorderLayout.EAST);
    
        setContentPane(mainContainer);
        
        setupTimers();
        setupKeyListener();
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public void updateInventoryIfNeeded() {
        if (isInventoryVisible && inventoryPanel != null) {
            inventoryPanel.updateInventory();
            inventoryPanel.repaint();
        }
    }
    
    private void toggleInventory() {
        isInventoryVisible = !isInventoryVisible;
        inventoryPanel.setVisible(isInventoryVisible);
        if (isInventoryVisible) {
            setSize(1100, 600);  // 인벤토리가 보일 때 크기
            inventoryPanel.updateInventory();
        } else {
            setSize(800, 600);  // 인벤토리가 숨겨질 때 크기
        }
        gamePanel.requestFocus();
    }

    private void setupTimers() {
        // 게임 타이머
        Timer gameTimer = new Timer(16, e -> {
            updatePlayerMovement();
            playerRenderer.update();
            updateCustomers();  // customers update 추가
            gamePanel.repaint();
        });
        gameTimer.start();

        // 고객 스폰 타이머
        Timer customerSpawnTimer = new Timer(3000, e -> spawnNewCustomer());
        customerSpawnTimer.start();
    }

    private void setupKeyListener() {
        // keyState 배열 초기화
        keyState = new boolean[256];
        
        gamePanel.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyState[e.getKeyCode()] = true;
                
                // I 키 처리
                if (e.getKeyCode() == KeyEvent.VK_I) {
                    toggleInventory();
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                keyState[e.getKeyCode()] = false;
            }
        });
    
        // gamePanel에 초기 포커스 설정
        gamePanel.setFocusable(true);
        gamePanel.requestFocus();
    }

    private void updateCustomers() {
        for (Customer customer : customers) {
            customer.update();
        }
    }

    private void spawnNewCustomer() {
        int spawnX = random.nextInt(600) + 100; // 100 ~ 700 사이 
        
        Customer newCustomer = new NormalCustomer(spawnX, CUSTOMER_SPAWN_Y) {
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
            }
        };
        
        customers.add(newCustomer);
        System.out.println("New customer added at position: " + spawnX + ", " + CUSTOMER_SPAWN_Y);  // 디버깅용
    }

    private void updatePlayerMovement() {
        int dx = 0;
        int dy = 0;
        
        if (keyState[KeyEvent.VK_A]) dx -= 1;
        if (keyState[KeyEvent.VK_D]) dx += 1;
        if (keyState[KeyEvent.VK_W]) dy -= 1;
        if (keyState[KeyEvent.VK_S]) dy += 1;
        
        if (!playerRenderer.isMovingToTarget() && (dx != 0 || dy != 0)) {
            playerRenderer.move(dx, dy);
        }
    }
}