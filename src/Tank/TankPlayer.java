/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.image.BufferedImage;
import java.awt.image.ImageObserver;
import java.util.HashMap;
import java.util.Observable;
import java.util.Observer;
import javax.swing.JApplet;


//PlayerState class is used to keep the TankPlayer constructor number of arguments down
/**
 *
 * @author Kenneth
 */


public class TankPlayer extends Player implements Observer
{
    private double angleIncrement;
    private int angle;
    private int imageIndexBound;
    private java.awt.geom.Ellipse2D bounding;
    private Powerup activePowerup;
    private int altFireKey;
    private int fireKey;
    private boolean vulnerable;
    private int initalAngle;
    private boolean firing;
    

    public TankPlayer(Image img, int index, int x, int y, int speed, int lives, int angle, double angleIncrement, int imageIndexBound, MovementKeys motionKeys, int fireKey, int altFireKey, Controler control) {
        super(img, index, x, y, speed, lives, motionKeys, ' ', control);
        this.angle = angle;
        this.initalAngle = angle;
        this.angleIncrement = angleIncrement;
        this.vulnerable = true;
        this.imageIndexBound = imageIndexBound;
        this.fireKey = fireKey;
        this.altFireKey = altFireKey;
        this.firing = false;
        bounding = new java.awt.geom.Ellipse2D.Double(x+8, y+8, 45, 45); //Tight bounding
        //bounding = new Ellipse2D.Double(12+this.x, 12+this.y, 40, 40); //Loose bounding
        
        this.fireRefractoryPeriod = 40;
    }
    

    @Override
    public void draw(Graphics g, ImageObserver obs)
    {
        int imageIndex = (angle / 6);     

        if(imageIndex == 60) imageIndex = 0;
        
        if (isFunctional())
            g.drawImage(((sun.awt.image.ToolkitImage)img).getBufferedImage().getSubimage(imageIndexBound*imageIndex, 0, imageIndexBound, imageIndexBound),
                x, y, obs);    
        else 
        {
            if(this.destructionCounter % 4 == 0 || this.destructionCounter % 4 == 1)
                g.drawImage(((sun.awt.image.ToolkitImage)img).getBufferedImage().getSubimage(imageIndexBound*imageIndex, 0, imageIndexBound, imageIndexBound),
                    x, y, obs);    
            
            if(this.destructionCounter+1 >= this.destructionPause)
            {
                this.x = this.spawnX;
                this.y = this.spawnY;  
                this.angle = this.initalAngle;
                bounding = new java.awt.geom.Ellipse2D.Double(x+8, y+8, 45, 45);
                this.health = 100;
            }
            this.destructionCounter++;
        }

        if(hasPowerup != 0 && this.activePowerup != null && this.powerupCounter > 0) 
            drawPowerup(g, obs);
        
        
        this.fireCounter++;
    }
    
    private void drawPowerup(Graphics g, ImageObserver obs)
    {

        if (SheildPowerup.class.isInstance(this.activePowerup)) 
        {
            //draw red
            if (index == 1) {
                ((SheildPowerup) activePowerup).drawOnTank(g, obs, "red", x, y);
            } //draw blue
            else {
                ((SheildPowerup) activePowerup).drawOnTank(g, obs, "blue", x, y);
            }
        }
        
    }
    
