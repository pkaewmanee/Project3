package Project3_6480279;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class GameWindow extends JFrame {
    public GameWindow() {
        setTitle("Game Name");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize( 600, 800);
        setResizable(false); //Not allow user to resize 
        setLocationRelativeTo(null);

        GamePanel gamePanel = new GamePanel();
        add(gamePanel);
        gamePanel.start();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);
        });
    }
}

class GamePanel extends JPanel implements Runnable, KeyListener {
    private static final int GAME_WIDTH = 600;
    private static final int GAME_HEIGHT = 800;
    private static final int PLAYER_SPEED = 25;
    private boolean isRunning;
    private Thread gameThread;
    private Player player;
    private java.util.List<Enemy> enemies;
    private java.util.List<Projectile> projectiles;
    private long lastEnemySpawnTime;
    /*Boolean variable to keeps track if the button is pressed so that the character can do both action at the same time*/
    private boolean moveleft;
    private boolean moveright;
    private boolean moveup;
    private boolean movedown;
    private boolean shoot;
    /*Add cooldown between bullet*/
    private long lastShotTime;
    private static final int BulletCooldown = 40;
    private GameWindow currentFrame;
    
    //String path = "src/main/java/Project3_6480279/resources/";

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK); //Default background for now, need change!
        setFocusable(true);
        addKeyListener(this);

        player = new Player(GAME_WIDTH / 2 - 25, GAME_HEIGHT - 100, 100, 100, PLAYER_SPEED, currentFrame);
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
    }

    public void start() {
        isRunning = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    public void stop() {
        isRunning = false;
        try {
            gameThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        lastEnemySpawnTime = System.currentTimeMillis();

        while (isRunning) {
            update();
            repaint();
            try {
                Thread.sleep(16);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - lastEnemySpawnTime > 2000) {
                spawnEnemy();
                lastEnemySpawnTime = currentTime;
            }
        }
    }
    
    private void PlayerInput(){
        if (moveleft) {
            player.moveLeft();
        }
        else if (moveright) {
            player.moveRight();
        }
        else if (moveup) {
            player.moveUp();
        }
        else if (movedown) {
            player.moveDown();
        }
        else if (shoot) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > BulletCooldown) {
                player.shoot(projectiles);
                lastShotTime = currentTime;
            }
            
        }
    }
    
    private void spawnEnemy() {
        int x = (int) (Math.random() * (GAME_WIDTH - 50)); //ramdomly and within the frame
        int y = -50;
        Enemy enemy = new Enemy(x, y, 50, 50);
        enemies.add(enemy);
    }

    private void update() {
        player.update();
        for (Enemy enemy : enemies) {
            enemy.update();
        }
        for (Projectile projectile : projectiles) {
            projectile.update();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.draw(g);
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }
    }
    
    @Override
    public void keyPressed(KeyEvent e) {
        int keyCode = e.getKeyCode();
        
        switch (keyCode) {
            case KeyEvent.VK_LEFT:
                moveleft = true;
                break;
            case KeyEvent.VK_RIGHT:
                moveright = true;
                break;
            case KeyEvent.VK_UP:
                moveup = true;
                break;
            case KeyEvent.VK_DOWN:
                movedown = true;
                break;
            case KeyEvent.VK_Z:
                shoot = true;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        int keyCode = e.getKeyCode();
            switch (keyCode) {
            case KeyEvent.VK_LEFT:
                moveleft = false;
                break;
            case KeyEvent.VK_RIGHT:
                moveright = false;
                break;
            case KeyEvent.VK_UP:
                moveup = false;
                break;
            case KeyEvent.VK_DOWN:
                movedown = false;
                break;
            case KeyEvent.VK_Z:
                shoot = false;
                break;
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {}
    
    public static int getGameHeight(){
        return GAME_HEIGHT;
    }
    
    public static int getGameWidth(){
        return GAME_WIDTH;
    }
}

class Object extends JLabel{//parent class for all object in the game: player, enemy, projectile, etc.
    protected int x, y;
    protected int width, height;
    
    public Object(int x, int y, int width, int height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void update(){ }

    public void draw(Graphics g){}

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

class Player extends Object {
    private int speed;
    private MyImageIcon image;
    private GameWindow parentFrame;
    
    String imageFile = "src/main/java/Project3_6480279/resources/jet.png";

    public Player(int x, int y, int width, int height, int speed, GameWindow pf) {
        super(x, y, width, height);
        parentFrame = pf;
        this.speed = speed;
        image  = new MyImageIcon(imageFile).resize(width, height);
        setIcon(image);
    }

    public void moveUp() {
        y -= speed;
        if (y < 0) {
            y = 0;
        }
        update();
    }

    public void moveDown() {
        y += speed;
        int maxY = GamePanel.getGameHeight() - height;
        if (y > maxY) {
            y = maxY;
        }
        update();
    }

    public void moveLeft() {
        x -= speed;
        if (x < 0) {
            x = 0;
        }
        update();
    }

    public void moveRight() {
        x += speed;
        int maxX = GamePanel.getGameWidth() - width;
        if (x > maxX) {
            x = maxX;
        }
        update();
    }
    
    public void shoot(java.util.List<Projectile> projectile){
        int projectileWidth = 3;
        int projectileHeight = 7;
        int projectileSpeed = 10;
        int projectileX = x + width / 2 - projectileWidth / 2;
        int projectileY = y - projectileHeight;
        projectile.add(new Projectile(projectileX,projectileY,projectileWidth,projectileHeight,projectileSpeed));
    }

    @Override
    public void update() {
        // Update player's position, etc.
    }

    @Override
    public void draw(Graphics g) { //Player picture, default for now
        g.drawImage(image.getImage(), x, y, width, height, parentFrame);
    }
}

class Enemy extends Object {
    private int verticalspeed;
    private int horizontalspeed;
    private int updateCounter;
    private int updatesBeforeDirectionChange;

    public Enemy(int x, int y, int width, int height) {
        super(x, y, width, height);
        this.verticalspeed = 1;
        this.horizontalspeed = generateRandomHorizontalSpeed();
        this.updateCounter = 0;
        this.updatesBeforeDirectionChange = generateRandomUpdatesBeforeDirectionChange();
    }
    
    private int generateRandomHorizontalSpeed() {
        return new Random().nextInt(5) - 2;
    }
    
    private int generateRandomUpdatesBeforeDirectionChange() {
        return new Random().nextInt(71) + 30;
    }
    
    @Override
    public void update() {
        // Update enemy's position, behavior, etc.
        y += verticalspeed;
        x += horizontalspeed;
        
        if (x < 0) {
            x = 0;
            horizontalspeed = -horizontalspeed;
        } else if (x > GamePanel.getGameWidth() - width) {
            x = GamePanel.getGameWidth() - width;
            horizontalspeed = -horizontalspeed;
        }
        updateCounter++;
        if (updateCounter >= updatesBeforeDirectionChange) {
            horizontalspeed = generateRandomHorizontalSpeed();
            updateCounter = 0;
            updatesBeforeDirectionChange = generateRandomUpdatesBeforeDirectionChange();
        }
    }
    
    
    @Override
    public void draw(Graphics g) { //Enemy picture, default for now
        g.setColor(Color.RED);
        g.fillRect(x, y, width, height);
    }
}

class Projectile extends Object { //Implement projectile here, maybe consider powerboost to change projectile (maybe additional class)
    private int speed;

    public Projectile(int x, int y, int width, int height, int speed) {
        super(x, y, width, height);
        this.speed = speed;
        //Add some bool
    }

    @Override
    public void update() {
        // Update the projectile's position based on its speed and direction
        y -= speed;
        // Check if the projectile is out of bounds and remove it from the game if necessary
    }

    @Override
    public void draw(Graphics g) {
        //projectile picture
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }
}

class GamePhysic { //game physic will be in this class: projetile and enemy collision, enemy and player collision, etc.
    
}
