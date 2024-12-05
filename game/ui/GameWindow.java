package game.ui;

import javax.swing.*;
import java.awt.event.*;
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
    
    private List<Customer> customers;
    private static final int CUSTOMER_SPAWN_Y = 600;
    private static final int CUSTOMER_TARGET_Y = 300;
    private Random random = new Random();
    
    public GameWindow() {
        setTitle("Farming Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        customers = new ArrayList<>();
        
        player = new Player(20);
        playerRenderer = new PlayerRenderer(50, 50);
        farm = new Farm();
        CommandRegistry registry = new CommandRegistry(player, farm);
        registry.registerDefaults();
        gamePanel = new GamePanel(playerRenderer, player, farm, registry, customers);

        setupTimers();
        setupKeyListener();
        
        add(gamePanel);
        setFocusable(true);
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
    }

    private void setupKeyListener() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyState[e.getKeyCode()] = true;
                
                // 스페이스바 이벤트를 별도로 처리
                if (e.getKeyCode() == KeyEvent.VK_SPACE) {
                    spawnNewCustomer();
                    System.out.println("Customer spawned!");  // 디버깅용
                }
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                keyState[e.getKeyCode()] = false;
            }
        });
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