package src.game;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.*;

public class GamePanel extends JPanel implements Runnable, KeyListener {
    public static final int WIDTH = 800;
    public static final int HEIGHT = 300;
    private final int GROUND_Y = 220;

    private Thread gameThread;
    private boolean running = false;
    private boolean gameOver = false;

    private Dinosaur dino;
    private java.util.List<Obstacle> obstacles = new ArrayList<>();
    private Random rand = new Random();

    private int score = 0;
    private long lastObstacleTime = 0;
    private long lastScoreTick = 0;
    private int groundOffset = 0;

    public GamePanel() {
        setPreferredSize(new Dimension(WIDTH, HEIGHT));
        setBackground(Color.WHITE);
        setFocusable(true);
        addKeyListener(this);

        initGame();
    }

    private void initGame() {
        dino = new Dinosaur(60, GROUND_Y - 48);
        obstacles.clear();
        lastObstacleTime = System.currentTimeMillis();
        lastScoreTick = System.currentTimeMillis();
        score = 0;
        gameOver = false;
    }

    public void startGame() {
        if (gameThread == null) {
            running = true;
            gameThread = new Thread(this, "GameThread");
            gameThread.start();
        }
        requestFocusInWindow();
    }

    @Override
    public void run() {
        final double TARGET_FPS = 60.0;
        final double nsPerFrame = 1000000000.0 / TARGET_FPS;
        long lastTime = System.nanoTime();
        double delta = 0;

        while (running) {
            long now = System.nanoTime();
            delta += (now - lastTime) / nsPerFrame;
            lastTime = now;

            while (delta >= 1) {
                updateGame();
                delta--;
            }
            repaint();

            try { Thread.sleep(2); } catch (InterruptedException e) { }
        }
    }

    private void updateGame() {
        if (!gameOver) {
            dino.update();

            // ground scroll
            groundOffset += 6 + score / 100;
            if (groundOffset > WIDTH) groundOffset = 0;

            // spawn obstacles
            int baseInterval = Math.max(700, 1500 - score);
            if (System.currentTimeMillis() - lastObstacleTime > baseInterval) {
                spawnObstacle();
                lastObstacleTime = System.currentTimeMillis();
            }

            // update obstacles
            Iterator<Obstacle> it = obstacles.iterator();
            while (it.hasNext()) {
                Obstacle o = it.next();
                o.update(6 + score / 100);
                if (o.getX() + o.getWidth() < 0) {
                    it.remove();
                } else if (o.getBounds().intersects(dino.getBounds())) {
                    gameOver = true;
                }
            }

            // scoring
            if (System.currentTimeMillis() - lastScoreTick >= 1000) {
                score++;
                lastScoreTick = System.currentTimeMillis();
            }
        }
    }

    private void spawnObstacle() {
        int w = 20 + rand.nextInt(16);
        int h = 20 + rand.nextInt(36);
        int x = WIDTH + 10;
        int y = GROUND_Y - h;
        obstacles.add(new Obstacle(x, y, w, h));
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();

        // sky
        g2.setColor(new Color(235, 235, 235));
        g2.fillRect(0, 0, WIDTH, HEIGHT);

        // ground
        g2.setColor(new Color(70, 70, 70));
        g2.fillRect(0, GROUND_Y, WIDTH, HEIGHT - GROUND_Y);

        // moving dashes
        g2.setColor(new Color(100, 100, 100));
        for (int x = -groundOffset % 40; x < WIDTH; x += 40) {
            g2.fillRect(x, GROUND_Y, 24, 6);
        }

        // draw dino
        dino.draw(g2);

        // draw obstacles
        for (Obstacle o : obstacles) o.draw(g2);

        // score
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("Monospaced", Font.BOLD, 16));
        g2.drawString(String.format("Score: %04d", score), WIDTH - 150, 30);

        if (gameOver) {
            g2.setFont(new Font("SansSerif", Font.BOLD, 36));
            String msg = "GAME OVER";
            int tw = g2.getFontMetrics().stringWidth(msg);
            g2.setColor(Color.RED.darker());
            g2.drawString(msg, (WIDTH - tw) / 2, HEIGHT / 2 - 10);

            g2.setFont(new Font("SansSerif", Font.PLAIN, 18));
            String retry = "Press R to restart";
            tw = g2.getFontMetrics().stringWidth(retry);
            g2.setColor(Color.BLACK);
            g2.drawString(retry, (WIDTH - tw) / 2, HEIGHT / 2 + 20);
        }

        g2.dispose();
    }

    // ---------- KeyListener ----------
    @Override public void keyTyped(KeyEvent e) { }

    @Override
    public void keyPressed(KeyEvent e) {
        int k = e.getKeyCode();
        if (k == KeyEvent.VK_SPACE || k == KeyEvent.VK_UP) {
            if (!gameOver) dino.jump();
        } else if (k == KeyEvent.VK_DOWN) {
            dino.duck(true);
        } else if (k == KeyEvent.VK_R) {
            initGame();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_DOWN) {
            dino.duck(false);
        }
    }
}
