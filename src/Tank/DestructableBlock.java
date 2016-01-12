/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Observable;
import java.util.Observer;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 *
 * @author Kenneth
 */
public class DestructableBlock extends Block implements Observer
{
    private static Image destructBlockImage;
    private static int fluxTime;
    private int fluxCounter;
    private int health;
    private int initialHealth;
    private Controler control;
    
    public DestructableBlock(Controler control,int x, int y, int health) 
    {
        super(x, y);
        fluxCounter = fluxTime;
        this.health = health;
        this.initialHealth = health;
        this.control = control;
    }
    
    @Override
    public void draw(Graphics g, ImageObserver obs) {
        if (show) 
        {
            if(!isInFlux())
                g.drawImage(destructBlockImage, x, y, obs);
            
            
            else
                fluxCounter++;
        }
    }

    @Override
    public void update(Observable o, Object arg) 
    {
        GameEvents ge = (GameEvents) arg;
        if (ge.type == 4) 
        {
            if (this == ge.event) 
            {
                --health;
                if(health <= 0)
                {
                    this.playSound("Resources/turret.wav");
                    this.fluxCounter = 0;
                    health = this.initialHealth;
                }
                
                else
                {
                    control.explosions.add(new StripExplosion(control, ((Bullet)ge.cause).getX(), ((Bullet)ge.cause).getY(), 5, 6, 0, 32, "Resources/Explosion_small_strip6.png"));
                    this.playSound("Resources/Explosion_small.wav");
                }
            }
        }
    }
    
    public boolean isInFlux()
    {
        return fluxCounter < fluxTime;
    }

    public static void setDestructBlockImage(Image destructBlockImage) {
        DestructableBlock.destructBlockImage = destructBlockImage;
    }

    public static void setFluxTime(int fluxTime) {
        DestructableBlock.fluxTime = fluxTime;
    }
    
    
    protected void playSound(String filename) 
    {
        try {
            AudioPlayer.player.start(new AudioStream(getClass().getResourceAsStream(filename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
}
