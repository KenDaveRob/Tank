/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;
import java.util.Observable;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;
import sun.awt.image.ToolkitImage;

/**
 *
 * @author Kenneth
 */
public class SheildPowerup extends TankPowerup
{
    //private int powerupCounter;
    private String color;
    private static Image redSheildImage;
    private static Image blueSheildImage;
    private static Image sheildIconImage;
    private static int imagePosition;
    private static int imageIndexBound;
    private static int fluxTime;
    
    
    
    //", Sheild,"
    public SheildPowerup(int x, int y, int speed, int powerupDuration, Controler control, String type) 
    {
        super(sheildIconImage, x, y, speed, powerupDuration, control, type);
        fluxCounter = fluxTime;
        this.sizeX = imageIndexBound;
    }
    
    public static void setFluxTime(int fluxTime) {
        SheildPowerup.fluxTime = fluxTime;
    }

    public static void setImageIndexBound(int imageIndexBound) {
        SheildPowerup.imageIndexBound = imageIndexBound;
    }

    public static void setImagePosition(int imagePosition) {
        SheildPowerup.imagePosition = imagePosition;
    }
    
    public void drawOnTank(Graphics g, ImageObserver obs, String color, int x, int y)
    {
        if(color == "red")
            g.drawImage(redSheildImage, x-8, y-8, obs);
        
        else
            g.drawImage(blueSheildImage, x-8, y-8, obs);
    }

    public static void setRedSheildImage(Image redSheildImage) {
        SheildPowerup.redSheildImage = redSheildImage;
    }

    public static void setBlueSheildImage(Image blueSheildImage) {
        SheildPowerup.blueSheildImage = blueSheildImage;
    }

    public static void setSheildIconImage(Image sheildIconImage) {
        SheildPowerup.sheildIconImage = sheildIconImage;
    }
    
    @Override
    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            if (!isInFlux()) {
                g.drawImage(((ToolkitImage) img).getBufferedImage().getSubimage(imageIndexBound * imagePosition, 0, imageIndexBound, imageIndexBound), x, y, obs);
            } else {
                fluxCounter++;
            }
        }
    }
    
    public void draw(Graphics g, ImageObserver obs, int x, int y) {
        g.drawImage(((ToolkitImage) img).getBufferedImage().getSubimage(imageIndexBound * imagePosition, 0, imageIndexBound, imageIndexBound), x, y, obs);
    }
    
    @Override
    protected boolean isCollectable() {
        return this.show && !isInFlux();
    }

    public boolean isInFlux() {
        return fluxCounter < fluxTime;
    }

}
//"Powerup , Sheild,1"
