package View;

import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.AState;
import algorithms.search.MazeState;
import algorithms.search.Solution;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.input.ScrollEvent;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.scene.paint.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class MazeDrawer extends Canvas{
    private Maze mazeObj;
    private final GraphicsContext graphicsContext = getGraphicsContext2D();
    private final ICharacter character;
    private double cellHeight;
    private double cellWidth;
    private Position currentPosition;
    private Color[][] board;//an array that holds for each position the color in it
    private Solution solution=null;
    private static MediaPlayer mediaPlayer;
        
    public MazeDrawer(Maze m, ICharacter c,Position p) {
        super(600,400);
        widthProperty().addListener(evt -> draw());
        heightProperty().addListener(evt ->draw());
        mazeObj = m;
        character = c;
        if(p==null){
            currentPosition=m.getStartPosition();
        }
        else{
            currentPosition=p;
        }
      }
    
   public boolean isResizable(){
        return true;
   }
    
    @Override
    public double prefHeight(double width) {
        return getHeight();
    }
    
    @Override
    public double prefWidth(double height) {
        return getWidth();
    }
    
    /**
     * Function that draw the maze
     */
    public void draw(){
        int[][] maze = mazeObj.getMaze();
        double canvasHeight = getHeight();
        double canvasWidth = getWidth();
        int row = maze.length;
        int col = maze[0].length;
        board=new Color[row][col];
        cellHeight = canvasHeight / row;
        cellWidth = canvasWidth / col;
        graphicsContext.clearRect(0, 0, canvasWidth, canvasHeight);
        graphicsContext.setFill(Color.WHITE);
        double x, y;
        String pathToResources = System.getProperty("user.dir") + "/resources";
        FileInputStream imageWall = null;
        try {
            imageWall = new FileInputStream(pathToResources + "/Wall.jpg");
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        Image wall = new Image(imageWall, cellWidth, cellHeight, false, false);
        for(int i = 0; i < row; i++) {
            for(int j = 0; j < col; j++) {
                y = i * cellHeight;
                x = j * cellWidth;
                if(maze[i][j] == 1) // Wall
                {
                    graphicsContext.drawImage(wall,x,y,cellWidth,cellHeight);//draw wall
                }
                else {
                    graphicsContext.fillRect(x,y, cellWidth,cellHeight);//draw free space
                    board[i][j]=Color.WHITE;
                }
            }
        }
        try {
            imageWall.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
        updateStartAndEndPosition();
        drawCharacter();
    }
    
    /**
     * A function that draw the start and end
     */
    private void updateStartAndEndPosition(){
        Position start=mazeObj.getStartPosition();
        board[start.getRowIndex()][start.getColumnIndex()]=Color.LIMEGREEN;//update color arr in start postion
        double startRowIndex=start.getRowIndex()*cellHeight;
        double startColumnIndex=start.getColumnIndex()*cellWidth;
        graphicsContext.setFill(Color.LIMEGREEN);
        graphicsContext.fillRect(startColumnIndex,startRowIndex,cellWidth,cellHeight);//draw start
        Position end = mazeObj.getGoalPosition();
        board[end.getRowIndex()][end.getColumnIndex()]=Color.BLACK;//update color arr in end position
        double endRowIndex = end.getRowIndex() * cellHeight;
        double endColumnIndex = end.getColumnIndex() * cellWidth;
        graphicsContext.setFill(Color.BLACK);
        graphicsContext.fillRect(endColumnIndex,endRowIndex, cellWidth,cellHeight);//draw end
    }
    
    /**
     * A function that draws the character
     */
    public void drawCharacter() {
        double startRow = currentPosition.getRowIndex() * cellHeight;
        double startColumn = currentPosition.getColumnIndex() * cellWidth;
        character.drawCharacter(graphicsContext, startColumn,startRow, cellHeight, cellWidth);
    }
    
    
    /**
     * A function that play sounds(error sound or finish sound)
     * @param path
     */
    private void playSound(String path) {
        Media sound = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(sound);
        mediaPlayer.setAutoPlay(true);
    }
    
    /**
     * A function that act when user ariived to end
     */
    private void succeededMaze(){
        String path=System.getProperty("user.dir")+"/resources/applause3.mp3";
        playSound(path);
        Alert alert=new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText("Well Done!");
        alert.show();
      }
    
    
    /**
     * A function that redraw the character
     * @param newPosition new position to redraw
     */
    public void redrawTheCharacter(Position newPosition) {
        if(newPosition.getRowIndex()==-1 && newPosition.getColumnIndex()==-1){//if not valid position
            String path=System.getProperty("user.dir")+"/resources/ErrorSound.mp3";
            playSound(path);
            return;
        }
        double row = currentPosition.getRowIndex() * cellHeight;
        double column = currentPosition.getColumnIndex() * cellWidth;
        graphicsContext.clearRect(column,row,cellWidth,cellHeight);
        graphicsContext.setFill(board[currentPosition.getRowIndex()][currentPosition.getColumnIndex()]);
        graphicsContext.fillRect(column,row, cellWidth,cellHeight);
        currentPosition=newPosition;
        drawCharacter();
        if(newPosition.equals(mazeObj.getGoalPosition())){//if finish position
            succeededMaze();
        }
    }
    
    /**
     * A function that draws the solution
     * @param sol
     */
    public void drawSolution(Solution sol) {
        if(solution==null){
            solution=sol;
        }
        else{
            drawSolutionHelper(Color.WHITE);
            solution=sol;
        }
        drawSolutionHelper(Color.TOMATO);
    }
    
    /**
     * A function that removes a solution that was drown
     */
    public void removeSolution(){
        if(solution!=null){
            drawSolutionHelper(Color.WHITE);
            solution=null;
        }
        else{//show error message
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Cannot remove a non existing solution");
            alert.show();
        }
     }
    
    private void drawSolutionHelper(Color color){
        ArrayList<AState> steps = solution.getSolutionPath();
        for(AState step : steps) {
            Position pos = ((MazeState) step).getCurrentPosition();
            if(pos.equals(currentPosition) || pos.equals(mazeObj.getGoalPosition()) || pos.equals(mazeObj.getStartPosition())){
                continue;
            }
            board[pos.getRowIndex()][pos.getColumnIndex()]=color;
            double x=pos.getColumnIndex()*cellWidth;
            double y=pos.getRowIndex()*cellHeight;
            if(color==Color.WHITE){
                graphicsContext.clearRect(x,y,cellWidth,cellHeight);
            }
            graphicsContext.setFill(color);
            graphicsContext.fillRect(x,y, cellWidth, cellHeight);
        }
    }
    
    /**
     * zoom listener
     * @param event zoom event
     * @param zoomFactor factor to zoom in
     */
    public void zoom(ScrollEvent event,double zoomFactor) {
        if(event.isControlDown()) {
           event.consume();
           setScaleX(getScaleX()*zoomFactor);
           setScaleY(getScaleY()*zoomFactor);
            }
    }
    
    public Position getCurrentPosition() {
        return currentPosition;
    }
    
    public Maze getMazeObj() {
        return mazeObj;
    }
    
    public void setMaze(Maze m,Position p){
        mazeObj=m;
        if(p==null){
            currentPosition=m.getStartPosition();
        }
        else{
            currentPosition=p;
        }
        solution=null;
    }
}
