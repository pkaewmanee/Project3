/*
Supakorn Unjindamanee 6480279
Jawit Poopradit      6480087
Phakkhapon Kaewmanee 6480929
Possathorn Sujipisut 6480274
 */
package Project3_6480279;

import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import javax.imageio.ImageIO;
import java.io.IOException;

class MainApplication extends JFrame {

    private StartPanel startPanel;
    private GamePanel gamePanel;
    private MySoundEffect themeSound;

    String path = "src/main/java/Project3_6480279/resources/";

    public MainApplication() {
        setTitle("Journey To The Stars");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(600, 800);
        setResizable(false); //Not allow user to resize 
        setLocationRelativeTo(null);

        themeSound = new MySoundEffect(path + "theme.wav");
        themeSound.playLoop();
        themeSound.setVolume(0.4f);
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

    public void updateDifficulty(int difficulty) {
        gamePanel.updateDifficulty(difficulty);
    }

    public void updateHealthPower(int power) {
        gamePanel.updateHealthPower(power);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MainApplication gameWindow = new MainApplication();
            gameWindow.setVisible(true);
        });
    }
}

class StartPanel extends JPanel {

    private MainApplication gameWindow;
    private JButton startButton;
    private JComboBox<String> selectDifficulty;
    private JRadioButton[] healthOptions;
    private JButton creditsButton;
    private JButton howToPlayButton;
    private JTextField playerNameField;
    private ButtonGroup healthGroup;

    private Image backgroundImage;

    public StartPanel(MainApplication gameWindow) {
        this.gameWindow = gameWindow;

        try {
            backgroundImage = ImageIO.read(new File("src/main/java/Project3_6480279/resources/background.png"));
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Player Name Field
        playerNameField = new JTextField(15);
        playerNameField.setBackground(Color.LIGHT_GRAY);

        // Start Button
        startButton = new JButton("START GAME");
        startButton.setBackground(Color.LIGHT_GRAY);
        startButton.addActionListener(e -> gameWindow.startGame());

        // Difficulty level selection
        String[] comboString = {"Easy", "Medium", "Hard", "Extreme", "Mayhem"};
        selectDifficulty = new JComboBox<>(comboString);
        selectDifficulty.setBackground(Color.LIGHT_GRAY);
        selectDifficulty.setSelectedIndex(1);
        selectDifficulty.addActionListener(e -> gameWindow.updateDifficulty(getDifficulty()));

        // Health power selection
        healthGroup = new ButtonGroup();
        healthOptions = new JRadioButton[5];
        for (int i = 0; i < healthOptions.length; i++) {
            healthOptions[i] = new JRadioButton(String.format("%dx Health", 1 << i));
            healthOptions[i].setSelected(i == 0);
            healthOptions[i].addActionListener(e -> gameWindow.updateHealthPower(getHealthPower()));
            healthGroup.add(healthOptions[i]);
        }

        creditsButton = new JButton("CREDITS");
        creditsButton.setBackground(Color.LIGHT_GRAY);
        creditsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        null,
                        """
                    Game developed by:
                    Supakorn Unjindamanee 6480279
                    Jawit Poopradit      6480087
                    Phakkhapon Kaewmanee 6480929
                    Possathorn Sujipisut 6480274
                                                """,
                        "Credits",
                        JOptionPane.INFORMATION_MESSAGE
                );
            }
        });

        howToPlayButton = new JButton("HOW TO PLAY");
        howToPlayButton.setBackground(Color.LIGHT_GRAY);
        howToPlayButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(
                        null,
                        """
                        Make Sure to set the desired difficulty and enemy health level before start the game.
                        The difficulty selection will be a drop-down box.
                        The enemy health will be an option to pick.
                        
                        How To Play The Game:
                        Use arrow keys to move your character. You can go anywhere within the frame.
                        Use z key to shoot. Hold down for rapid firing or press to shoot from time to time
                        
                        The game will run forever until you die. 
                        Your score will be pop-up after after GAME OVER!
                        
                        ENJOY THE GAME!
                        """,
                        "How to play",
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
        JLabel playerNameLabel = new JLabel("Player Name: ");
        playerNameLabel.setForeground(Color.WHITE); // set the text color to red
        add(playerNameLabel, c);
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
        JLabel difficultyQuestion = new JLabel("Select Difficulty Level:");
        difficultyQuestion.setForeground(Color.WHITE);
        add(difficultyQuestion, c);
        c.gridx = 1;
        add(selectDifficulty, c);

        //Select Health Power
        c.gridx = 0;
        c.gridy = 3;
        JLabel healthSelection = new JLabel("Select Health Power:");
        healthSelection.setForeground(Color.WHITE);
        add(healthSelection, c);
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

        c.gridx = 0;
        c.gridy = 5;
        c.gridwidth = 2;
        c.anchor = GridBagConstraints.EAST;
        add(howToPlayButton, c);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Draw the background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), null);
        }
    }

    public void setPlayerName(String name) {
        playerNameField.setText(name);
    }

    public String getPlayerName() {
        return playerNameField.getText();
    }

    public void setDifficulty(String difficulty) {
        selectDifficulty.setSelectedItem(difficulty);
    }

    public int getDifficulty() {
        String x = (String) selectDifficulty.getSelectedItem();
        switch (x) {
            case "Easy":
                return 1;
            case "Medium":
                return 2;
            case "Hard":
                return 3;
            case "Extreme":
                return 4;
            case "Mayhem":
                return 5;
            default:
                return 0;
        }
    }

    public void setHealthPower(int power) {
        for (int i = 0; i < healthOptions.length; i++) {
            if ((1 << i) == power) {
                healthOptions[i].setSelected(true);
                break;
            }
        }
    }

    public int getHealthPower() {
        int healthPower = 0;
        Enumeration<AbstractButton> buttons = healthGroup.getElements();
        while (buttons.hasMoreElements()) {
            JRadioButton button = (JRadioButton) buttons.nextElement();
            if (button.isSelected()) {
                String buttonText = button.getText();
                healthPower = Integer.parseInt(buttonText.substring(0, buttonText.indexOf("x")).trim());
                break;
            }
        }
        return healthPower;
    }

}

