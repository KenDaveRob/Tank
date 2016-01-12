/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.applet.AudioClip;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.ArrayList;
import java.util.List;


/**
 *
 * @author Kenneth
 */
public class Bullet 
{
    static Image largeImg;
    static Image largeRightImg;
    static Image largeLeftImg;
    static Image smallImg;
    static java.util.ArrayList<Integer> bulletStrengthArray;
    protected int x,y;
    protected int angle;
    protected int xStep, yStep;
    protected int strength;
    protected boolean show;
    protected Controler control;

    public Bullet(Controler control, int x, int y, int xStep, int yStep, int strength) {
        this.control = control;
        this.strength = strength;
        this.x = x;
        this.y = y;
        this.xStep = xStep;
        this.yStep = yStep;
        this.angle = 90;
        this.show = true;
    }
    
    public Bullet(Controler control, int x, int y, int xStep, int yStep, int strength, int angle) {
        this.control = control;
        this.strength = strength;
        this.x = x;
        this.y = y;
        this.xStep = xStep;
        this.yStep = yStep;
        this.angle = angle;
        this.show = true;
    }
    
    
    
    public void tick(int w, int h)
    {
        x += xStep;
        y += yStep;
        if(strength == 1)
        {
            int i = 1;
            for(Player currentPlayer : control.players)
            {
                if (currentPlayer.collision(x, y, smallImg.getWidth(null), smallImg.getHeight(null))) {
                    show = false;
                    control.gameEvents.setValue("ExplosionSmall "+i, this);
                }
                i++;
            }
        }
        
        
        else if(strength == 2)
        {
            for(Enemy currentEnemy : control.enemies)
            {
                if(currentEnemy.collision(this.x, this.y, largeImg.getWidth(null), largeImg.getHeight(null)))
                {
                    show = false;
                    control.gameEvents.setValueBulletHitEnemy(currentEnemy, this);
                    break;//this break makes it so that one bullet can kill only one enemy
                }
            }
        }
    }
    
    public boolean isShown()
    {
        return this.show;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    
    
    public void setVelocity(int xStep, int yStep)
    {
        this.xStep = xStep;
        this.yStep = yStep;
    }

    public void draw(Graphics g, ImageObserver obs) 
    {
        
        if (show) {
            //Draw small bullet
            if(strength == 1)
                g.drawImage(this.smallImg, x, y, obs);
            else if(strength == 2)
            {
                //Straight bullet
                if(angle == 90)
                    g.drawImage(this.largeImg, x, y, obs);
                
                //Right angle bullet
                else if(angle == 45)
                    g.drawImage(this.largeRightImg, x, y, obs);
                
                //Left angle bullet
                else if(angle == 135)
                    g.drawImage(this.largeLeftImg, x, y, obs);
            }
        }
    } 
}