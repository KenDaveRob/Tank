/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import static Tank.Bullet.largeImg;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.awt.image.ImageObserver;
import java.util.HashMap;

/**
 *
 * @author Kenneth
 */
class Shell extends Bullet
{
    private static Image shellStrip;
    private static int imageIndexBound;
    private static int desiredShellSpeedBoost;
    private Player creator;
    
    public Shell(Controler control, int x, int y, int xStep, int yStep, int strength, int angle, Player creator) {
        super(control, x, y, xStep, yStep, strength, angle);
        this.creator = creator;
    }
    
    @Override
    public void tick(int w, int h)
    {
        x += xStep;
        y += yStep;
        
        int i = 1;
        for (Player currentPlayer : control.players) 
        {
            if(currentPlayer != creator && ((TankPlayer)currentPlayer).collision(new Rectangle.Double(x, y, imageIndexBound, imageIndexBound))) 
            {
                control.gameEvents.setValue("ExplosionSmall " + i, this);
                show = false;
            }
            i++;
        }
        
        for(i = 0; i < control.worldMap.getMapWidth(); i++)
        {
            HashMap<Integer, Block> blockRow = control.worldMap.getBlockRow(i);
            for(int j = 0; j < control.worldMap.getMapHeight(); j++)
            {
                Block currentBlock = blockRow.get(j);
                
                if(currentBlock != null)
                {
                    boolean isBrokenBlock = (DestructableBlock.class == currentBlock.getClass() && ((DestructableBlock)currentBlock).isInFlux());
                    
                    if(!isBrokenBlock && collision(currentBlock.bounding)) 
                    {

                        show = false;
                        control.gameEvents.setValueBulletHitBlock(currentBlock, this);
                        break;
                    }
                }
                
            }
        }
    }
    
    //This function could be improved    
    public boolean collision(java.awt.geom.Rectangle2D bound)
    {
        return bound.intersects(new Rectangle2D.Double(x, y, imageIndexBound, imageIndexBound));
    }
    
    
    @Override
    public void draw(Graphics g, ImageObserver obs) 
    {
        if (show)
        {
            int imageIndex = ((angle) / 6);     

            imageIndex %= 60;

            g.drawImage(((sun.awt.image.ToolkitImage)shellStrip).getBufferedImage().getSubimage(imageIndexBound*imageIndex, 0, imageIndexBound, imageIndexBound),
                    x, y, obs);
        }
    }

    public static Image getShellStrip() {
        return shellStrip;
    }

    public static void setShellStrip(Image shellStrip) {
        Shell.shellStrip = shellStrip;
    }

    public static int getImageIndexBound() {
        return imageIndexBound;
    }

    public static void setImageIndexBound(int imageIndexBound) {
        Shell.imageIndexBound = imageIndexBound;
    }

    public static void setDesiredShellSpeedBoost(int desiredShellSpeedBoost) {
        Shell.desiredShellSpeedBoost = desiredShellSpeedBoost;
    }

    public static int getDesiredShellSpeedBoost() {
        return desiredShellSpeedBoost;
    }
    
    
}