class GamePanel extends JPanel implements Runnable, KeyListener, MouseListener {

    private StartPanel startPanel;
    private static final int GAME_WIDTH = 600;
    private static final int GAME_HEIGHT = 800;
    private static final int PLAYER_SPEED = 7;
    private boolean isRunning;
    private Thread gameThread;
    private Player player;
    private java.util.List<Enemy> enemies;
    private java.util.List<Projectile> projectiles;
    private long lastEnemySpawnTime;
    private int healthMultiplier;
    private int damageMultiplier;
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
    private MainApplication currentFrame;

    private Image background;

    public void updateDifficulty(int difficulty) {
        this.damageMultiplier = difficulty;
    }

    public void updateHealthPower(int power) {
        this.healthMultiplier = power;
    }

    public GamePanel(MainApplication currentFrame) {
        this.startPanel = new StartPanel(currentFrame);
        this.currentFrame = currentFrame;
        healthMultiplier = startPanel.getHealthPower();
        damageMultiplier = startPanel.getDifficulty();
        System.out.print(healthMultiplier + " " + damageMultiplier);
        setPreferredSize(new Dimension(GAME_WIDTH, GAME_HEIGHT));

        setLayout(null);
        try {
            background = ImageIO.read(new File("src/main/java/Project3_6480279/resources/background.png"));
        } catch (IOException e) {
            System.err.println(e);
        }

        player = new Player(GAME_WIDTH / 2 - 60, GAME_HEIGHT - 150, 30, 30, PLAYER_SPEED, currentFrame);
        enemies = new ArrayList<>();
        projectiles = new ArrayList<>();
        gamephysics = new GamePhysic();

        setFocusable(true);
        addKeyListener(this);
        addMouseListener(this);
    }

