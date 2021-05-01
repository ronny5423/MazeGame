package ViewModel;

import Model.IModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import javafx.scene.input.KeyCode;
import org.apache.log4j.Level;

import java.io.*;
import java.net.UnknownHostException;
import java.security.KeyException;
import java.util.Observable;
import java.util.Observer;

public class MyViewModel extends Observable implements Observer {
    private final IModel model;
    
    public MyViewModel(IModel model){
        this.model=model;
        this.model.assignObserver(this);
    }
    
    /**
     * A function that checks if row and columns are valid and if yes,send them to model to generate maze
     * @param rows rows
     * @param columns columns
     * @return true if rows and columns are valid or false if not
     * @throws UnknownHostException
     */
    public boolean validateInput(String rows,String columns) throws UnknownHostException {
        if(!isValidInput(rows,columns) || rows.equals("") || columns.equals("")){
            String message="Wrong input parameters: rows:"+rows+", columns:"+columns;
            model.writelog(model.getMazeGeneratorLogger(), Level.ERROR, message);
            return false;
        }
        int rowsInteger=Integer.parseInt(rows);
        int columnsInteger=Integer.parseInt(columns);
        model.generateMaze(rowsInteger,columnsInteger);
        return true;
    }
    
    private boolean isValidInput(String rows,String columns){
        return validateIfANumber(rows) && validateIfANumber(columns);
    }
    
    private boolean validateIfANumber(String s){
        if(s.length()==1){//if string contains only one char,check if the char is not 0
            return s.charAt(0) != '0';
        }
        for(int i=0;i<s.length();i++){
            if(s.charAt(i)<48 || s.charAt(i)>57){//check if a char is digit between 0 to 9
                return false;
            }
        }
        return true;
    }
    
    /**
     * A function that transfers to model maze to save to file and the file where to save it
     * @param f file to save to
     * @param m maze to save
     * @param p position to save
     * @throws IOException
     */
    public void saveFile(File f, Maze m, Position p) throws IOException {
        model.saveMaze(f,m,p);
       }
    
    public void loadMaze(File f){
        model.loadMaze(f);
    }
    
    public void exit(){
        model.exit();
    }
    
    public void Solve(Maze m){
        model.solveMaze(m);
    }
    
    public void keyPressed(KeyCode code,Maze m,Position currentPosition){
        model.keyPressed(code,m,currentPosition);
    }
    
    /**
     * A method that checks if all the new keys are different and if yes sends them to odel
     * @param arr arr of keys
     * @return true if different or false if not
     */
    public boolean changeKeys(KeyCode[] arr){
        for(int i=0;i<arr.length;i++){
            for(int j=i+1;j<arr.length;j++){
                if(arr[i]==arr[j]){
                    return false;
                }
            }
        }
        setChanged();
        notifyObservers(Boolean.TRUE);
        model.changeKeys(arr);
        return true;
     }
     
     public void initKeys() throws IOException {
        model.initKeys();
     }
     
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof IModel){
            setChanged();
            notifyObservers(arg);
        }
    }
}
