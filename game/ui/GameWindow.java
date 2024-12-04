package game.ui;

import javax.swing.*;

import core.CommandRegistry;

import java.awt.*;
import java.awt.event.*;
import game.entity.Player;

public class GameWindow extends JFrame {
    private GamePanel gamePanel;
    private Player player;
    
    public GameWindow() {
        setTitle("Farming Game");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        player = new Player(50, 50); // initial position
        CommandRegistry registry = new CommandRegistry();
        registry.registerDefaults();
        gamePanel = new GamePanel(player, registry);
        
        // add key listener
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                switch(e.getKeyCode()) {
                    case KeyEvent.VK_LEFT:
                        player.move(-5, 0);
                        break;
                    case KeyEvent.VK_RIGHT:
                        player.move(5, 0);
                        break;
                    case KeyEvent.VK_UP:
                        player.move(0, -5);
                        break;
                    case KeyEvent.VK_DOWN:
                        player.move(0, 5);
                        break;
                }
                gamePanel.repaint();
            }
        });
        
        add(gamePanel);
        setFocusable(true);
    }
}