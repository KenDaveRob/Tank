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
public class Animation {

    protected Image img;
    protected int x, y, height, width;
    protected int counter, rate, frameMax;
    protected boolean show;
    protected String animationName;
    protected javax.swing.JApplet workingApplet;

    public Animation(JApplet workingApplet, int x, int y, String animationName, int rate, int frameMax) {
        this.workingApplet = workingApplet;
        this.animationName = animationName;
        this.x = x;
        this.y = y;
        this.rate = rate;
        this.frameMax = frameMax;
        this.counter = 0;
        this.show = true;
        setImage(1);
    }

    public Animation(int x, int y, String animationName, int rate, int frameMax) 
    {
        this.x = x;
        this.y = y;
        this.rate = rate;
        this.frameMax = frameMax;
        this.animationName = animationName;
        this.counter = 0;
        this.show = true;
    }
    
    

    public void incrementAnimation() {
        counter++;

        if (counter % rate == 0) {
            if ((counter / rate) + 1 <= frameMax) {
                try {
                    setImage((counter / rate) + 1);
                } catch (Exception e) {
                    System.err.println("Exception trying to setImage() in incrementAnimation(), e = " + e.toString());
                }
            } else {
                show = false;
            }

        }
    }

    private void setImage(int frame) {
        String frameName = "Resources/" + animationName + "_" + Integer.toString(frame) + ".png";
        this.img = ((Tank)this.workingApplet).getSprite(frameName);
        width = img.getWidth(null);
        height = img.getHeight(null);
    }

    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            g.drawImage(img, x, y, obs);
        }
    }
}
class Explosion extends Animation {

    private int explosionType;
    private int cycleTimes;
    private int cycleCounter;

    public Explosion(JApplet workingApplet, int x, int y, int explosionType, int rate, int frameMax, int cycleNumber) {
        super(workingApplet,x, y, "explosion" + Integer.toString(explosionType), rate, frameMax);

        this.explosionType = explosionType;
        this.cycleTimes = cycleNumber;
        this.cycleCounter = 1;
    }

    private void setImage(int frame) {
        String frameName = "Resources/explosion" + Integer.toString(explosionType) + "_" + Integer.toString(frame) + ".png";
        this.img = ((Tank)super.workingApplet).getSprite(frameName);
        width = img.getWidth(null);
        height = img.getHeight(null);
    }

    public void incrementAnimation() {
        counter++;

        if (counter % rate == 0) {
            if ((counter / rate) + 1 <= frameMax) {
                try {
                    setImage((counter / rate) + 1);
                } catch (Exception e) {
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
}