    public void VictoryRoyal(Graphics g) {
        String End = "Game Over";
        String TotalScore = "Total Score: " + player.getScore();
        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.PLAIN, 40));
        g.drawString(End, getWidth() / 2 - 100, getHeight() / 2 - 50);
        g.drawString(TotalScore, getWidth() / 2 - 100, getHeight() / 2);
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

    private void PlayerInput() {
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
        //Enemy enemy = new Enemy(x, y, 80, 90, 200, 30,currentFrame, healthMultiplier);
        Enemy enemy = new Enemy(x, y, 80, 90, 100, 15, currentFrame, healthMultiplier, damageMultiplier);
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

        if (gamephysics.IsGameOver()) { //Once the game is over, it will clear all the existing enemies
            enemies.clear();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(background, 0, 0, getWidth(), getHeight(), null);
        player.update();
        player.draw(g);
        if (player.WhatMeLife() <= 0) {
            VictoryRoyal(g);
            return;
        }
        for (Enemy enemy : enemies) {
            enemy.draw(g);
        }
        for (Projectile projectile : projectiles) {
            projectile.draw(g);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            shoot = true;
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            shoot = false;
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {

    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
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
            /*case KeyEvent.VK_Z:
                shoot = true;
                break;*/
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
            /*case KeyEvent.VK_Z:
                shoot = false;
                break;*/
            default:
                break;
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    public static int getGameHeight() {
        return GAME_HEIGHT;
    }

    public static int getGameWidth() {
        return GAME_WIDTH;
    }
}

class Object extends JLabel {//parent class for all object in the game: player, enemy, projectile, etc.

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

    public void update() {
    }

    public void draw(Graphics g) {
    }

    public Rectangle getBounds() {
        return new Rectangle(x, y, width, height);
    }
}

class Player extends Object {

    private int speed;
    private MyImageIcon image;
    private MainApplication parentFrame;
    private int score = 0;

    String playerImage = path + "jet.png";

    private String projectileImage = path + "projectile_p.png";

    public Player(int x, int y, int width, int height, int speed, MainApplication pf) {
        super(x, y, width, height, 200, 25);
        parentFrame = pf;
        this.speed = speed;
        image = new MyImageIcon(playerImage).resize(width, height);
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

    public void shoot(java.util.List<Projectile> projectile) {
        int projectileWidth = 10;
        int projectileHeight = 15;
        int projectileSpeed = 10;
        int projectileX = x + width / 2 - projectileWidth / 2;
        int projectileY = y - projectileHeight;
        projectile.add(new Projectile(projectileX, projectileY, projectileWidth, projectileHeight, projectileSpeed, damage, projectileImage, parentFrame));
    }

    public void IncreaseScore(int points) {
        score += points;
    }

    public int getScore() {
        return score;
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
    private MainApplication parentFrame;

    String enemyImage = path + "enemy.png";

    private String projectileImage = path + "projectile_e.png";

    public Enemy(int x, int y, int width, int height, int health, int damage, MainApplication pf, int healthMultiplier, int damageMultiplier) {
        super(x, y, width, height, health * healthMultiplier, damage * damageMultiplier);
        this.verticalspeed = 1;
        this.horizontalspeed = RandomHorizontalSpeed();
        this.updateCounter = 0;
        this.updatesDirectionChange = RandomDirectionChange();
        parentFrame = pf;
        image = new MyImageIcon(enemyImage).resize(width, height);
        setIcon(image);
    }

    private int RandomHorizontalSpeed() {
        return new Random().nextInt(5) - 2; //It will random number between -2 and 2, which is the speed of enemy going left or right
    }

    private int RandomDirectionChange() {
        return new Random().nextInt(71) + 30; //This will random number between 30 and 100, which determines how often the enemy changes direction.
    }

    public void shoot(java.util.List<Projectile> projectile) {
        int projectileWidth = 15;
        int projectileHeight = 15;
        int projectileSpeed = -7;
        int projectileX = x + width / 2 - projectileWidth / 2;
        int projectileY = y + height;
        projectile.add(new Projectile(projectileX, projectileY, projectileWidth, projectileHeight, projectileSpeed, damage, projectileImage, parentFrame));
    }

    @Override
    public void update() {
        // Update enemy's position, behavior, etc.
        y += verticalspeed;
        x += horizontalspeed;

        if (x < 0) { //check if an enemy go out of bound on the left side
            x = 0; //if enemy went out of bound on left side, it will set x to 0
            horizontalspeed = -horizontalspeed; //then reverse the horizontalspeed which make enemy go to opposite direction
        } else if (x > GamePanel.getGameWidth() - width) { //this one check out of bound on the right side
            x = GamePanel.getGameWidth() - width; //if out of bound, set x coordinate to edge of game panel
            horizontalspeed = -horizontalspeed; //reverse horizontalspeed, make enemy go to opposite direction
        }
        updateCounter++; //Increase counter by one in each update()
        if (updateCounter >= updatesDirectionChange) { //if the counter is >= updatesDirectionChange, this will change enemy direction
            horizontalspeed = RandomHorizontalSpeed(); //randomize whether the speed is negative (go to the left) or positive (go to the right)
            updateCounter = 0; //reset counter back to 0
            updatesDirectionChange = RandomDirectionChange(); //Randomize updatesDirectionChange to number between 30-100, this prevent enemy from having fix directional change time interval (Basically make sure enemy doesn't changes direction every x seconds but changes direction every random seconds)
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
    private String imagePath;
    private MyImageIcon image;
    private MainApplication parentFrame;

    public Projectile(int x, int y, int width, int height, int speed, int damage, String imagePath, MainApplication parentFrame) {
        super(x, y, width, height, 0, damage);
        this.speed = speed;
        this.OutOfBoundProjectile = false;

        this.imagePath = imagePath;
        this.parentFrame = parentFrame;

        image = new MyImageIcon(imagePath).resize(width, height);
        setIcon(image);
    }

    public boolean OutOfBoundBullet() {
        return OutOfBoundProjectile;
    }

    public int getSpeed() {
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
        g.drawImage(image.getImage(), x, y, width, height, parentFrame);
    }
}

class GamePhysic { //game physic will be in this class: projetile and enemy collision, enemy and player collision, etc.

    private boolean GameOver;

    public void CheckForGameOver(Player player) {
        if (player.WhatMeLife() <= 0) {
            GameOver = true;
        }
    }

    public boolean IsGameOver() {
        return GameOver;
    }

    public void GamePhysicUpdate(Player player, java.util.List<Enemy> enemies, java.util.List<Projectile> projectiles) {
        CheckForGameOver(player);
        if (!GameOver) {
            PlayerToEnemyCollision(player, enemies);
            ProjectileCollisionPlayer(player, projectiles);
            ProjectileCollisionEnemy(player, enemies, projectiles);
        }
    }

    private void PlayerToEnemyCollision(Player player, java.util.List<Enemy> enemies) { //This will check collision between player and enemy, if the player hitbox intersect with enemy hitbox the player will take damage
        Rectangle playerBounds = player.getBounds(); //Use rectangle to get the hitbox of player
        for (Enemy enemy : enemies) {
            if (playerBounds.intersects(enemy.getBounds())) { //This one detect if player rectangle intersects with enemy rectangle
                player.MeLifeIs(player.WhatMeLife() - 10); //If they hit each other it will decreasees player hp by 10
            }
        }
    }

    private void ProjectileCollisionPlayer(Player player, java.util.List<Projectile> projectiles) {
        Rectangle playerBounds = player.getBounds();
        projectiles.removeIf(projectile -> { //this will make the bullet disappears once it hit a player
            if (projectile.getSpeed() < 0 && playerBounds.intersects(projectile.getBounds())) { //Check collision between player and bullet. projectile.getspeed() < 0 means the bullet is from enemy
                player.MeLifeIs(player.WhatMeLife() - projectile.getDamage()); //If player rectangle intersects with enemy bullet rectangle, it will decreases player hp depending on enemy damage
                return true; //remove projectile from list
            }
            return false;
        });
    }

    private void ProjectileCollisionEnemy(Player player, java.util.List<Enemy> enemies, java.util.List<Projectile> projectiles) {
        projectiles.removeIf(projectile -> { //This will make the bullet disappear once it hit an enemy
            if (projectile.getSpeed() > 0) { //this will check if the bullet comes from player or not (player bullet speed is in positive)
                Rectangle bulletBounds = projectile.getBounds();
                for (Enemy enemy : enemies) {
                    if (bulletBounds.intersects(enemy.getBounds())) { //If projectile rectangle (from player) intersects with enemy rectangle, it will decrease enemy hp proportion to player damage
                        enemy.MeLifeIs(enemy.WhatMeLife() - projectile.getDamage()); //Decrease enemy hp
                        if (enemy.WhatMeLife() <= 0) { //If enemy hp is less than or equal to 0 it will delete the enemy
                            enemies.remove(enemy);
                            player.IncreaseScore(10); //Increase player score whenever they kills an enemy
                        }
                        return true; //remove projectile from list
                    }
                }
            }
            return false;
        });
    }
}
