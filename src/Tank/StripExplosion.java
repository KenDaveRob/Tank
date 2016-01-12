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
public class StripExplosion extends Animation
{
    private int explosionType;
    private int cycleTimes;
    private int cycleCounter;
    private int imageIndexBound;
    private Image imageFrame;
    private Controler control;

    public StripExplosion(Controler control, int x, int y, int rate, int frameMax, int cycleNumber, int imageIndexBound, String resourceName) 
    {
        super(x, y, resourceName, rate, frameMax);
        this.imageIndexBound = imageIndexBound;
        this.explosionType = explosionType;
        this.control = control;
        this.cycleTimes = cycleNumber;
        this.cycleCounter = 0;
        this.img = ((Tank)this.control.workingApplet).getSprite(resourceName);
        setImage(0);
    }

    private void setImage(int frame) 
    {
        imageFrame = ((sun.awt.image.ToolkitImage)img).getBufferedImage().getSubimage(imageIndexBound*frame, 0, imageIndexBound, imageIndexBound);
    }

    public void incrementAnimation() {
        counter++;

        
        if (counter % rate == 0) 
        {
            if ((counter / rate) + 1 <= frameMax) 
            {
                try 
                {
                    setImage((counter / rate));
                } 
                catch (Exception e) {
                    System.err.println("Exception trying to setImage() in incrementAnimation(), e = " + e.toString());
                }
            } else if (cycleCounter < cycleTimes) {
                cycleCounter++;
                counter = 0;
            } else {
                show = false;
            }
        }
        
    }
    
    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            g.drawImage(imageFrame, x, y, obs);
        }
    }
}
