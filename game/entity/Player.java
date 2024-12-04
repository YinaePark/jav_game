package game.entity;

import java.awt.*;

public class Player {
    private int x, y;
    private static final int SIZE = 30;
    
    public Player(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return x;
    }
    
    public int getY() {
        return y;
    }
    
    public void move(int dx, int dy) {
        x += dx;
        y += dy;
        
        // process collision
        x = Math.max(0, Math.min(x, 800 - SIZE));
        y = Math.max(0, Math.min(y, 600 - SIZE));
    }
    
    public void draw(Graphics g) {
        g.setColor(Color.RED);
        g.fillRect(x, y, SIZE, SIZE);
    }
}