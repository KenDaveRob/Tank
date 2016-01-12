/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.awt.image.ToolkitImage;

/**
 *
 * @author Kenneth
 */

//Some common functions shared by all powerups in tank game
public abstract class TankPowerup extends Powerup {
    protected int fluxCounter;
    
    public TankPowerup(Image img, int x, int y, int speed, int powerupDuration, Controler control, String type) {
        super(img, x, y, speed, powerupDuration, control, type);
    }

    @Override
    protected void collected() 
    {
        this.fluxCounter = 0;
        this.playSound("Resources/Click.wav");
    }

    protected void playSound(String filename) {
        try {
            AudioPlayer.player.start(new AudioStream(getClass().getResourceAsStream(filename)));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setControl(Controler control) {
        this.control = control;
    }
    
}
