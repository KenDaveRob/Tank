/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;


/**
 *
 * @author Kenneth
 */
//This class was quite a pain to get working correctly
public class Soundtrack implements Runnable
{
    private Thread thread; 
    private boolean continueLooping = true;
    private AudioStream song;
    private String songFileName;

    public Soundtrack(String songFileName) {
        this.songFileName = songFileName;
    }
    
    
    
    
    public void start() {
        thread = new Thread(this);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }
    
    public void run()
    {
        try {
            song = new AudioStream(getClass().getResourceAsStream(songFileName));
        } catch (IOException ex) {
            Logger.getLogger(Soundtrack.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        while (continueLooping) {
            AudioPlayer.player.start(song);


            try {
                //This polling allows the music to be suspended before the next loop
                for (int i = 0; i < 1000; i++) {
                    if (!continueLooping) {
                        AudioPlayer.player.stop(song);
                        break;
                    }
                    this.thread.sleep(69);
                }


            } catch (InterruptedException ex) {
                Logger.getLogger(Soundtrack.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    public boolean isContinueLooping() {
        return continueLooping;
    }

    public void setContinueLooping(boolean continueLooping) {
        this.continueLooping = continueLooping;
    }
    
    
}
