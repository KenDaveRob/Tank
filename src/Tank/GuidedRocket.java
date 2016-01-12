/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.ImageObserver;

/**
 *
 * @author Kenneth
 */
public class GuidedRocket extends Shell
{
    private static Image rocketStrip;
    private static int desiredShellSpeedBoost;
    private static int imageIndexBound;
    private static int desiredRocketSpeedBoost;
    
    public GuidedRocket(Controler control, int x, int y, int xStep, int yStep, int strength, int angle, Player creator) {
        super(control, x, y, xStep, yStep, strength, angle, creator);
    }
    
    @Override
    public void draw(Graphics g, ImageObserver obs) 
    {        
        if (show)
        {
            int imageIndex = ((360-angle) / 6);     

            imageIndex %= 60;

            g.drawImage(((sun.awt.image.ToolkitImage)rocketStrip).getBufferedImage().getSubimage(imageIndexBound*imageIndex, 0, imageIndexBound, imageIndexBound),
                    x, y, obs);
        }
    }
    
    public static Image getRocketStrip() {
        return rocketStrip;
    }

    public static void setRocketStrip(Image shellStrip) {
        GuidedRocket.rocketStrip = shellStrip;
    }
    public static int getImageIndexBound() {
        return imageIndexBound;
    }

    public static void setImageIndexBound(int imageIndexBound) {
        GuidedRocket.imageIndexBound = imageIndexBound;
    }

    public static void setDesiredRocketSpeedBoost(int desiredShellSpeedBoost) {
        GuidedRocket.desiredRocketSpeedBoost = desiredShellSpeedBoost;
    }

    public static int getDesiredRocketSpeedBoost() {
        return desiredRocketSpeedBoost;
    }
    
}
