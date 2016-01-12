/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

/**
 *
 * @author Kenneth
 */
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.*;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.Observable;
import java.util.Observer;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.DataLine;
import javax.sound.sampled.Line;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.*;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;



/**
 *
 * @author Kenneth
 */
public class Tank extends JApplet implements Runnable {

    private static final Tank demo = new Tank();
    private Thread thread; 
   
    private int x = 0;//, move = 0;
    private BufferedImage bimg;
    private BufferedImage map;
    private Random generator = new Random(1234567);
    
    private ArrayList<Player> players;
    private java.util.List<Powerup> powerups;
    private java.util.List<Bullet> bullets;
    private java.util.List<Animation> explosions;
    private Controler control;
    private int frameCounter;
    private static Map worldMap;
    
    //Application WIDTH = 1280
    private static int WIDTH;// = 1400;
    
    //Application HEIGHT 960
    private static int HEIGHT;// = 860;
    private static final int MAP_WIDTH = 2400; //These values are consistent with Background.png width and height 
    private static final int MAP_HEIGHT = 2560;//400 320 : 6 and 8
    //WIDTH 75 BLOCKS, HEIGHT 80 BLOCKS
    private static final int MINIMAP_HEIGHT = 280;//25*8
    private static final int MINIMAP_WIDTH = 280;//33*6
    private static int initalNumberOfLives = 3;
    private static String songFileName = "Resources/Music.wav";
    
    private Object mapSourceFile = Tank.class.getResource("Resources/mapInstructions.txt"); 
    //private Object mapSourceFile = new File("C:\\mapInstructions.txt"); 
    
    private boolean escPressed = false;
    private String helpString[] = {"Red Player(Left) Controls: Up: W Key"
                                  ,"Down: S Key, Left: A Key, Right: F Key"
                                  ,"Fire: Space Key, Alt. Fire: X Key"
                                  ,""
                                  ,"Blue Player(Right Controls: Up: Up Key"
                                  ,"Down: Down Key or Numpad5 Key, Left: Left Key, Right: Right Key"
                                  ,"Fire: Numpad0 Key, Alt. Fire: Numpad. Key"
                              };//,""
                                  //,"(un)Mute: M Key, Open Map: O Key"};
    
    private int damageFromRamming;
      
    private TankPlayer playerOne;
    private TankPlayer playerTwo;
    
    private Soundtrack soundtrack;
    private Thread soundthread;
    
    private GameEvents gameEvents;

    private ImageObserver observer;
    
    private javax.swing.JMenu fileMenu;
    private javax.swing.JMenu optionsMenu;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JMenuItem killSoundtrackMenuItem;
    private javax.swing.JMenuItem openScenarioMenuItem;
    private javax.swing.JMenuItem controlsMenuItem;
    private JFrame mainFrame;
    
    private boolean initialized = false;
    //Fast mode was put in because the frame rate running in applet mode on windows explorer sucks.  Firefox is a bit better.
    private static boolean applicationMode = false; //Should be true for application