    private boolean isFunctional()
    {
        return this.destructionCounter >= this.destructionPause && !isDestroyed;
    }
    
    
    @Override
    public void tick()
    {        
        if(health <= 0) this.blowUp();
            
        if(isFunctional())
        {
            if(angle + movingX*angleIncrement < 0)
                angle = (int)Math.round(360.0 + angle + movingX*angleIncrement);

            else if(angle > 360)
                angle = ((int)Math.round(angle + movingX*angleIncrement) % 360);
            else
                angle += (int)Math.round(movingX*angleIncrement);

            if(movingY != 0)        
            {  
                int xStep = (int)Math.round(speed*Math.cos(Math.PI*angle / 180.0d));
                int yStep = (int)Math.round(speed*Math.sin(Math.PI*angle / 180.0d));
                java.awt.geom.Ellipse2D oldBounding = this.bounding;

                x += movingY*xStep;
                y += -movingY*yStep;

                this.bounding = new java.awt.geom.Ellipse2D.Double(x+8, y+8, 45, 45); //Tight bounding

                for (Player currentPlayer : control.players) 
                {
                     if (((TankPlayer) currentPlayer).getIndex() != this.getIndex() && collision(((TankPlayer) currentPlayer).getBounding())) 
                     {
                        x -= movingY*xStep;
                        y -= -movingY*yStep;

                        this.bounding = oldBounding;
                     }
                }

                for(int i = 0; i < control.worldMap.getMapHeight(); i++)
                {
                    HashMap<Integer, Block> blockrow = control.worldMap.getBlockRow(i);

                    for(Block currentBlock : blockrow.values())
                    {
                        boolean isBrokenBlock = (DestructableBlock.class == currentBlock.getClass() && ((DestructableBlock)currentBlock).isInFlux());
                        if(!isBrokenBlock || Block.class == currentBlock.getClass())
                        {
                            if(this.collision(currentBlock.bounding))
                            {
                                x -= movingY*xStep;
                                y -= -movingY*yStep;

                                this.bounding = oldBounding;
                            }
                        }
                    }
                }
                
            }
            
            if(SheildPowerup.class.isInstance(this.activePowerup) && this.powerupCounter > 0)
                this.powerupCounter--;
        
        
            else if(this.powerupCounter == 0)
            {
                this.vulnerable = true;
                this.activePowerup = null;
                this.hasPowerup = 0;
                this.powerupCounter--;
            }
            
            //Check to see if the player has tried to fire to recently
            if (firing && this.fireCounter > this.fireRefractoryPeriod) 
                fire();
        }  
    }
    
    
    @Override
    public void update(Observable obj, Object arg)
    {
        GameEvents ge = (GameEvents) arg;
        if (ge.type == 1) {
            KeyEvent e = (KeyEvent) ge.event;

            if (e.getKeyCode() == this.motionKeys.getLeft())
                movingX = +1;
            else if (e.getKeyCode() == this.motionKeys.getRight())
                movingX = -1;
            if (e.getKeyCode() == this.motionKeys.getUp())
                movingY = +1;
            else if (e.getKeyCode() == this.motionKeys.getDown() 
                    || (e.getKeyCode() == 12 && this.motionKeys.getDown() == KeyEvent.VK_DOWN)) //This was added on to associate NUMPAD5 with down key
                movingY = -1;
            
            if(e.getKeyCode() == this.altFireKey && this.hasPowerup != 0)
            {
                if(SheildPowerup.class.isInstance(this.activePowerup))
                {
                    this.powerupCounter = activePowerup.powerupDuration;
                    this.vulnerable = false;
                }
                else if(GuidedRocketPowerup.class.isInstance(this.activePowerup))
                {

                    if (this.fireCounter > this.fireRefractoryPeriod) 
                    {
                        Player target;
                        if(index == 1)
                            target = control.players.get(1);
                        else
                            target = control.players.get(0);
                        
                        double attackAngle = GuidedRocketPowerup.attackAngle(this, target);
                        int rocketSpeed = GuidedRocket.getDesiredRocketSpeedBoost()+this.speed;
                        int rocketXStep = (int)Math.round(rocketSpeed*Math.cos(Math.PI*attackAngle / 180.0d));
                        int rocketYStep = (int)Math.round(rocketSpeed*Math.sin(Math.PI*attackAngle / 180.0d));
                        
                        
                        Point2D spawnLocation = getProjectileSpawnLocation(attackAngle);
                        
                        control.bullets.add(new GuidedRocket(control, (int)Math.round(spawnLocation.getX()), (int)Math.round(spawnLocation.getY()), rocketXStep, rocketYStep, 1, (int)Math.round(attackAngle), this));
                    
                        
                        if(powerupCounter == 1)
                            this.activePowerup = null;

                        this.fireCounter = 0;
                        this.powerupCounter--;
                    }
                }
            }
            //Change firing flag
            if(e.getKeyCode() == this.fireKey)
                this.firing = true;
            
        }
        else if (ge.type == 2) 
        {
            String msg = (String) ge.event;

            
            //Contact with an enemy bullet
            if (msg.equals("ExplosionSmall " + this.index)) 
            {
                if (!Shell.class.isInstance(ge.cause) || ge.cause == null) 
                {
                    System.out.println("Error: inside player.update(), gameEvent.cause does not exist or is not a bullet.");
                    System.out.println("ge.cause = "+ge.cause);
                }
                else 
                {
                    //The idea here is to make it look like the explosion is occuring right where the contact between the bullet and plane is.
                    int explosionX = ((Bullet) ge.cause).getX() + (Shell.getImageIndexBound() / 4);
                    int explosionY = ((Bullet) ge.cause).getY() + /*(Shell.getImageIndexBound() / 2)*/ +this.imageIndexBound / 10;

                    control.explosions.add(new StripExplosion(control, explosionX, explosionY, 5, 6, 0, 32, "Resources/Explosion_small_strip6.png"));

                    super.playSound("Resources/Explosion_small.wav");
                    if(vulnerable)
                    {
                        if(((Bullet) ge.cause).getClass() == Shell.class)
                            this.health -= Bullet.bulletStrengthArray.get(0);
                        
                        else if(((Bullet) ge.cause).getClass() == GuidedRocket.class)
                            this.health -= Bullet.bulletStrengthArray.get(1);
                    }
                }
            }
            //"Powerup , Sheild,1"
            if((msg.split(",")[0]).equals("Powerup ") && (msg.split(",")[2]).equals(Integer.toString(index)))
            {
                
                if((msg.split(",")[1]).equals(" Sheild"))
                {
                    
                    this.activePowerup = (SheildPowerup)ge.cause;
                    this.powerupCounter = -1;
                    this.hasPowerup = 1;
                }
                
                else if((msg.split(",")[1]).equals(" Rocket"))
                {
                    this.activePowerup = (GuidedRocketPowerup)ge.cause;
                    this.powerupCounter = activePowerup.powerupDuration;
                    this.hasPowerup = 2;
                }
                
            }
            
        }
        //Key was released
        else if(ge.type == 3)
        {
            KeyEvent e = (KeyEvent) ge.event;
            if (e.getKeyCode() == this.motionKeys.getLeft() || e.getKeyCode() == this.motionKeys.getRight()) 
                movingX = 0;
            if (e.getKeyCode() == this.motionKeys.getUp() || e.getKeyCode() == this.motionKeys.getDown()
                    || (e.getKeyCode() == 12 && this.motionKeys.getDown() == KeyEvent.VK_DOWN)) 
                movingY = 0;
            if(e.getKeyCode() == this.fireKey)
                firing = false;
        }
    }
    
