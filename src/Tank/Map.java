/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Tank;

import java.awt.Image;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;

/**
 *
 * @author Kenneth
 */
public class Map 
{
    private HashMap<Integer, HashMap<Integer, Block>> blocks;
    private LinkedList<Powerup> powerups;
    private BufferedReader mapSource;
    private int mapHeight;
    private int mapWidth;
    private static Controler control; //Had some issues with passing in the controler through the constructor
    private static int sheildPowerupDuration = 400;
    private static int rocketPowerupNumber = 10;

    public Map(int mapWidth, int mapHeight) 
    {
        this.mapHeight = mapHeight/Block.getHeight();
        this.mapWidth = mapWidth/Block.getWidth();
        blocks = new HashMap<Integer, HashMap<Integer, Block>>();
        powerups = new LinkedList<Powerup>();
        
        for(int y = 0; y < mapHeight; y++)
            blocks.put(y, new HashMap<Integer, Block>());
        
    }
    
    public HashMap<Integer, Block> getBlockRow(int row)
    {
        return blocks.get(row);
    }
    
    public LinkedList<Powerup> getPowerups()
    {
        return powerups;
    }
    
    private void createHorizontalBlockWall(int initialX, int initialY, int number)
    {        
        for(int i = 0; i < number; i++)
        {
            Block newBlock = new Block((initialX+i)*32, initialY*32);
            blocks.get(initialY).put((initialX+i), newBlock); 
        }        
    }
    
    private void createHorizontalDestructableBlockWall(int initialX, int initialY, int number)
    {
        for(int i = 0; i < number; i++)
        {
            DestructableBlock newBlock = new DestructableBlock(control, (initialX+i)*32, initialY*32, 2);
            control.gameEvents.addObserver(newBlock);
            blocks.get(initialY).put((initialX+i), newBlock); 
        }       
    }
    
    private void createVerticalBlockWall(int initialX, int initialY, int number)
    {        
        for(int i = 0; i < number; i++)
        {
            Block newBlock = new Block(initialX*32, (initialY+i)*32);
            blocks.get((initialY+i)).put(initialX, newBlock);
            
        }        
    }
    
    private void createVerticalDestructableBlockWall(int initialX, int initialY, int number)
    {        
        for(int i = 0; i < number; i++)
        {
            DestructableBlock newBlock = new DestructableBlock(control, (initialX)*32, (initialY+i)*32, 2);
            control.gameEvents.addObserver(newBlock);
            blocks.get((initialY+i)).put(initialX, newBlock);
            
        }        
    }
    
    private void createSheildPowerup(int x, int y)
    {
        SheildPowerup newSheild = new SheildPowerup(x, y, 0, sheildPowerupDuration, control, ", Sheild,");
        this.powerups.add(newSheild);
    }
    
    private void createRocketPowerup(int x, int y)
    {
        GuidedRocketPowerup newGuidedRocket = new GuidedRocketPowerup(x, y, 0, rocketPowerupNumber, control, ", Rocket,");
        this.powerups.add(newGuidedRocket);
    }
    
    
    public void loadMap(Object scenarioFile) throws Exception
    {
        String currentLine;
        List<String> parts;
        List<String> instructions;
        ArrayList<Block> nextMapRow;
        
        try
        {

            //Accepts Object so that it can open files online or on a computer
            if(scenarioFile.getClass() == File.class)
                mapSource = new BufferedReader(new FileReader((File)scenarioFile));
            else if(scenarioFile.getClass() == URL.class)
                mapSource = new BufferedReader(new InputStreamReader(((URL)scenarioFile).openStream()));
            
            else
                throw new Exception("Error: Scenario File is neither a URL or a local File.");
            
            while((currentLine = mapSource.readLine()) != null)
            {
                //Blank lines get ignored
                if(!currentLine.equals(""))
                {
                    nextMapRow = new java.util.ArrayList<Block>();
                    instructions = Arrays.asList(currentLine.split(";"));

                    for(String instruction : instructions)
                    {
                        parts = Arrays.asList(instruction.split(","));
                        
                        if(parts.get(0).equals("createHorizontalWall"))
                        {
                            if(parts.get(1).equals("Block"))
                            {
                                createHorizontalBlockWall(Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)), Integer.parseInt(parts.get(4)));
                            }
                            else if(parts.get(1).equals("DestructableBlock"))
                            {
                                createHorizontalDestructableBlockWall(Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)), Integer.parseInt(parts.get(4)));
                            }
                        }
                        
                        else if(parts.get(0).equals("createVerticalWall"))
                        {
                            if(parts.get(1).equals("Block"))
                            {
                                createVerticalBlockWall(Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)), Integer.parseInt(parts.get(4)));
                            }
                            else if(parts.get(1).equals("DestructableBlock"))
                            {
                                createVerticalDestructableBlockWall(Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)), Integer.parseInt(parts.get(4)));
                            }
                        }
                        
                        else if(parts.get(0).equals("createPowerup"))
                        {
                            if(parts.get(1).equals("Sheild"))
                            {
                                createSheildPowerup(Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)));
                            }
                            
                            else if(parts.get(1).equals("Rocket"))
                            {
                                createRocketPowerup(Integer.parseInt(parts.get(2)), Integer.parseInt(parts.get(3)));
                            }
                        }
                    }
                }
            }
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
        
        finally
        {
            try
            {
                mapSource.close();
            }
            catch(IOException ex)
            {
                ex.printStackTrace();
            }
        }
    }

    public int getMapHeight() {
        return mapHeight;
    }

    public int getMapWidth() {
        return mapWidth;
    }

    public static void setControl(Controler control) {
        Map.control = control;
    }

    public static void setSheildPowerupDuration(int sheildPowerupDuration) {
        Map.sheildPowerupDuration = sheildPowerupDuration;
    }

    public static void setRocketPowerupNumber(int rocketPowerupNumber) {
        Map.rocketPowerupNumber = rocketPowerupNumber;
    }
}
