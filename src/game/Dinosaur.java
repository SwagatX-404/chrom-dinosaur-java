package game;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Dinosaur {
    private int x, y;
    private int width = 44, height = 44;
    private int normalHeight = 44, duckHeight = 26;

    private double yVel = 0;
    private final double GRAVITY = 0.65;
    private boolean onGround = true;
    private boolean ducking = false;

    private BufferedImage sprite;

    public Dinosaur(int x, int y) {
        this.x = x;
        this.y = y;
        try {
            sprite = ImageIO.read(new File("assets/dino.png"));
        } catch (IOException e) {
            System.out.println("⚠️ Could not load dino.png, using fallback box");
        }
    }

    public void update() {
        if (!onGround) {
            yVel += GRAVITY;
            y += (int) yVel;
        }
        int groundY = 220;
        int currentHeight = ducking ? duckHeight : normalHeight;
        if (y + currentHeight >= groundY) {
            y = groundY - currentHeight;
            yVel = 0;
            onGround = true;
        }
    }

    public void jump() {
        if (onGround) {
            yVel = -12.5;
            onGround = false;
        }
    }

    public void duck(boolean wantDuck) {
        if (wantDuck && onGround) {
            if (!ducking) {
                ducking = true;
                y += (normalHeight - duckHeight);
            }
        } else if (!wantDuck && ducking) {
            y -= (normalHeight - duckHeight);
            ducking = false;
        }
    }

    public Rectangle getBounds() {
        int h = ducking ? duckHeight : normalHeight;
        return new Rectangle(x, y, width, h);
    }

    public void draw(Graphics2D g2) {
        int h = ducking ? duckHeight : normalHeight;

        if (sprite != null) {
            g2.drawImage(sprite, x, y, width, h, null);
        } else {
            // fallback box
            g2.setColor(Color.BLACK);
            g2.fillRect(x, y, width, h);
        }
    }
}
