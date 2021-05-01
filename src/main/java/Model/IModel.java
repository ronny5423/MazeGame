package Model;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.Observer;

public interface IModel{
    /**
     * This fnction generates new maze
     * @param rows rows
     * @param columns columns
     * @throws UnknownHostException
     */
    void generateMaze(int rows,int columns) throws UnknownHostException;
    
    /**
     * This method saves a maze to file
     * @param f file to save to
     * @param m maze to save
     * @param currentPosition last position of the character to save also to file
     * @throws IOException
     */
    void saveMaze(File f, Maze m, Position currentPosition) throws IOException;
    
    /**
     * A method that loads a maze into a given file
     * @param f file to read maze and position from
     */
    void loadMaze(File f);
    
    /**
     * A method that closes the servers before exit
     */
    void exit();
    
    /**
     * A method that gets a maze and solve it
     * @param maze maze to solve
     */
    void solveMaze(Maze maze);
    
    /**
     * A method that changes the keys
     * @param arr array that include all the keys to change
     */
    void changeKeys(KeyCode[] arr);
    
    void assignObserver(Observer o);
    
    /**
     * A method that ckecks that if the key that was pressed is valid and if a move can be done
     * @param code key that was pressed
     * @param m maze
     * @param current current position
     */
    void keyPressed(KeyCode code,Maze m,Position current);
    
    /**
     * A method that inits all the keys in the begining of the game
     * @throws IOException
     */
    void initKeys() throws IOException;
    
    /**
     * This method writes log
     * @param logger logger to write with
     * @param level levl of log message
     * @param message message to write
     */
    void writelog(Logger logger, Level level, String message);
    
    /**
     * Getter for maze generator logger
     * @return maze generator logger
     */
    Logger getMazeGeneratorLogger();
    }
