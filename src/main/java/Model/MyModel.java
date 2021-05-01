package Model;

import Client.*;
import IO.MyDecompressorInputStream;
import Server.*;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.Solution;
import javafx.scene.input.KeyCode;
import org.apache.log4j.FileAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.file.NoSuchFileException;
import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

public class MyModel extends Observable implements IModel {
    private Server mazeGeneratingServer;
    private Server mazeSolvingServer;
    private  Logger generatorLog;
    private  Logger solverLogger;
    
    public MyModel(){
        mazeGeneratingServer=new Server(5400,100,new ServerStrategyGenerateMaze());
        mazeSolvingServer=new Server(5401,100,new ServerStrategySolveSearchProblem());
        mazeGeneratingServer.start();
        mazeSolvingServer.start();
        generatorLog=Logger.getLogger("a");
        solverLogger=Logger.getLogger("b");
        SimpleLayout layout=new SimpleLayout();
        String path=System.getProperty("user.dir")+"/logs";
        FileAppender generateFile=null,solveFile=null;
        try {
            generateFile=new FileAppender(layout,path+"/generateMazeLogFile.txt");
        } catch(IOException e) {
            e.printStackTrace();
        }
        generatorLog.addAppender(generateFile);
        generatorLog.setLevel(Level.INFO);
        try {
            solveFile=new FileAppender(layout,path+"/solveMazeLogFile.txt");
        } catch(IOException e) {
            e.printStackTrace();
        }
        solverLogger.addAppender(solveFile);
        solverLogger.setLevel(Level.INFO);
    }
    
