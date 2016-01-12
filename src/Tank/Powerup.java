/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import javax.swing.JApplet;

/**
 *
 * @author Kenneth
 */
public class Powerup implements Spawnable
{
    protected Image img;
    protected int x, y, sizeX, sizeY;
    protected int powerupDuration;
    protected int speed;
    protected Controler control;
    protected boolean show;
    private String type = "";

    public Powerup(Image img, int x, int y, int speed, int powerupDuration, Controler control, String type) 
    {
        this.img = img;
        this.sizeX = img.getWidth(null);
        this.sizeY = img.getHeight(null);
        this.show = true;
        this.powerupDuration = powerupDuration;
        this.x = x;
        this.y = y;
        this.speed = speed;
        this.control = control;
        this.type = type;
    }


    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            g.drawImage(img, x, y, obs);
        }
    }
    
    @Override
    public void tick(int w, int h, GameEvents gameEvents) throws NullPointerException {
        int i = 1;
        y += this.speed;

        for (Player currentPlayer : control.players) 
        {
            if (currentPlayer.collision(x, y, this.sizeX, this.sizeY) && isCollectable()) 
            {
                collected();
                gameEvents.setValue("Powerup " + getType() + i, this);
                break;
            }
            i++;
        }
    }
    
    protected boolean isCollectable()
    {
        return true;
    }
    
    protected void collected()
    {
        show = false;
    }
 
    public int getPowerupDuration() {
        return powerupDuration;
    }
    

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public boolean isShown() {
        return show;
    }

    protected String getType()
    {
        return type;
    }

}
