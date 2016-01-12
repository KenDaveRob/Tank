/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Observer;
import javax.swing.JApplet;

/**
 *
 * @author Kenneth
 */

//Generic Enemy class, should be reusable for just about any enemy you can come up with
public abstract class Enemy implements Observer, Spawnable
{
    protected Image img;
    protected int x, y, sizeX, sizeY;
    protected JApplet workingApplet;
    protected boolean show;
    protected int scoreShootingDown;
    
    public void setImg(Image img) {
        this.img = img;
        sizeX = img.getWidth(null);
        sizeY = img.getHeight(null);
        this.show = true;
    }
    
    public void setWorkingApplet(JApplet workingApplet) {
        this.workingApplet = workingApplet;
    }

    
    public void setImageAndApplet(JApplet workingApplet, Image img) 
    {
        this.setWorkingApplet(workingApplet);
        this.setImg(img);
    }

    public void setScoreShootingDown(int scoreShootingDown) {
        this.scoreShootingDown = scoreShootingDown;
    }
    
    public boolean isShown()
    {
        return this.show;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public void setY(int y) {
        this.y = y;
    }
    
    
    
    abstract void move();
    abstract java.util.List<Bullet> fire();
    abstract void draw(Graphics g, ImageObserver obs);
    abstract boolean collision(int x, int y, int w, int h);
    abstract public void tick(int w, int h, GameEvents gameEvents) throws NullPointerException;
    
    protected void damageFromCollision()
    {
        show = false;
    }
    
    java.util.List<Bullet> tick(GameEvents gameEvents) throws NullPointerException {
        if (workingApplet == null) {
            throw new NullPointerException("Enemy update() called but workingApplet == null.");
        }
        move();

        int i = 1;
        for (Player currentPlayer : ((Tank) this.workingApplet).getPlayers()) {
            if (currentPlayer.collision(x, y, sizeX, sizeY)) {
                damageFromCollision();
                gameEvents.setValue("EnemyCollision " + i, this);
            }
            i++;
        }

        return fire();
    }
    
    
    
}
