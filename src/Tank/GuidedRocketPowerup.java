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

/**
 *
 * @author Kenneth
 */
public class GuidedRocketPowerup extends TankPowerup
{
    private static int imageIndexBound;
    private static int imagePosition;
    private static Image rocketIconImage;
    private static int fluxTime;
    //private int fluxCounter;

    public GuidedRocketPowerup(int x, int y, int speed, int powerupDuration, Controler control, String type) 
    {
        super(rocketIconImage, x, y, speed, powerupDuration, control, type);
        this.sizeX = imageIndexBound;
        fluxCounter = fluxTime;
    }
    
    @Override
    public void draw(Graphics g, ImageObserver obs) {
        if (show) 
        {
            if(!isInFlux())
                g.drawImage(((sun.awt.image.ToolkitImage)img).getBufferedImage().getSubimage(imageIndexBound*imagePosition, 0, imageIndexBound, imageIndexBound),
                x, y, obs);
            
            
            else
                fluxCounter++;
            
        }
    }
    
    public void draw(Graphics g, ImageObserver obs, int x, int y) 
    {
        g.drawImage(((sun.awt.image.ToolkitImage)img).getBufferedImage().getSubimage(imageIndexBound*imagePosition, 0, imageIndexBound, imageIndexBound),
                x, y, obs);
    }
    
    
    public boolean isInFlux()
    {
        return fluxCounter < fluxTime;
    }
    
    @Override
    protected boolean isCollectable()
    {
        return this.show && !isInFlux();
    }
    
    public static double attackAngle(Player firer, Player target)
    {
        double attackAngle = Math.toDegrees(Math.atan2(target.getY() - firer.getY(),((double)(target.getX() - firer.getX()))));
        if(attackAngle < 0) attackAngle += 360.0d;
        return attackAngle;
    }
    
    public static void setImagePosition(int imagePosition) {
        GuidedRocketPowerup.imagePosition = imagePosition;
    }

    public static void setImageIndexBound(int imageIndexBound) {
        GuidedRocketPowerup.imageIndexBound = imageIndexBound;
    }

    public static void setRocketIconImage(Image rocketIconImage) {
        GuidedRocketPowerup.rocketIconImage = rocketIconImage;
    }
    
    

    public static Image getRocketIconImage() {
        return rocketIconImage;
    }

    public static void setFluxTime(int fluxTime) {
        GuidedRocketPowerup.fluxTime = fluxTime;
    }    
  
}
