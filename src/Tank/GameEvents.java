/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.event.KeyEvent;
import java.util.Observable;

/**
 *
 * @author Kenneth
 */
public class GameEvents extends Observable {

        int type;
        Object event;
        Object cause;

        public void setValue(KeyEvent e) {
            type = 1; // let's assume this mean key input. Should use CONSTANT value for this
            event = e;
            setChanged();
            // trigger notification
            notifyObservers(this);
        }

        public void setValue(String msg) 
        {
            type = 2; // let's assume this mean key input. Should use CONSTANT value for this
            event = msg;  
            setChanged();
            // trigger notification
            notifyObservers(this);
        }
        
        public void setValue(String msg, Object cause) 
        {
            type = 2; // let's assume this mean key input. Should use CONSTANT value for this
            event = msg;  
            this.cause = cause;
            setChanged();
            // trigger notification
            notifyObservers(this);
        }
        
        //This is used to indicate a key has been released
        public void setValueReleased(KeyEvent e)
        {
            type = 3; // Released value
            event = e;
            setChanged();
            // trigger notification
            notifyObservers(this);
        }
        
        public void setValueBulletHitEnemy(Enemy hitEnemy, Object cause) 
        {
            type = 4;
            event = hitEnemy;
            this.cause = cause;
            setChanged();
            // trigger notification
            notifyObservers(this);
        }
        
        public void setValueBulletHitBlock(Block hitBlock, Object cause)
        {
            type = 4;
            event = hitBlock;
            this.cause = cause;
            setChanged();
            // trigger notification
            notifyObservers(this);
        }
        
                
        
        
        
    }