    public boolean collision(java.awt.geom.Ellipse2D bound) 
    {        
        return Math.pow(bound.getCenterX()-bounding.getCenterX(), 2) + Math.pow(bound.getCenterY()-bounding.getCenterY(), 2) <= Math.pow((bound.getWidth()/2.0d) + (bounding.getWidth()/2.0d), 2);
    }
    
    public boolean collision(java.awt.geom.Rectangle2D bound)
    {
        return bounding.intersects(bound);
    }
    
    @Override
    public boolean collision(int x, int y, int w, int h) 
    {
        
        if ((y + h > this.y) && (y < this.y + this.imageIndexBound)) {
            if ((x + w > this.x) && (x < this.x + this.imageIndexBound)) {
                return true;
            }
        }
        return false;
    }
    
    private void fire()
    {
        int shellSpeed = Shell.getDesiredShellSpeedBoost() + this.speed;
        int shellXStep = (int) Math.round(shellSpeed * Math.cos(Math.PI * angle / 180.0d));
        int shellYStep = -(int) Math.round(shellSpeed * Math.sin(Math.PI * angle / 180.0d));

        Point2D spawnLocation = getProjectileSpawnLocation(angle);

        control.bullets.add(new Shell(control, (int) Math.round(spawnLocation.getX()), (int) Math.round(spawnLocation.getY()), shellXStep, shellYStep, 1, this.angle, this));

        this.fireCounter = 0;
    }
    