    public void init() 
    {
        if(applicationMode)
        {
            WIDTH = 1280;

            HEIGHT = 960;
        }
        else
        {
            WIDTH = 1400;
            HEIGHT = 860;
        }
 
        
        setFocusable(true);
        setBackground(Color.white);
        Image redTankImage;
        Image blueTankImage;
        
        frameCounter = 0;
        damageFromRamming = 10;
        

        redTankImage = getSprite("Resources/Tank_red_basic_strip60.png");
        blueTankImage = getSprite("Resources/Tank_blue_basic_strip60.png");
        Block.setPermBlockImage(getSprite("Resources/Blue_wall1.png"));
        DestructableBlock.setDestructBlockImage(getSprite("Resources/Wall2.png"));
        SheildPowerup.setBlueSheildImage(getSprite("Resources/Shield2.png"));
        SheildPowerup.setRedSheildImage(getSprite("Resources/Shield1.png"));
        SheildPowerup.setSheildIconImage(getSprite("Resources/Pickup_strip4.png"));
        SheildPowerup.setImageIndexBound(32); //The width of the image section to be cut out of each strip (note: the width and height are often equal) 
        SheildPowerup.setImagePosition(2);

        GuidedRocketPowerup.setRocketIconImage(getSprite("Resources/Pickup_strip4.png"));
        GuidedRocketPowerup.setImageIndexBound(32); //The width of the image section to be cut out of each strip (note: the width and height are often equal) 
        GuidedRocketPowerup.setImagePosition(0);

        Shell.setShellStrip(getSprite("Resources/Shell_strip60.png"));
        Shell.setImageIndexBound(24); //The width of the image section to be cut out of each strip (note: the width and height are often equal) 
        Shell.setDesiredShellSpeedBoost(2); //Actual speed is aproximately the speed boost plus the speed of the tank
        
        GuidedRocket.setRocketStrip(getSprite("Resources/Rocket_strip60.png"));
        GuidedRocket.setImageIndexBound(24);
        GuidedRocket.setDesiredRocketSpeedBoost(1); //Actual speed is aproximately the speed boost plus the speed of the tank
        
        DestructableBlock.setFluxTime(800);
        SheildPowerup.setFluxTime(8000);
        GuidedRocketPowerup.setFluxTime(8000);
        
        //Use default value for Application
        if(!applicationMode) Map.setSheildPowerupDuration(200);
        

        Bullet.bulletStrengthArray = new ArrayList<Integer>();

        Bullet.bulletStrengthArray.add(0, 3); //Damage of shell
        Bullet.bulletStrengthArray.add(1, 10); //Damage of rocket
 
        observer = this;
        
        players = new ArrayList<Player>();
        powerups = new LinkedList<Powerup>();
        bullets = new LinkedList<Bullet>();
        explosions = new LinkedList<Animation>();
        control = new Controler();
        control.players = players;
        control.explosions = explosions;
        control.bullets = bullets;
        control.workingApplet = this;
        control.enemies = null;
        control.appletHeight = HEIGHT;
        control.appletWidth = WIDTH;
        control.damageFromRamming = damageFromRamming;
        
        control.playerScores = new ArrayList<Integer>();
        control.playerScores.add(0);
        control.playerScores.add(0);
        
        KeyControl key = new KeyControl();
        addKeyListener(key);
        
        gameEvents = new GameEvents();
        
        control.gameEvents = gameEvents;
        

        //Application Speed = 5, Application Angle Increment = 2.5d      
        if(!applicationMode)
        {
            //Left
            playerOne = new TankPlayer(redTankImage, 1, 250, 200, 10, initalNumberOfLives, 0, 5.0d, 64, new MovementKeys(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D), 32, KeyEvent.VK_Z, control); //SPACE = 32            

            //Right
            playerTwo = new TankPlayer(blueTankImage, 2, MAP_WIDTH-350, 200, 10, initalNumberOfLives, 180, 5.0d, 64, new MovementKeys(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT), 155, 127, control);//Nampad0 = 155 NUMPAD_DECIMAL = 127

            //Use default for Application
            playerOne.setFireRefractoryPeriod(20);
            playerTwo.setFireRefractoryPeriod(20);
        }
        else
        {
            //Left
            playerOne = new TankPlayer(redTankImage, 1, 250, 200, 5, initalNumberOfLives, 0, 2.5d, 64, new MovementKeys(KeyEvent.VK_W, KeyEvent.VK_S, KeyEvent.VK_A, KeyEvent.VK_D), 32, KeyEvent.VK_Z, control); //SPACE = 32            

            //Right
            playerTwo = new TankPlayer(blueTankImage, 2, MAP_WIDTH-350, 200, 5, initalNumberOfLives, 180, 2.5d, 64, new MovementKeys(KeyEvent.VK_UP, KeyEvent.VK_DOWN, KeyEvent.VK_LEFT, KeyEvent.VK_RIGHT), 155, 127, control);//Nampad0 = 155 NUMPAD_DECIMAL = 127

        }
        
        players.add(playerOne);
        players.add(playerTwo);
        
        gameEvents.addObserver(playerOne);
        gameEvents.addObserver(playerTwo);
        
        
        Map.setControl(control);
        worldMap = new Map(MAP_WIDTH, MAP_HEIGHT);
        
        try
        {
            worldMap.loadMap(this.mapSourceFile);
        }
        catch (Exception ex) {
            System.out.println("Exception found inside" + this.getClass().getName() + ".init()...");
            System.out.println(ex.getMessage());
            ex.printStackTrace();
        }
        
        control.worldMap = this.worldMap;
        
        
        
        soundtrack = new Soundtrack(songFileName);
        soundthread = new Thread(soundtrack);
        soundthread.start();
         
        
        menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        killSoundtrackMenuItem = new javax.swing.JMenuItem();
        openScenarioMenuItem = new javax.swing.JMenuItem();
        controlsMenuItem = new javax.swing.JMenuItem();
        optionsMenu = new javax.swing.JMenu();

        fileMenu.setText("File");
        killSoundtrackMenuItem.setText("Please god stop the sound!");
        openScenarioMenuItem.setText("Open Scenario");
        controlsMenuItem.setText("Show Controls");
        
        killSoundtrackMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                killSoundActionPerformed(evt);
            }
        });
        openScenarioMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openActionPerformed(evt);
            }
        });
        controlsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                controlsActionPerformed(evt);
            }
        });
                
        fileMenu.add(killSoundtrackMenuItem);

        optionsMenu.add(openScenarioMenuItem);
        optionsMenu.add(this.controlsMenuItem);
        menuBar.add(fileMenu);

        optionsMenu.setText("Options");
        menuBar.add(optionsMenu);

        if(applicationMode)
        {
            mainFrame = new JFrame("Tank Game");

            mainFrame.setJMenuBar(menuBar);
            mainFrame.addWindowListener(new WindowAdapter() {});
            mainFrame.getContentPane().add("Center", demo);
            mainFrame.pack();

            mainFrame.setSize(new Dimension(WIDTH, HEIGHT+50)); //Issue with the screen needing to be 50 pixels bigger than it should, possibly for the menu bar
            mainFrame.setVisible(true);

            mainFrame.setResizable(false);
        }
        map = generateMap(); //Map must be generated after mainFrame
        
        initialized = true;
        
    }
   

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public class KeyControl extends KeyAdapter {

        public void keyPressed(KeyEvent e) 
        {
            if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                escPressed = !escPressed;
            
            //This code doesnt work in applet mode, so its better to use the menubar for application
            /*
            else if(e.getKeyCode() == KeyEvent.VK_M)
                killSoundActionPerformed(new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, "(un)pause sound"));
            else if(e.getKeyCode() == KeyEvent.VK_O)
                openActionPerformed(new java.awt.event.ActionEvent(this, java.awt.event.ActionEvent.ACTION_PERFORMED, "open map"));
           */
                
            gameEvents.setValue(e);
        }

        //Using Released may make the tank more responsive
        public void keyReleased(KeyEvent e) {
            gameEvents.setValueReleased(e);
        }
    }
    
    public Image getSprite(String name) {
        URL url = Tank.class.getResource(name);
        Image img = getToolkit().getImage(url);
        try {
            MediaTracker tracker = new MediaTracker(this);
            tracker.addImage(img, 0);
            tracker.waitForID(0);
        } catch (Exception e) {
        }
        return img;
    }
    
    
    public BufferedImage generateMap()
    {
        Image background = getSprite("Resources/Background.png");
        
        int TileWidth = background.getWidth(this);
        int TileHeight = background.getHeight(this);
        
        int NumberX = (int) (MAP_WIDTH / TileWidth);
        int NumberY = (int) (MAP_HEIGHT / TileHeight);
                 
        BufferedImage tiledBackground = (BufferedImage) createImage(NumberX * TileWidth, NumberY * TileHeight);
        
        for (int i = 0; i <= NumberY; i++)
            for (int j = 0; j <= NumberX; j++) 
                tiledBackground.getGraphics().drawImage(background, j * TileWidth, i * TileHeight, TileWidth, TileHeight, this);
        
        for(int i = 0; i < worldMap.getMapWidth(); i++)
        {
            HashMap<Integer, Block> blockRow = worldMap.getBlockRow(i);
            for(int j = 0; j < worldMap.getMapHeight(); j++)
            {
                Block currentBlock = blockRow.get(j);
                
                if(currentBlock != null)
                    currentBlock.draw(tiledBackground.getGraphics(), this);              
            }
        }
        
        return tiledBackground;
    }
    
    public Image getPlayerViewport(int width, int height, Player player)
    {
        int cornerX = player.getX() - (width / 2);
        int cornerY = player.getY() - (height / 2);

        if (2 * player.getX() <= width)
            cornerX = 0;
        else if (2 * map.getWidth() - 2 * player.getX() <= width) //logicly equivilent to (player.getX() >= map.getWidth()-(subWidth/2))
        {
            cornerX = map.getWidth() - width;
        }

        if (2 * player.getY() <= height) 
            cornerY = 0;
        else if (2 * map.getHeight() - 2 * player.getY() <= height) //logicly equivilent to (player.getY() >= map.getHeight()-(HEIGHT/2))
        {
            cornerY = map.getWidth() - height;
        }


        return map.getSubimage(cornerX, cornerY, width, height);
    }

    // generates a new color with the specified hue
    public void drawMap(int w, int h, Graphics2D g2) {
       
        try 
        {
            int subWidth = WIDTH/2;
            Image sub;
                        
            sub = getPlayerViewport(subWidth, HEIGHT, playerOne);            
            g2.drawImage(sub, 0, 0, this);
            
            
            
            sub = getPlayerViewport(WIDTH-subWidth, HEIGHT, playerTwo);
            g2.drawImage(sub, WIDTH-subWidth, 0, this);                     

            sub = map.getScaledInstance(MINIMAP_WIDTH, MINIMAP_HEIGHT, Image.SCALE_DEFAULT);
            g2.drawImage(sub, (WIDTH/2)-(MINIMAP_WIDTH/2), HEIGHT-MINIMAP_HEIGHT, this);  
            
            g2.setColor(Color.black);
            g2.draw(new java.awt.geom.Line2D.Double(subWidth, 0, subWidth, HEIGHT-MINIMAP_HEIGHT));
                     
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        
    }

    public void drawGame(int w, int h, Graphics2D g2) {
        
        //Applet seems to run drawGame() before init()
        if(this.initialized)
        {          
            map = generateMap();

            updateAndDrawBullets(w, h, g2);
            clearOldBullets();

            try 
            {
                playerOne.tick();
                playerOne.draw(map.getGraphics(), this);

                playerTwo.tick();
                playerTwo.draw(map.getGraphics(), this);
            }
            catch(Exception e)
            {
                e.printStackTrace(); 
            }



            updateAndDrawPowerups(w, h, (Graphics2D)map.getGraphics());
            clearOldPowerups();


            processExplosions((Graphics2D)map.getGraphics());

            drawMap(w, h, g2);

            drawPlayersStatus(g2);

            //Draw Control Strings to Screen
            if(escPressed)
            {
                g2.setColor(Color.black);
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 30));
                for(int i = 0; i < helpString.length; i++)
                    g2.drawString(helpString[i], 0, HEIGHT/2-300 + i*60);
            }
            
        }
    }
    
    private void drawPlayersStatus(Graphics2D g2)
    {
        int playerOneHealth = this.playerOne.getHealth();
        int playerTwoHealth = this.playerTwo.getHealth();
        
        g2.setColor(Color.green);
        g2.drawRect((WIDTH/4)-100, HEIGHT - 22, 200, 20);
        g2.drawRect((WIDTH/2)+(WIDTH/4)-100, HEIGHT - 22, 200, 20);
        g2.setColor(Color.red);
        //Draw Left health
        g2.fillRect((WIDTH/4)-99, HEIGHT - 21, 2*playerOneHealth-1, 19);

        if(((TankPlayer)playerOne).getActivePowerup() != null)
        {
            if(SheildPowerup.class.isInstance(((TankPlayer)playerOne).getActivePowerup()))
            {
                int timeRemaining = playerOne.getPowerupCounter()/10;
                ((SheildPowerup)((TankPlayer)playerOne).getActivePowerup()).draw(g2, this, (WIDTH/4)+101, HEIGHT - 31);

                g2.setColor(Color.black);
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 10));
                if(timeRemaining == 0) timeRemaining = (((SheildPowerup)((TankPlayer)playerOne).getActivePowerup()).getPowerupDuration()/10);
                g2.drawString(Integer.toString(timeRemaining), (WIDTH/4)+101+32, HEIGHT - 3);
            }
            else if(GuidedRocketPowerup.class.isInstance(((TankPlayer)playerOne).getActivePowerup()))
            {
                int shotsRemaining = playerOne.getPowerupCounter();
                ((GuidedRocketPowerup)((TankPlayer)playerOne).getActivePowerup()).draw(g2, this, (WIDTH/4)+101, HEIGHT - 31);

                g2.setColor(Color.black);
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 10));
                if(shotsRemaining == 0) shotsRemaining = (((SheildPowerup)((TankPlayer)playerOne).getActivePowerup()).getPowerupDuration()/10);
                g2.drawString(Integer.toString(shotsRemaining), (WIDTH/4)+101+32, HEIGHT - 3);
                
            }
        }
        
        
        g2.setColor(Color.blue);
        g2.fillRect((WIDTH/2)+(WIDTH/4)-99, HEIGHT - 21, 2*playerTwoHealth-1, 19);
        
        g2.setColor(Color.black);
        g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 20));
        g2.drawString("Kills: "+control.playerScores.get(0), (WIDTH/4)-99, HEIGHT - 22);
        g2.drawString("Kills: "+control.playerScores.get(1), (WIDTH/2)+(WIDTH/4)-99, HEIGHT - 22);
        if(((TankPlayer)playerTwo).getActivePowerup() != null)
        {
            if(SheildPowerup.class.isInstance(((TankPlayer)playerTwo).getActivePowerup()))
            {
                int timeRemaining = playerTwo.getPowerupCounter()/10;
                ((SheildPowerup)((TankPlayer)playerTwo).getActivePowerup()).draw(g2, this, (WIDTH/2)+(WIDTH/4)+101, HEIGHT - 31);

                g2.setColor(Color.black);
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 10));
                if(timeRemaining == 0) timeRemaining = (((SheildPowerup)((TankPlayer)playerTwo).getActivePowerup()).getPowerupDuration()/10);
                g2.drawString(Integer.toString(timeRemaining), (WIDTH/2)+(WIDTH/4)+101+32, HEIGHT - 3);
            }
            else if(GuidedRocketPowerup.class.isInstance(((TankPlayer)playerTwo).getActivePowerup()))
            {
                int shotsRemaining = playerTwo.getPowerupCounter();
                ((GuidedRocketPowerup)((TankPlayer)playerTwo).getActivePowerup()).draw(g2, this, (WIDTH/2)+(WIDTH/4)+101, HEIGHT - 31);

                g2.setColor(Color.black);
                g2.setFont(new Font(g2.getFont().getName(), Font.BOLD, 10));
                if(shotsRemaining == 0) shotsRemaining = (((SheildPowerup)((TankPlayer)playerTwo).getActivePowerup()).getPowerupDuration()/10);
                g2.drawString(Integer.toString(shotsRemaining), (WIDTH/2)+(WIDTH/4)+101+32, HEIGHT - 3);
                
            }
        }
        
    }

    private void updateAndDrawPowerups(int w, int h, Graphics2D g2)
    {
        for(Powerup currentPowerup : worldMap.getPowerups())
        {
            currentPowerup.draw(g2, this);
            currentPowerup.tick(w, h, gameEvents);
        }
    }
    
    private void processExplosions(Graphics2D g2)
    {
        Animation explosionIndex;
        Iterator<Animation> enemiesIterator = this.explosions.iterator();
        while(enemiesIterator.hasNext())
        {
            explosionIndex = enemiesIterator.next();
            if(!explosionIndex.show)
                enemiesIterator.remove();
            
            else
            {
                explosionIndex.draw(g2, this);
                explosionIndex.incrementAnimation();  
            }
        }
    }
    
    private void updateAndDrawBullets(int w, int h, Graphics2D g2)
    {
        
        for(Bullet currentBullet : this.bullets)
        {       
            
            currentBullet.tick(w, h);
            currentBullet.draw(map.getGraphics(), this);
            
        }
        
    }
    
    private void clearOldPowerups()
    {        
        Iterator<Powerup> powerupIterator = worldMap.getPowerups().iterator();
        while(powerupIterator.hasNext())
        {
            Powerup currentPowerup = powerupIterator.next();
            
            
            if(!currentPowerup.isShown())
                powerupIterator.remove();
                
        }
    }
    
    
    private void clearOldBullets()
    {
        Iterator<Bullet> bulletIterator = this.bullets.iterator();
        while(bulletIterator.hasNext())
        {
            Bullet currentBullet = bulletIterator.next();
            if(currentBullet.getY() > map.getHeight() || currentBullet.getY() < 0 ||
                    currentBullet.getX() > map.getWidth() || currentBullet.getX() < 0)
                bulletIterator.remove();
            
            
            else if(!currentBullet.isShown())
                bulletIterator.remove();
                
        }
    }

    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.clearRect(0, 0, w, h);
        return g2;
    }

    public void paint(Graphics g) {
        Dimension d = getSize();
        Graphics2D g2 = createGraphics2D(d.width, d.height);
        drawGame(d.width, d.height, g2);
        g2.dispose();
        g.drawImage(bimg, 0, 0, this);
    }

    public void start() 
    {
        
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    //For some reason the program continues to run even if I set thread = null
    //Somehow the program seems to have gotten uncoupled from the thread its supposed to be running on,
    //Since this code is based off the wingman game that is running on a thread I'm at a loss as to why it isnt
    public void run() {
    	
        Thread me = Thread.currentThread();
        while (thread == me) 
        {
            repaint();
           
          try {
                thread.sleep(25); //25
            } catch (InterruptedException e) {
                break;
            }
            
            
        }
        
    	    	
    }
    
    
    
    /* Usefull java sound resource websites
     * http://www.anyexample.com/programming/java/java_play_wav_sound_file.xml
     * http://stackoverflow.com/questions/8425481/play-wav-file-from-jar-as-resource-using-java
     * see http://www.cs.cmu.edu/~illah/CLASSDOCS/javasound.pdf
     */
    void playSound(String filename)
    {
        try {
            AudioPlayer.player.start(new AudioStream(getClass().getResourceAsStream(filename)));
        } catch (Exception e) {
            e.printStackTrace();
        }   
    }

    
    public static void main(String argv[]) 
    {
        applicationMode = true;
        demo.init();     
        demo.start();
    }
   
       
    
    private void killSoundActionPerformed(java.awt.event.ActionEvent evt) {   
        if(demo.killSoundtrackMenuItem.getText().equals("Please god stop the sound!"))
        {
            demo.soundtrack.setContinueLooping(false);
            demo.killSoundtrackMenuItem.setText("Please turn on that wonderfull noise, It's positively orgasmic.");
        }
        
        else
        {
            soundtrack = new Soundtrack(songFileName);
            soundthread = new Thread(soundtrack);
            soundthread.start();
            demo.killSoundtrackMenuItem.setText("Please god stop the sound!");
        }     
    }   
    
    private void openActionPerformed(java.awt.event.ActionEvent evt) {
        final JFileChooser fc = new JFileChooser();
        int returnVal = fc.showOpenDialog(this);
        
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            mapSourceFile = fc.getSelectedFile().getAbsoluteFile();
            boolean playSound = soundtrack.isContinueLooping();
            demo.init();
            soundtrack.setContinueLooping(playSound);
        }
    }
    
    private void controlsActionPerformed(java.awt.event.ActionEvent evt) {
        ControlsDialog controlsDialog = new ControlsDialog(null, true);
        controlsDialog.setVisible(true);
    }

    /**
     * This method is called from within the init() method to initialize the
     * form. WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
    }// </editor-fold>//GEN-END:initComponents

    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
