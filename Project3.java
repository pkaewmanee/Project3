package Project3_6480279;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

class GameWindow extends JFrame {
    private StartPanel startPanel;
    private GamePanel gamePanel;
    
    public GameWindow() {
        setTitle("Game Name");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setResizable(false); //Not allow user to resize 
        setLocationRelativeTo(null);
        
        startPanel = new StartPanel(this);
        gamePanel = new GamePanel(this);
        add(startPanel);
    }
    
    public void startGame() {
        remove(startPanel);
        add(gamePanel);
        gamePanel.start();
        validate();
        repaint();
    }
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            GameWindow gameWindow = new GameWindow();
            gameWindow.setVisible(true);
        });
    }
}

class StartPanel extends JPanel {
    private GameWindow gameWindow;
    private JButton startButton;
    private JComboBox<String> selectDifficulty;
    private JRadioButton[] healthOptions;
    private JButton creditsButton;
    private JTextField playerNameField;
    
    public StartPanel(GameWindow gameWindow) {
        this.gameWindow = gameWindow;
        
        //Player Name Field
        playerNameField = new JTextField(15);
        
        // Start Button
        startButton = new JButton("START GAME");
        startButton.addActionListener(e -> gameWindow.startGame());
        
        // Difficulty level selection
        String[] comboString = {"Easy", "Medium", "Hard", "Extreme", "Mayhem"};
        selectDifficulty = new JComboBox<>(comboString);
        selectDifficulty.setSelectedIndex(1);
        
        // Health power selection
        ButtonGroup healthGroup = new ButtonGroup();
        healthOptions = new JRadioButton[5];
        for (int i = 0; i < healthOptions.length; i++) {
            healthOptions[i] = new JRadioButton(String.format("%dx Health", 1 << i));
            healthOptions[i].setSelected(i == 0);
            healthGroup.add(healthOptions[i]);
        }
        
        creditsButton = new JButton("CREDITS");
        creditsButton.addActionListener(new ActionListener (){
            @Override
            public void actionPerformed(ActionEvent e){
                JOptionPane.showMessageDialog(
                        null, 
                        """
                        Game developed by:
                        Supakorn Unjindamanee 6480279
                        Jawit Poopradit       6480087
                        Possathorn Sujipisut 6480274
                                                    """, 
                        "Credits",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });
        
        // Layout components
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(10, 10, 10, 10);
        
        //Add Player Name
        c.gridx = 0;
        c.gridy = 0;
        c.anchor = GridBagConstraints.WEST;
        add(new JLabel("Player Name: "), c);
        c.gridx = 1;
        c.anchor = GridBagConstraints.CENTER;
        add(playerNameField, c);
        
        //start button
        c.gridx = 0;
        c.gridy = 1;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.CENTER;
        add(startButton, c);
        
        //Select Difficulty
        c.gridx = 0;
        c.gridy = 2;
        c.gridwidth = 1;
        c.anchor = GridBagConstraints.WEST;
        add(new JLabel("Select Difficulty Level:"), c);
        c.gridx = 1;
        add(selectDifficulty, c);
        
        //Select Health Power
        c.gridx = 0;
        c.gridy = 3;
        add(new JLabel("Select Health Power:"), c);
        c.gridx = 1;
        JPanel healthPanel = new JPanel(new GridLayout(0, 1));
        for (JRadioButton option : healthOptions) {
            healthPanel.add(option);
        }
        add(healthPanel, c);
        
        //credits button
        c.gridx = 0;
        c.gridy = 4;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        add(creditsButton, c);
    }
}

class GamePanel extends JPanel implements Runnable, KeyListener {
    private static final int GAME_WIDTH = 600;
    private static final int GAME_HEIGHT = 800;
    private static final int PLAYER_SPEED = 7;
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
    private static final int EnemyShotCooldown = 1000;
    private long lastEnemyShotTime;
    private GamePhysic gamephysics;
    private GameWindow currentFrame;

    public GamePanel(GameWindow currentFrame) {
        this.currentFrame = currentFrame;
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK); // Default background for now, need change!
        setFocusable(true);
        addKeyListener(this);

        player = new Player(GAME_WIDTH / 2 - 60, GAME_HEIGHT - 150, 30, 30, PLAYER_SPEED, currentFrame);
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        gamephysics = new GamePhysic();
    }


    public void start() {
        requestFocus();
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
            PlayerInput();
	    EnemyShoot();
            update();
            projectiles.removeIf(Projectile::OutOfBoundBullet);
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
        if (moveright) {
            player.moveRight();
        }
        if (moveup) {
            player.moveUp();
        }
        if (movedown) {
            player.moveDown();
        }
        if (shoot) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastShotTime > BulletCooldown) {
                player.shoot(projectiles);
                lastShotTime = currentTime;
            }
        }
    }
	
    private void EnemyShoot() {
    long currentTime = System.currentTimeMillis();
        if (currentTime - lastEnemyShotTime > EnemyShotCooldown) {
            for (Enemy enemy : enemies) {
                enemy.shoot(projectiles);
            }
            lastEnemyShotTime = currentTime;
        }
    }
    
    private void spawnEnemy() {
        int x = (int) (Math.random() * (GAME_WIDTH - 50)); //ramdomly and within the frame
        int y = -50;
        Enemy enemy = new Enemy(x, y, 80, 90, currentFrame);
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
	gamephysics.GamePhysicUpdate(player, enemies, projectiles);
        
        if (player.WhatMeLife() <= 0) {  ///THIS IS FOR TESTING FOR WHEN PLAYER HP REACHES 0 IT WILL END PROGRAM, PLEASE CHANGE THIS LATER -Chev
            stop();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        player.update();
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
    protected int health;
    protected int damage;
    
    String path = "src/main/java/Project3_6480279/resources/";
    
    public Object(int x, int y, int width, int height, int health, int damage) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
	this.health = health;
        this.damage = damage;
    }

    public int WhatMeLife() {
        return health;
    }

    public void MeLifeIs(int health) {
        this.health = health;
    }

    public int getDamage() {
        return damage;
    }

    public void setDamage(int damage) {
        this.damage = damage;
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
    
    String playerImage = path + "jet.png";

    public Player(int x, int y, int width, int height, int speed, GameWindow pf) {
        super(x, y, width, height, 100, 10);
        parentFrame = pf;
        this.speed = speed;
        image  = new MyImageIcon(playerImage).resize(width, height);
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
        projectile.add(new Projectile(projectileX,projectileY,projectileWidth,projectileHeight,projectileSpeed, damage));
    }

    @Override
    public void update() {
        setLocation(x, y);
        // Update player's position, etc.
    }

    @Override
    public void draw(Graphics g) { //Player picture, default for now
        g.drawImage(image.getImage(), x, y, 50, 50, parentFrame);
    }
}

class Enemy extends Object {
    private int verticalspeed;
    private int horizontalspeed;
    private int updateCounter;
    private int updatesDirectionChange;
    
    private MyImageIcon image;
    private GameWindow parentFrame;
    
    String enemyImage = path + "enemy.png";

    public Enemy(int x, int y, int width, int height, GameWindow pf) {
        super(x, y, width, height, 100, 50);
        this.verticalspeed = 1;
        this.horizontalspeed = RandomHorizontalSpeed();
        this.updateCounter = 0;
        this.updatesDirectionChange = RandomDirectionChange();
        parentFrame = pf;
        image  = new MyImageIcon(enemyImage).resize(width, height);
        setIcon(image);
    }
    
    private int RandomHorizontalSpeed() {
        return new Random().nextInt(5) - 2;
    }
    
    private int RandomDirectionChange() {
        return new Random().nextInt(71) + 30;
    }
    
    public void shoot(java.util.List<Projectile> projectile){
        int projectileWidth = 3;
        int projectileHeight = 7;
        int projectileSpeed = -7;
        int projectileX = x + width / 2 - projectileWidth / 2;
        int projectileY = y + height;
        projectile.add(new Projectile(projectileX,projectileY,projectileWidth,projectileHeight,projectileSpeed, damage));
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
        if (updateCounter >= updatesDirectionChange) {
            horizontalspeed = RandomHorizontalSpeed();
            updateCounter = 0;
            updatesDirectionChange = RandomDirectionChange();
        }
    }
    
    
    @Override
    public void draw(Graphics g) { //Enemy picture, default for now
        g.drawImage(image.getImage(), x, y, width, height, parentFrame);
    }
}

class Projectile extends Object { //Implement projectile here, maybe consider powerboost to change projectile (maybe additional class)
    private int speed;
    private boolean OutOfBoundProjectile;

    public Projectile(int x, int y, int width, int height, int speed, int damage) {
        super(x, y, width, height, 0 , damage);
        this.speed = speed;
        this.OutOfBoundProjectile = false;
    }
    
    public boolean OutOfBoundBullet(){
        return OutOfBoundProjectile;
    }

    public int getSpeed(){
        return speed;
    }

    @Override
    public void update() {
        // Update the projectile's position based on its speed and direction
        y -= speed;
        // Check if the projectile is out of bounds and remove it from the game if necessary
        	if (y < 0 || y > GamePanel.getGameHeight()) {
            OutOfBoundProjectile = true;
        }
    }

    @Override
    public void draw(Graphics g) {
        //projectile picture
        g.setColor(Color.WHITE);
        g.fillRect(x, y, width, height);
    }
}

class GamePhysic { //game physic will be in this class: projetile and enemy collision, enemy and player collision, etc.
    public void GamePhysicUpdate(Player player, java.util.List<Enemy> enemies, java.util.List<Projectile> projectiles){
        PlayerToEnemyCollision(player, enemies);
        ProjectileCollisionPlayer(player,projectiles);
        ProjectileCollisionEnemy(enemies,projectiles);
    }
    private void PlayerToEnemyCollision(Player player, java.util.List<Enemy> enemies){ //This will check collision between player and enemy, if the player hitbox intersect with enemy hitbox the player will take damage
        Rectangle playerBounds = player.getBounds(); //Use rectangle to get the hitbox of player
        for (Enemy enemy : enemies) {
            if (playerBounds.intersects(enemy.getBounds())){ //This one detect if player rectangle intersects with enemy rectangle
                player.MeLifeIs(player.WhatMeLife() - 10); //If they hit each other it will decreasees player hp by 10
            }
        }
    }
    private void ProjectileCollisionPlayer(Player player, java.util.List<Projectile> projectiles){
        Rectangle playerBounds = player.getBounds();
        projectiles.removeIf(projectile -> { //this will make the bullet disappears once it hit a player
           if(projectile.getSpeed() < 0 && playerBounds.intersects(projectile.getBounds())) { //Check collision between player and bullet. projectile.getspeed() < 0 means the bullet is from enemy
               player.MeLifeIs(player.WhatMeLife() - projectile.getDamage()); //If player rectangle intersects with enemy bullet rectangle, it will decreases player hp depending on enemy damage
               return true; //remove projectile from list
           }
           return false;
        });
    }
    private void ProjectileCollisionEnemy(java.util.List<Enemy> enemies, java.util.List<Projectile> projectiles){
        projectiles.removeIf(projectile -> { //This will make the bullet disappear once it hit an enemy
           if(projectile.getSpeed() > 0) { //this will check if the bullet comes from player or not (player bullet speed is in positive)
               Rectangle bulletBounds = projectile.getBounds();
               for (Enemy enemy : enemies) {
                   if (bulletBounds.intersects(enemy.getBounds())) { //If projectile rectangle (from player) intersects with enemy rectangle, it will decrease enemy hp proportion to player damage
                       enemy.MeLifeIs(enemy.WhatMeLife() - projectile.getDamage()); //Decrease enemy hp
                       if (enemy.WhatMeLife() <= 0) { //If enemy hp is less than or equal to 0 it will delete the enemy
                           enemies.remove(enemy);
                       }
                       return true; //remove projectile from list
                   }
               }
           } 
           return false;
        });
    }
}
