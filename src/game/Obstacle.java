package src.game;

import java.awt.*;

public class Obstacle {
    private int x, y, width, height;

    public Obstacle(int x, int y, int w, int h) {
        this.x = x;
        this.y = y;
        this.width = w;
        this.height = h;
    }

    public void update(double speed) {
        x -= (int) Math.max(4, speed);
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }

    public void draw(Graphics2D g2) {
        g2.setColor(new Color(80, 80, 80));
        g2.fillRect(x, y, width, height);
    }

    // Getters
    public int getX() { return x; }
    public int getWidth() { return width; }
}