    private java.awt.geom.Point2D getProjectileSpawnLocation(double angle)
    {
        double cannonX;
        double cannonY;
        //These values are for testing where the Shell should be placed when the tank is at right angles
        /*
        if (angle >= 315 || angle < 45) //Smaller 0-90
        {
            cannonX = x + (this.imageIndexBound) - 10;
            cannonY = (y + (this.imageIndexBound / 4)) + 3;
        } 
        else if (angle < 135)//90-180
        {
            cannonX = x + (this.imageIndexBound / 4) + 5;
            cannonY = y - 10;
        } 
        else if (angle < 225) 
        {
            cannonX = x - 5;//Move left -
            cannonY = (y + (this.imageIndexBound / 4)) + 3;//Move down +
        } else 
        {
            cannonX = x + (this.imageIndexBound / 4) + 3;
            cannonY = (y + this.imageIndexBound) - 10;
        }*/

        //The position between the right angles is calculated by taking a weighted average of the values found experimentally for the closest right angles
        if (angle >= 0 && angle < 90) 
        {
            double angleRatio = (((double) angle) / 90.0d);
            //NextValue*angleRatio + CurrentValue*(1-angleRatio)
            cannonX = (x + (this.imageIndexBound / 4.0d) + 5) * angleRatio + (x + (this.imageIndexBound) - 10) * (1.0d - angleRatio);
            cannonY = (y - 10) * angleRatio + ((y + (this.imageIndexBound / 4)) + 3) * (1.0d - angleRatio);
        } 
        else if (angle < 180) 
        {
            double angleRatio = (((double) (angle - 90.0d)) / 90.0d);
            cannonX = (x - 5) * angleRatio + (x + (this.imageIndexBound / 4) + 5) * (1.0d - angleRatio);
            cannonY = ((y + (this.imageIndexBound / 4)) + 3) * angleRatio + (y - 10) * (1.0d - angleRatio);
        } 
        else if (angle < 270)
        {
            double angleRatio = (((double) (angle - 180.0d)) / 90.0d);
            cannonX = (x + (this.imageIndexBound / 4) + 3) * angleRatio + (x - 5) * (1.0d - angleRatio);
            cannonY = ((y + this.imageIndexBound) - 10) * angleRatio + ((y + (this.imageIndexBound / 4)) + 3) * (1.0d - angleRatio);
        } 
        else 
        {
            double angleRatio = (((double) (angle - 270.0d)) / 90.0d);
            cannonX = (x + (this.imageIndexBound) - 10) * angleRatio + (x + (this.imageIndexBound / 4) + 3) * (1.0d - angleRatio);
            cannonY = ((y + (this.imageIndexBound / 4)) + 3) * angleRatio + ((y + this.imageIndexBound) - 10) * (1.0d - angleRatio);
        }
        
        return new java.awt.geom.Point2D.Double(cannonX, cannonY);
    }

    public Ellipse2D getBounding() {
        return bounding;
    }

    public int getIndex() {
        return index;
    }
    
    public double getAngleIncrement() {
        return angleIncrement;
    }

    public void setAngleIncrement(int angleIncrement) {
        this.angleIncrement = angleIncrement;
    }

    public int getAngle() {
        return angle;
    }

    public int getPowerupCounter() {
        return powerupCounter;
    }

    public Powerup getActivePowerup() {
        return activePowerup;
    }
    
    

    private void blowUp() 
    {
        if(this.lives > 0)
        {
            this.lives--;
            this.destructionCounter = 0;
            this.health = 100;

            control.explosions.add(new StripExplosion(control, this.x, this.y, 5, 7, 2, 64, "Resources/Explosion_large_strip7.png"));
            this.playSound("Resources/Explosion_large.wav");  
            
            if(this.index == 0) control.playerScores.set(1, control.playerScores.get(1)+1);
            else control.playerScores.set(0, control.playerScores.get(0)+1);
        }
        else
            isDestroyed = true;
    }
}