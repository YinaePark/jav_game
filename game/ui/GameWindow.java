package game.ui;

import javax.swing.*;

import core.CommandRegistry;

import java.awt.*;
import java.awt.event.*;
import game.entity.Player;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    private Player player;
    private Timer animationTimer;
    private boolean[] keyState = new boolean[256];
    
    public GameWindow() {
        setTitle("Farming Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        player = new Player(50, 50); // initial position
        CommandRegistry registry = new CommandRegistry();
        registry.registerDefaults();
        gamePanel = new GamePanel(player, registry);

        animationTimer = new Timer(1000/60, e -> {
            player.update();
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
            player.update();
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
        
        if (!player.isMovingToTarget() && (dx != 0 || dy != 0)) {
            player.move(dx, dy);
        }
    }
}