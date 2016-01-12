/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Graphics;
import java.awt.Image;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.Observable;

/**
 *
 * @author Kenneth
 */
public class Block
{
    protected int x;
    protected int y;
    private static Image permBlockImage;
    protected boolean show;
    protected java.awt.geom.Rectangle2D bounding;

    public Block(int x, int y) {
        this.x = x;
        this.y = y;
        this.show = true;
        
        bounding = new Rectangle2D.Double(x, y, permBlockImage.getWidth(null), permBlockImage.getHeight(null));
    }
    
    public void draw(Graphics g, ImageObserver obs) {
        if (show) {
            g.drawImage(permBlockImage, x, y, obs);
        }
    }
    
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
    
    public static int getWidth() {
        return permBlockImage.getWidth(null);
    }

    public static int getHeight() {
        return permBlockImage.getHeight(null);
    }

    public Rectangle2D getBounding() {
        return bounding;
    }
    
    

    public static Image getPermBlockImage() {
        return permBlockImage;
    }

    public static void setPermBlockImage(Image img) {
        Block.permBlockImage = img;
    }
   
    public boolean isShown() {
        return show;
    }

    @Override
    public String toString() {
        return "Block{" + "x=" + x + ", y=" + y + ", show=" + show + '}';
    }
    
    
}
