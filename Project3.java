package Project3_6480279;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

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
    
    
        //PUN PART
    private int difficulty;
    private JButton	  startButton;
    private JComboBox	  selectDifficulty;
    private JRadioButton  [] types;
    private boolean haveStarted = false;
    private String [] comboString = {"Easy", "Medium", "Hard", "Very Hard", "Absurdly Impossible"};
	private ButtonGroup healthGroup;
    private JToggleButton [] setHealth;
    private int healthPower;
    
    public void startScreen(){
        //Start Button
        startButton = new JButton("START GAMES");
	startButton.addActionListener( new ActionListener() {
            @Override
            public void actionPerformed( ActionEvent e )
            {
                haveStarted = true;
            }
        });
        
        selectDifficulty = new JComboBox(comboString);
        selectDifficulty.setSelectedIndex(1);
        selectDifficulty.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                String item = "" + e.getItem();
                switch(item){
                    case "Easy":
                        difficulty = 1;
                        break;
                        
                    case "Medium":
                        difficulty = 2;
                        break;
                        
                    case "Hard":
                        difficulty = 3;
                        break;
                        
                    case "Very Hard":
                        difficulty = 4;
                        break;
                        
                    case "Absurdly Impossible":
                        difficulty = 5;
                        break;
                        
                    default:
                        difficulty = 2;
                        break;
                }
                
            }
        }); 
	    
	setHealth = new JToggleButton[5];
        healthGroup = new ButtonGroup();
        
        //Button 1
        setHealth[0] = new JRadioButton("1x Health"); 
        setHealth[0].setName("1x Health");
        setHealth[0].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == e.SELECTED){
                    healthPower = 0;
                }
            }
        });
        
        //Button 2
        setHealth[1] = new JRadioButton("2x Health"); 
        setHealth[1].setName("2x Health");
        setHealth[1].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == e.SELECTED){
                    healthPower = 1;
                }
            }
        });
        
        //Button 3
        setHealth[2] = new JRadioButton("4x Health"); 
        setHealth[2].setName("4x Health");
        setHealth[2].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == e.SELECTED){
                    healthPower = 2;
                }
            }
        });
        
        //Button 4
        setHealth[3] = new JRadioButton("8x Health"); 
        setHealth[3].setName("8x Health");
        setHealth[3].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == e.SELECTED){
                    healthPower = 3;
                }
            }
        });
        
        //Button 5
        setHealth[4] = new JRadioButton("16x Health"); 
        setHealth[4].setName("16x Health");
        setHealth[4].addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e) {
                if(e.getStateChange() == e.SELECTED){
                    healthPower = 4;
                }
            }
        });
        
        //Select button 1 first and add to healthGroup
        setHealth[0].setSelected(true);
        for (int i = 0; i<5; i++){
            healthGroup.add(setHealth[i]);
        }
        
        //wait for start button to be pressed    
        while(haveStarted == false){
            
        }
    }
    
    //String path = "src/main/java/Project3_6480279/resources/";

    public GamePanel() {
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));
        setBackground(Color.BLACK); //Default background for now, need change!
        setFocusable(true);
        addKeyListener(this);

        player = new Player(GAME_WIDTH / 2 - 60, GAME_HEIGHT - 150, 100, 100, PLAYER_SPEED, currentFrame);
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
	gamephysics = new GamePhysic();
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
            PlayerInput();
	    EnemyShoot();
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

    public Projectile(int x, int y, int width, int height, int speed, int damage) {
        super(x, y, width, height, 0 , damage);
        this.speed = speed;
        //Add some bool
    }

    public int getSpeed(){
        return speed;
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
        projectiles.removeIf(projectile -> {
           if(projectile.getSpeed() < 0 && playerBounds.intersects(projectile.getBounds())) { //Check collision between player and bullet. projectile.getspeed < 0 means the bullet
               player.MeLifeIs(player.WhatMeLife() - projectile.getDamage());
               return true;
           }
           return false;
        });
    }
    private void ProjectileCollisionEnemy(java.util.List<Enemy> enemies, java.util.List<Projectile> projectiles){
        projectiles.removeIf(projectile -> {
           if(projectile.getSpeed() > 0) {
               Rectangle bulletBounds = projectile.getBounds();
               for (Enemy enemy : enemies) {
                   if (bulletBounds.intersects(enemy.getBounds())) {
                       enemy.MeLifeIs(enemy.WhatMeLife() - projectile.getDamage());
                       if (enemy.WhatMeLife() <= 0) {
                           enemies.remove(enemy);
                       }
                       return true;
                   }
               }
           } 
           return false;
        });
    }
}
