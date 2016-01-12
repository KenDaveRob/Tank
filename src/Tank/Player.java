/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JApplet;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;



/**
 *
 * @author Kenneth
 */

class MovementKeys {

    private int up;
    private int down;
    private int left;
    private int right;

    public MovementKeys(int up, int down, int left, int right) {
        this.up = up;
        this.down = down;
        this.left = left;
        this.right = right;
    }

    public int getUp() {
        return up;
    }

    public void setUp(int up) {
        this.up = up;
    }

    public int getDown() {
        return down;
    }

    public void setDown(int down) {
        this.down = down;
    }

    public int getLeft() {
        return left;
    }

    public void setLeft(int left) {
        this.left = left;
    }

    public int getRight() {
        return right;
    }

    public void setRight(int right) {
        this.right = right;
    }

    
}


public class Player implements Observer {
        protected Image img;
        protected int x, y, speed;
        protected int spawnX, spawnY;
        protected int boom, explosionCounter;
        protected int boomTime;
        protected ImageObserver obs;
        protected int movingX;
        protected int movingY;
        protected MovementKeys motionKeys;
        protected char fireKey;
        protected Controler control;
        protected int bulletSpeed;
        protected int index;
        protected int fireRefractoryPeriod;
        protected int fireCounter;
        protected int health;
        protected int lives;
        protected boolean isDestroyed;
        protected int destructionCounter, destructionPause;
        protected int powerupCounter;
        protected int hasPowerup;
        

        Player(Image img, int index, int x, int y, int speed, int lives, MovementKeys motionKeys, char fireKey, Controler control) {
            this.index = index;
            this.control = control;
            this.img = img;
            this.x = x;
            this.spawnX = x;
            this.y = y;
            this.spawnY = y;
            this.speed = speed;
            this.health = 100;
            bulletSpeed = -1;
            boom = 0;
            movingX = 0;
            movingY = 0;
            this.motionKeys = motionKeys;
            this.fireKey = fireKey;
            this.lives = lives;
            this.fireRefractoryPeriod = 2;
            this.fireCounter = 0;
            this.isDestroyed = false;
            this.destructionPause = 5*7*3  + 1;
            this.powerupCounter = 0;
            this.hasPowerup = 0;
            this.destructionCounter = this.destructionPause;
        }

        public void draw(Graphics g, ImageObserver obs) {
            //Check to see if player is destroyed or waiting to respawn
            if(this.destructionCounter >= this.destructionPause && !isDestroyed)
                g.drawImage(img, x, y, obs);
            else
            {
                //If player waiting to respawn then play the destruction noice a number of times
                if(this.destructionCounter % (7*3)  == 0)
                    this.playSound("Resources/snd_explosion2.wav");
                this.destructionCounter++;
            }
            
            //Protects against the same event causing multiple explosions
            if(explosionCounter == boomTime){
                boom = 0;
                explosionCounter = 0;
            } else explosionCounter++;
            
            this.fireCounter++;
            this.obs = obs; 
            
        }
        