    @Override
    public void generateMaze(int rows, int columns) throws UnknownHostException {
        AtomicReference<Maze> maze = new AtomicReference<>();
        Client client = new Client(InetAddress.getLocalHost(), 5400, (inFromServer, outToServer) -> {
            try {
                ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                toServer.flush();
                int[] mazeDimensions = new int[]{rows,columns};
                toServer.writeObject(mazeDimensions); //send maze dimensions to server
                toServer.flush();
                byte[] compressedMaze = (byte[]) fromServer.readObject(); //read generated maze (compressed with MyCompressor) from server
                InputStream is = new MyDecompressorInputStream(new ByteArrayInputStream(compressedMaze));
                byte[] decompressedMaze = new byte[rows*columns+50 /*CHANGE SIZE ACCORDING TO YOU MAZE SIZE*/]; //allocating byte[] for the decompressed maze -
                is.read(decompressedMaze); //Fill decompressedMaze with bytes
                maze.set(new Maze(decompressedMaze));
                
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        client.communicateWithServer();
        Maze m=maze.get();
        int[][]mazeMat=m.getMaze();
        String message=("IP of client:"+InetAddress.getLocalHost()+" Start Position:"+m.getStartPosition().toString()+" end position:"+m.getGoalPosition().toString()+" rows:"+mazeMat.length+" columns:"+mazeMat[0].length);
        writelog(generatorLog,Level.INFO,message);
        setChanged();
        notifyObservers(m);
    }
    
    @Override
    public void saveMaze(File saveFile, Maze toWrite, Position currentPosition) throws IOException {
        ObjectOutputStream mazeWriter=new ObjectOutputStream(new FileOutputStream(saveFile));
        mazeWriter.writeObject(toWrite);
        mazeWriter.writeObject(currentPosition);
        mazeWriter.close();
    }
    
    
    @Override
    public void loadMaze(File f){
        Maze maze;
        Position p;
        try {
        ObjectInputStream mazeReader=new ObjectInputStream(new FileInputStream(f));
        maze= (Maze) mazeReader.readObject();//read maze
        p=(Position)mazeReader.readObject();//read position
        mazeReader.close();
        } catch(IOException | ClassNotFoundException e) {
            setChanged();
            notifyObservers(new NoSuchFileException(""));//notify observers if wrong file was selected
            return;
        }
        Object[] arr=new Object[]{maze,p};//save the maze and position to array
        setChanged();
        notifyObservers(arr);
    }
    
    @Override
    public void exit() {
        mazeGeneratingServer.stop();
        mazeSolvingServer.stop();
     }
    
    @Override
    public void solveMaze(Maze maze) {
        AtomicReference<Solution> solution = new AtomicReference<>();
        try {
            Client client = new Client(InetAddress.getLocalHost(), 5401, (inFromServer, outToServer) -> {
                try {
                    ObjectOutputStream toServer = new ObjectOutputStream(outToServer);
                    ObjectInputStream fromServer = new ObjectInputStream(inFromServer);
                    toServer.flush();
                    toServer.writeObject(maze); //send maze to server
                    toServer.flush();
                    solution.set((Solution) fromServer.readObject()); //read generated maze (compressed with MyCompressor) from server
                    
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
            client.communicateWithServer();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
        Object[] arr=new Object[]{solution.get(),maze};//save maze and solution to array
        String message= null;
        ArrayList<AState> solArr=solution.get().getSolutionPath();
        try {
            message = ("IP of client:"+ InetAddress.getLocalHost()+" number of steps in solution:"+solArr.size());
        } catch(UnknownHostException e) {
            e.printStackTrace();
        }
        writelog(solverLogger,Level.INFO,message);
        setChanged();
        notifyObservers(arr);
    }
    
    @Override
    public void changeKeys(KeyCode[] arr) {
        HashMap<String,KeyCode> keys=getKeys();
        Properties properties=null;
        try {
            properties=loadProperties();
        } catch(IOException e) {
            e.printStackTrace();
        }
        //check keys and change them if necessary
        if(arr[0]!=keys.get("UP")){
            properties.setProperty("UP",arr[0].getName());
        }
        if(arr[1]!=keys.get("DOWN")){
            properties.setProperty("DOWN",arr[1].getName());
        }
        if(arr[2]!=keys.get("LEFT")){
            properties.setProperty("LEFT",arr[2].getName());
        }
        if(arr[3]!=keys.get("RIGHT")){
            properties.setProperty("RIGHT",arr[3].getName());
        }
        if(arr[4]!=keys.get("Diagonal Left Up")){
            properties.setProperty("Diagonal Left Up",arr[4].getName());
        }
        if(arr[5]!=keys.get("Diagonal Right Up")){
            properties.setProperty("Diagonal Right Up",arr[5].getName());
        }
        if(arr[6]!=keys.get("Diagonal Left Down")){
            properties.setProperty("Diagonal Left Down",arr[6].getName());
        }
        if(arr[7]!=keys.get("Diagonal Right Down")){
            properties.setProperty("Diagonal Right Down",arr[7].getName());
        }
        FileOutputStream out= null;
        try {
            out = new FileOutputStream(System.getProperty("user.dir")+"/resources/config.properties");
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        try {
            properties.store(out,null);//save the keys to config file
        } catch(IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * A function that load the keys from config file
     * @return hash map containing the keys
     */
    private HashMap<String,KeyCode> getKeys(){
        HashMap<String,KeyCode> keys=new HashMap<>();
        Properties properties=null;
        try {
            properties=loadProperties();
        } catch(IOException e) {
            e.printStackTrace();
        }
        keys.put("UP",KeyCode.valueOf(properties.getProperty("UP")));
        keys.put("DOWN",KeyCode.valueOf(properties.getProperty("DOWN")));
        keys.put("RIGHT",KeyCode.valueOf(properties.getProperty("RIGHT")));
        keys.put("LEFT",KeyCode.valueOf(properties.getProperty("LEFT")));
        keys.put("Diagonal Left Up",KeyCode.valueOf(properties.getProperty("Diagonal Left Up")));
        keys.put("Diagonal Right Up",KeyCode.valueOf(properties.getProperty("Diagonal Right Up")));
        keys.put("Diagonal Left Down",KeyCode.valueOf(properties.getProperty("Diagonal Left Down")));
        keys.put("Diagonal Right Down",KeyCode.valueOf(properties.getProperty("Diagonal Right Down")));
        return keys;
    }
    
    public void keyPressed(KeyCode code,Maze m,Position currentPosition) {
        int row = currentPosition.getRowIndex();
        int column = currentPosition.getColumnIndex();
        HashMap<String,KeyCode> keys=getKeys();
        if(keys.containsValue(code)){
            int[] result=checkDirection(code,row,column,m,keys);//check if key and move valid
            if(result[0]==-1 && result[1]==-1) {//if not
                setChanged();
                notifyObservers(new Position(-1,-1));
                return;
            }
            if(result[0]!=-1){//if row index was changed
                row=result[0];
            }
            if(result[1]!=-1){//if column index was changed
                column=result[1];
            }
            currentPosition = new Position(row, column);
            setChanged();
            notifyObservers(currentPosition);//notify observers for new positin
        }
        
    }
    
    /**
     * A method that checks if a key and a move is valid
     * @param event the key that was pressed
     * @param row current row
     * @param column current column
     * @param m maze
     * @param keys hash map of keys
     * @return an array with the new row and column index.If movw wasn't valid return {-1,-1}
     */
    private int[] checkDirection(KeyCode event,int row,int column,Maze m,HashMap<String,KeyCode> keys) {
        int[][] mazeMatrix=m.getMaze();
        int[] result={-1,-1};
        String direction="";
        if(!keys.containsValue(event)){
            return result;
        }
        for(String s:keys.keySet()){//get the direction
            if(keys.get(s)==event){
                direction=s;
                break;
            }
        }
        switch(direction) {
            case "UP":
                //check cell up
                if(row - 1 >= 0 && mazeMatrix[row - 1][column] == 0) {
                    result[0] = row - 1;
                }
                break;
            case "LEFT":
                //check cell left
                if(column - 1 >= 0 && mazeMatrix[row][column - 1] == 0) {
                    result[1] = column - 1;
                }
                break;
            case "DOWN":
                //check cell down
                if(row + 1 < mazeMatrix.length && mazeMatrix[row + 1][column] == 0) {
                    result[0] = row + 1;
                }
                break;
            case "RIGHT":
                //check cell right
                if(column + 1 < mazeMatrix[0].length && mazeMatrix[row][column + 1] == 0) {
                    result[1] = column + 1;
                }
                break;
            case "Diagonal Left Up":
                //check diagonal up left cell
                if(row - 1 >= 0 && column - 1 >= 0 && mazeMatrix[row - 1][column - 1] == 0 && (mazeMatrix[row][column - 1] == 0 || mazeMatrix[row - 1][column] == 0)) {
                    result[0] = row - 1;
                    result[1] = column - 1;
                }
                break;
            case "Diagonal Right Up":
                //check diagonal up right cell
                if(row - 1 >= 0 && column + 1 < mazeMatrix[0].length && mazeMatrix[row - 1][column + 1] == 0 && (mazeMatrix[row][column + 1] == 0 || mazeMatrix[row - 1][column] == 0)) {
                    result[0] = row - 1;
                    result[1] = column + 1;
                }
                break;
            case "Diagonal Left Down":
                //check diagonal down left cell
                if(row + 1 < mazeMatrix.length && column - 1 >= 0 && mazeMatrix[row + 1][column - 1] == 0 && (mazeMatrix[row][column - 1] == 0 || mazeMatrix[row + 1][column] == 0)) {
                    result[0] = row + 1;
                    result[1] = column - 1;
                }
                break;
            default:
                //check diagonal right down cell
                if(row + 1 < mazeMatrix.length && column + 1 < mazeMatrix[0].length && mazeMatrix[row + 1][column + 1] == 0 && (mazeMatrix[row + 1][column] == 0 || mazeMatrix[row][column + 1] == 0)) {
                    result[0] = row + 1;
                    result[1] = column + 1;
                }
                break;
        }
        return result;
    }
    
    public void initKeys() throws IOException {
        Properties properties=loadProperties();
        properties.setProperty("UP", "NUMPAD2");
        properties.setProperty("DOWN","NUMPAD8");
        properties.setProperty("LEFT","NUMPAD4");
        properties.setProperty("RIGHT","NUMPAD6");
        properties.setProperty("Diagonal Left Up","NUMPAD1");
        properties.setProperty("Diagonal Right Up","NUMPAD3");
        properties.setProperty("Diagonal Left Down","NUMPAD7");
        properties.setProperty("Diagonal Right Down","NUMPAD9");
        FileOutputStream out=new FileOutputStream(System.getProperty("user.dir")+"/resources/config.properties");
        properties.store(out,null);
    }
    
    private Properties loadProperties() throws IOException {
        Properties properties=new Properties();
        FileInputStream inputStream=new FileInputStream(System.getProperty("user.dir")+"/resources/config.properties");
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }
    
    @Override
    public void writelog(Logger logger, Level level, String message) {
        if(level.equals(Level.INFO)){//if info
            logger.info(message);
            return;
        }
        if(level.equals(Level.ERROR)){//if error
            logger.error(message);
        }
    }
    
    @Override
    public Logger getMazeGeneratorLogger() {
        return this.generatorLog;
    }
    
    @Override
    public void assignObserver(Observer o) {
        addObserver(o);
    }
    
}
