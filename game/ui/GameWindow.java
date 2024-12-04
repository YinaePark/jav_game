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
            playerRenderer.updateAnimation();
            gamePanel.repaint();
        });
        animationTimer.start();
        
        // add key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        playerRenderer.move(-7, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        playerRenderer.move(7, 0);
                        break;
                    case KeyEvent.VK_UP:
                        playerRenderer.move(0, -7);
                        break;
                    case KeyEvent.VK_DOWN:
                        playerRenderer.move(0, 7);
                        break;
                }
                gamePanel.repaint();
            }

            @Override
            public void keyReleased(KeyEvent e) {
                playerRenderer.move(0, 0); // stop moving
            }

        });
        
        add(gamePanel);
        setFocusable(true);
    }
}