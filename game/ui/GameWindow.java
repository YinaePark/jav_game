package game.ui;

import javax.swing.*;

import core.CommandRegistry;

import java.awt.event.*;

import domain.Farm;
import domain.player.Player;
import game.entity.PlayerRenderer;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    private PlayerRenderer playerRenderer;
    private Player player;
    private Farm farm;
    private Timer animationTimer;
    private boolean[] keyState = new boolean[256];
    
    public GameWindow() {
        setTitle("Farming Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        playerRenderer = new PlayerRenderer(50, 50); // initial position
        farm = new Farm();
        CommandRegistry registry = new CommandRegistry(player, farm);
        registry.registerDefaults();
        gamePanel = new GamePanel(playerRenderer, player, farm, registry);

        animationTimer = new Timer(1000/60, e -> {
            playerRenderer.update();
            gamePanel.repaint();
        });
        animationTimer.start();
        
        // add key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                keyState[e.getKeyCode()] = true;
            }
            
            @Override
            public void keyReleased(KeyEvent e) {
                keyState[e.getKeyCode()] = false;
            }

        });

        Timer gameTimer = new Timer(16, e -> {  // 약 60FPS
            updatePlayerMovement();
            playerRenderer.update();
            gamePanel.repaint();
        });
        gameTimer.start();
        
        add(gamePanel);
        setFocusable(true);
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