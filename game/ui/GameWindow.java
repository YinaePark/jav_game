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
    private ShopPanel shopPanel;
    private JPanel mainContainer;
    private JPanel rightPanel;  // 오른쪽에 두 패널을 배치할 새로운 JPanel
    private boolean isInventoryVisible = false;
    private boolean isShopVisible = false;

    private List<Customer> customers;
    private static final int CUSTOMER_SPAWN_Y = 600;
    private static final int CUSTOMER_TARGET_Y = 300;
    private Random random = new Random();

    private Timer customerSpawnTimer; //고객 스폰 타이머
    private boolean isCustomerPresent = false; //현재 고객이 있는지 여부
    private int customerCount = 0;

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

        shopPanel = new ShopPanel(player, gamePanel);  // ShopPanel 생성
        shopPanel.setPreferredSize(new Dimension(300, 600));  // 샵 크기 설정
        shopPanel.setVisible(false);
// 오른쪽 패널을 생성하고, BoxLayout을 사용하여 두 패널을 세로로 배치
        rightPanel = new JPanel();
        rightPanel.setLayout(new BoxLayout(rightPanel, BoxLayout.Y_AXIS));
        rightPanel.add(inventoryPanel);
        rightPanel.add(shopPanel);


        mainContainer.add(rightPanel, BorderLayout.EAST);  // 샵 패널 추가

        setContentPane(mainContainer);
        
        setupTimers();
        setupKeyListener();
        
        setSize(800, 600);
        setLocationRelativeTo(null);
    }

    public void updateInventoryIfNeeded() {
        if (isInventoryVisible && inventoryPanel != null) {
            inventoryPanel.updateInventory(player.getInventory());
            inventoryPanel.repaint();
        }
    }
    
    private void toggleInventory() {
        isInventoryVisible = !isInventoryVisible;
        inventoryPanel.setVisible(isInventoryVisible);
        if (isInventoryVisible) {
            setSize(1100, 600);  // 인벤토리가 보일 때 크기
            updateInventoryIfNeeded();  // 인벤토리 상태 업데이트
        } else {
            setSize(800, 600);  // 인벤토리가 숨겨질 때 크기
        }
        // shopPanel이 보이고 있다면 이를 숨깁니다.
        if (shopPanel.isVisible()) {
            shopPanel.setVisible(false);
        }
        gamePanel.requestFocus();
    }
    private void toggleShop() {
        // ShopPanel 토글
        if (shopPanel != null) {
            isShopVisible = !isShopVisible;
            shopPanel.setVisible(isShopVisible);  // 패널의 가시성을 변경

            // 창 크기 조정
            if (isShopVisible) {
                setSize(1100, 600);  // 샵이 보일 때 크기
            } else {
                setSize(800, 600);  // 샵이 숨겨질 때 크기
            }
            // inventoryPanel이 보이고 있다면 이를 숨깁니다.
            if (inventoryPanel.isVisible()) {
                inventoryPanel.setVisible(false);
            }
            // 레이아웃 갱신 및 패널 그리기
            mainContainer.revalidate();
            mainContainer.repaint();
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
        customerSpawnTimer = new Timer(10000, e -> {
            if (!isCustomerPresent && customerCount < 3) {
                spawnNewCustomer();
            }
        });
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
                // O 키 처리
                if (e.getKeyCode() == KeyEvent.VK_O) {
                    toggleShop();  // O 키로 샵 토글
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
        for (int i=0; i< customers.size(); i++) {
            Customer customer = customers.get(i);
            customer.update();

            //고객이 maxWaitingTime을 초과하면 돌아간다.
            if (customer.isWaitingTooLong()){
                customers.remove(i);
                System.out.println("A customer has left due to waiting too long.");
                i--; // list index modify
            }

            //고객이 주문을 완료했을 때
            if (customer.isOrderComplete()){
                customers.remove(i);
                customerCount++;
                isCustomerPresent = false;
                System.out.println("Order complete. Customer left.");
                i--;
            }
        }

        // 게임 안내: 고객 수가 3명이 되면 안내
        if (customerCount>=3) {
            System.out.println("3 customers served.");

        }
    }

    private void spawnNewCustomer() {
        int spawnX = random.nextInt(200) + 100; // 100 ~ 300 사이
        
        Customer newCustomer = new NormalCustomer(spawnX, CUSTOMER_SPAWN_Y) {
            private double currentY = CUSTOMER_SPAWN_Y;
            private static final double MOVE_SPEED = 2.0;
            private long spawnTime;

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
            // 손님 너무 오래 기다리면 돌아감
            @Override
            public boolean isWaitingTooLong() {
                return System.currentTimeMillis() - spawnTime > maxWaitingTime;
            }

            @Override
            public boolean isOrderComplete() {
                // 주문 완료 시 true 반환 (주문 완료 조건 구현)
                return false;
            }
        };
        
        customers.add(newCustomer);
        isCustomerPresent = true;
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