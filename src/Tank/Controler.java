/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.util.ArrayList;
import java.util.List;
import javax.swing.JApplet;

/**
 *
 * @author Kenneth
 */
public class Controler 
{
        ArrayList<Player> players;
        List<Enemy> enemies;
        java.util.List<Animation> explosions;
        java.util.List<Bullet> bullets;
        Map worldMap;
        GameEvents gameEvents;
        JApplet workingApplet;
        Integer appletWidth;
        Integer appletHeight;
        Integer damageFromRamming;
        ArrayList<Integer> playerScores;
        
        
}
