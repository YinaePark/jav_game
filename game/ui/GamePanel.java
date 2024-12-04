package game.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import game.entity.Player;
import game.tile.FarmTile;
import command.*;
import core.CommandRegistry;

public class GamePanel extends JPanel {
    private Player player;
    private static final int TILE_SIZE = 40;
    private FarmTile[][] tiles;
    private CommandRegistry registry;
    private String selectedCrop = "tomato";

    private boolean isInteractable(int tileX, int tileY) {
        int playerTileX = player.getX() / TILE_SIZE;
        int playerTileY = player.getY() / TILE_SIZE;
        
        return Math.abs(tileX - playerTileX) <= 1 && 
               Math.abs(tileY - playerTileY) <= 1;
    }
    
    public GamePanel(Player player, CommandRegistry registry) {
        this.player = player;
        this.registry = registry;
        setBackground(Color.GREEN.darker());

        // initialize farm tiles(8x6)
        tiles = new FarmTile[8][6];
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                tiles[i][j] = new FarmTile();
            }
        }
        
        // register commands
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int tileX = e.getX() / TILE_SIZE;
                int tileY = e.getY() / TILE_SIZE;
                
                if (tileX < 8 && tileY < 6) {  // check if clicked inside the farm
                    FarmTile tile = tiles[tileX][tileY];
                    
                    if (isInteractable(tileX, tileY)) {
                        if (e.getButton() == MouseEvent.BUTTON1) {
                            new TillCommand(tile).execute(new String[]{});
                        } else if (e.getButton() == MouseEvent.BUTTON3) {
                            new PlantCommand(tile, selectedCrop).execute(new String[]{});
                        }
                        repaint();
                    } else {
                        System.out.println("Too far away!");
                    }
                }
            }
        });
    }
    
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        
        // draw grid
        for (int i = 0; i < 8; i++) {
            for (int j = 0; j < 6; j++) {
                int x = i * TILE_SIZE;
                int y = j * TILE_SIZE;
                FarmTile tile = tiles[i][j];
                
                // draw tile
                if (isInteractable(i, j)) {
                    g.setColor(new Color(24, 100, 24));
                } else {
                    g.setColor(new Color(34, 139, 34));
                }
                g.fillRect(x, y, TILE_SIZE, TILE_SIZE);

                g.setColor(new Color(101, 67, 33));
                g.drawRect(x, y, TILE_SIZE, TILE_SIZE);
                
                // if tilled, fill the tile with dark brown
                if (tile.isTilled()) {
                    g.setColor(new Color(139, 69, 19));
                    g.fillRect(x + 2, y + 2, TILE_SIZE - 4, TILE_SIZE - 4);
                }
                
                // if crop is planted, draw the crop
                if (tile.hasCrop()) {
                    if (isInteractable(i, j)) {
                        g.setColor(new Color(0, 100, 0));
                    } else {
                        g.setColor(new Color(0, 70, 0));   // dark green
                    }
                    g.fillOval(x + 10, y + 10, TILE_SIZE - 20, TILE_SIZE - 20);
                }
            }
        }
        
        // draw player
        player.draw(g);
    }
}