    protected void playSound(String filename) 
    {
        try {
            AudioPlayer.player.start(new AudioStream(getClass().getResourceAsStream(filename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int getBulletSpeed() {
        return bulletSpeed;
    }
   
    

    public boolean isDestroyed() {
        return isDestroyed;
    }
    

    public void setBulletSpeed(int bulletSpeed) {
        this.bulletSpeed = bulletSpeed;
    }

    public void setDestructionPause(int destructionPause) {
        this.destructionPause = destructionPause;
    }
    
    public boolean collision(int x, int y, int w, int h) {
        
        if ((y + h > this.y) && (y < this.y + img.getHeight(this.obs))) {
            if ((x + w > this.x) && (x < this.x + img.getWidth(this.obs))) {
                return true;
            }
        }
        return false;
    }
        
        public void tick()
        {
            if(health <= 0) this.blowUp();
            
            //Keep player inside play area
            if((x + speed*movingX) < control.appletWidth-(this.img.getWidth(null)-10) && (x + speed*movingX) > -10 &&
                   (y + speed*movingY) < control.appletHeight-(this.img.getHeight(null)-15) && (y + speed*movingY) > -15)
            {
                x += speed*movingX;
                y += speed*movingY;
            }
        }
      
        public void update(Observable obj, Object arg)
        {
            GameEvents ge = (GameEvents) arg;
            if(ge.type == 1) {
                KeyEvent e = (KeyEvent) ge.event;
                
                if(e.getKeyCode() == this.motionKeys.getLeft())
                    movingX = -1;
                else if(e.getKeyCode() == this.motionKeys.getRight())
                    movingX = +1;
                else if(e.getKeyCode() == this.motionKeys.getUp())
                    movingY = -1;
                else if(e.getKeyCode() == this.motionKeys.getDown())
                    movingY = +1;
                
                if(e.getKeyChar() == this.fireKey)
                {
                    //Check to see if the player has tried to fire to recently
                    if(this.fireCounter > this.fireRefractoryPeriod)
                    {
                        //Check if the player is currently power uped
                        if(this.powerupCounter > 0)
                        {
                            //Generate bullets going at 45 degree angles
                            double speedComponent = Math.sqrt(2.0)/2.0;
                            
                            control.bullets.add(new Bullet(control,this.x+(this.img.getWidth(null)/4),this.y-8, 0, this.bulletSpeed, 2));
                            control.bullets.add(new Bullet(control,this.x+(this.img.getWidth(null)/4)-12,this.y-12, 
                                    (int)Math.round(speedComponent*((double)this.bulletSpeed)), (int)Math.round(speedComponent*((double)this.bulletSpeed)), 2, 135));
                            control.bullets.add(new Bullet(control,this.x+(this.img.getWidth(null)/4)-5,this.y-16, 
                                    -(int)Math.round(speedComponent*((double)this.bulletSpeed)), (int)Math.round(speedComponent*((double)this.bulletSpeed)), 2, 45));

                            /*
                            ((Tank)this.workingApplet).addShell(this.x+(this.img.getWidth(null)/4), this.y-8, 0, this.bulletSpeed, 2);
                            ((Tank)this.workingApplet).addShell(this.x+(this.img.getWidth(null)/4)-12, this.y-12, 
                                    (int)Math.round(speedComponent*((double)this.bulletSpeed)), (int)Math.round(speedComponent*((double)this.bulletSpeed)), 2, 135);
                            ((Tank)this.workingApplet).addShell(this.x+(this.img.getWidth(null)/4)-5, this.y-16, 
                                    -(int)Math.round(speedComponent*((double)this.bulletSpeed)), (int)Math.round(speedComponent*((double)this.bulletSpeed)), 2, 45);
                            */
                            powerupCounter--;
                        }
                        else
                        {
                            this.hasPowerup = 0;
                            control.bullets.add(new Bullet(control,this.x+(this.img.getWidth(null)/4), this.y-8, 0, this.bulletSpeed, 2));
                            //((Tank)this.workingApplet).addShell(this.x+(this.img.getWidth(null)/4), this.y-8, 0, this.bulletSpeed, 2);  
                        }
                        
                        this.fireCounter = 0;
                    }
                }

            }
            else if(ge.type == 2) {
                String msg = (String)ge.event;
                
                if(msg.equals("EnemyCollision "+this.index)) {
                    if(boom == 0) 
                    {
                        if(!Enemy.class.isInstance(ge.cause) || ge.cause == null)
                            System.out.println("Error: inside player.update(), gameEvent.cause does not exist or is not an enemy");
                        else
                        {
                            int explosionX = ((Enemy)ge.cause).getX();
                            int explosionY = ((Enemy)ge.cause).getY();
                            
                            health -= control.damageFromRamming;
                            
                            this.boomTime = 5*7*1  + 1;
                            control.explosions.add(new Explosion(control.workingApplet, explosionX, explosionY, 2, 5, 7, 1));
                            this.playSound("Resources/snd_explosion2.wav");
                        }
                    }
                    boom = 1;
                }
                
                //Contact with an enemy bullet
                if(msg.equals("ExplosionSmall "+this.index))
                {
                    if(ge.cause.getClass() != Bullet.class || ge.cause == null)
                        System.out.println("Error: inside player.update(), gameEvent.cause does not exist or is not a bullet.");
                    else
                    {
                        //The idea here is to make it look like the explosion is occuring right where the contact between the bullet and plane is.
                        int explosionX = ((Bullet)ge.cause).getX() + (Bullet.smallImg.getWidth(null)/4);
                        int explosionY = ((Bullet)ge.cause).getY() + (Bullet.smallImg.getWidth(null)/2)+this.img.getHeight(null)/10;
                        this.health -= Bullet.bulletStrengthArray.get(0);

                        control.explosions.add(new Explosion(control.workingApplet, explosionX, explosionY, 1, 5, 6, 1));
                        this.playSound("Resources/snd_explosion1.wav");
                    }
                } 
                
                if(msg.equals("Powerup " + this.index))
                {
                    if(this.hasPowerup == 0)
                    {
                        if(ge.cause.getClass() != Powerup.class || ge.cause == null)
                            System.out.println("Error: inside player.update(), gameEvent.cause does not exist or is not a powerup.");
                        else
                        {
                            this.hasPowerup = 1;
                            if(powerupCounter > 0)
                                this.powerupCounter += ((Powerup)ge.cause).getPowerupDuration();
                            else
                                this.powerupCounter = ((Powerup)ge.cause).getPowerupDuration();
                        }
                    }
                }
            }
            
            //Key was released
            else if(ge.type == 3)
            {
                KeyEvent e = (KeyEvent) ge.event;
                if (e.getKeyCode() == this.motionKeys.getLeft() || e.getKeyCode() == this.motionKeys.getRight()) 
                    movingX = 0;
                if (e.getKeyCode() == this.motionKeys.getUp() || e.getKeyCode() == this.motionKeys.getDown()) 
                    movingY = 0;
            }
        }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public void setFireRefractoryPeriod(int fireRefractoryPeriod) {
        this.fireRefractoryPeriod = fireRefractoryPeriod;
    }

    public int getHealth() {
        return health;
    }

    public int getLives() {
        return lives;
    }
    
    
    
    private void blowUp()
    {
        if(this.lives > 0)
        {
            this.lives--;
            this.health = 100;
            this.destructionCounter = 0;
            this.boomTime = 5*7*3  + 1;
            this.boom = 1;
            control.explosions.add(new Explosion(control.workingApplet, this.x, this.y, 2, 5, 7, 3));
            this.x = this.spawnX;
            this.y = this.spawnY;     
                        
        }
        else
            isDestroyed = true;
    }
    
    
    
    
    
    public void setVelocity(int xStep, int yStep)
    {
        this.movingX = xStep;
        this.movingY = yStep;
    }

    public Image getImg() {
        return img;
    }

    public ImageObserver getObs() {
        return obs;
    }
        
}
