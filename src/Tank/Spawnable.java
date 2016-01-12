/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

/**
 *
 * @author Kenneth
 */
//This is little more than a placeholder for both enemies and powerups
public interface Spawnable 
{
    public int getX();

    public int getY();

    public boolean isShown();
    
    public void tick(int w, int h, GameEvents gameEvents) throws NullPointerException;
}
