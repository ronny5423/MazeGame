package View;

import ViewModel.MyViewModel;
import algorithms.mazeGenerators.Maze;
import algorithms.mazeGenerators.Position;
import algorithms.search.Solution;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.io.*;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.util.*;


public class MyViewController implements IView, Initializable{
    @FXML private MenuBar menuBar;
    @FXML private Pane pane;
    @FXML private Menu exit;
    @FXML private Menu help;
    @FXML private Menu about;
    @FXML private Label instructions;
    @FXML private Label welcomeMessage;
    @FXML private VBox vbox;
          private MazeDrawer drawer=null;
          private MyViewModel viewModel;
          private static final ArrayList<Stage> stages=new ArrayList<>();//array holding all open stages
          private Stage mainStage;
          private static MediaPlayer mediaPlayer;
          
          
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Label exitText=new Label("Exit");
        exitText.setOnMouseClicked(event -> exitClicked());
        exit.setGraphic(exitText);
        Label helpText=new Label("Help");
        helpText.setOnMouseClicked(event -> helpClicked());
        help.setGraphic(helpText);
        Label aboutText=new Label("About");
        aboutText.setOnMouseClicked(event -> {
            try {
                aboutClicked();
            } catch(IOException e) {
                e.printStackTrace();
            }
        });
        about.setGraphic(aboutText);
        Image img= null;
        try {
            img = new Image(new FileInputStream(System.getProperty("user.dir")+"/resources/Maze Background.jpeg"));
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        BackgroundImage bgImg = new BackgroundImage(img,
                                                    BackgroundRepeat.NO_REPEAT, BackgroundRepeat.NO_REPEAT,
                                                    BackgroundPosition.DEFAULT,
                                                    new BackgroundSize(BackgroundSize.AUTO, BackgroundSize.AUTO, false, false, true, true));
    pane.setBackground(new Background(bgImg));
        vbox.layoutXProperty().bind(pane.widthProperty().subtract(vbox.widthProperty()).divide(2));
        vbox.layoutYProperty().bind(pane.heightProperty().subtract(vbox.heightProperty()).divide(2));
        vbox.prefWidthProperty().bind(pane.widthProperty());
        instructions.prefWidthProperty().bind(vbox.widthProperty());
        welcomeMessage.prefWidthProperty().bind(vbox.widthProperty());
        String path=System.getProperty("user.dir")+"/resources/OpeningMusic.mp3";
        Media openingSound = new Media(new File(path).toURI().toString());
        mediaPlayer = new MediaPlayer(openingSound);
        mediaPlayer.setAutoPlay(true);
        mediaPlayer.setOnEndOfMedia(() -> {
        mediaPlayer.seek(Duration.ZERO);
        mediaPlayer.play();
        });
        
       }
     
    
    
    @FXML
    public void newClicked() throws IOException {
        MazeGenerateWindow.setViewModel(viewModel);
        newStage("/View/MazeGenerateWindow.fxml","Maze Generating Window");
    }
    
    /**
     * A method that starts new stage
     * @param fxmlPath fxm path to load
     * @param windowName the name of the window
     * @throws IOException
     */
    private void newStage(String fxmlPath,String windowName) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource(fxmlPath));
        Stage primaryStage=new Stage();
        primaryStage.setTitle(windowName);
        Scene scene=new Scene(root,600,400);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setOnCloseRequest(event -> stages.remove(primaryStage));
        stages.add(primaryStage);
        
    }
    
    public void saveClicked() throws IOException {
        if(drawer==null){//if no maze to save
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("A maze cannot be saved before it was created");
            alert.setResizable(false);
            alert.show();
            return;
        }
        FileChooser saveFileChooser=new FileChooser();
        FileChooser.ExtensionFilter txt=new FileChooser.ExtensionFilter("TXT files",".txt");
        FileChooser.ExtensionFilter temp=new FileChooser.ExtensionFilter("Temp Files",".tmp");
        saveFileChooser.getExtensionFilters().addAll(txt,temp);
        File saveFile=saveFileChooser.showSaveDialog(null);
        if(saveFile!=null){// if a file to save to was choosen
            viewModel.saveFile(saveFile,drawer.getMazeObj(),drawer.getCurrentPosition());
        }
        
    }
    
    public void loadClicked(){
        FileChooser loadFileChooser=new FileChooser();
        File file=loadFileChooser.showOpenDialog(null);
        
        if(file!=null){// if a file was choosen
            viewModel.loadMaze(file);
            }
      }
      
      private void showAlert(){
          Alert alert=new Alert(Alert.AlertType.ERROR);
          alert.setContentText("A not containing maze file was selected");
          alert.setResizable(false);
          alert.show();
      }
    
    @Override
    public void moveToMazeScene(){
        VBox mainLayout=new VBox();
        ScrollPane scrollPane=new ScrollPane(new Group(drawer));//create scroll pane that holds the maze drawer
        scrollPane.setPannable(true);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToWidth(true);
        scrollPane.setFitToHeight(true);
        
        drawer.setOnScroll(event -> {//set listener of maze drawer on zoom
            double zoomFactor=event.getDeltaY()>0 ? 1.05 : 0.9;
            drawer.zoom(event,zoomFactor);
        });
            
        
            HBox hBox=new HBox();
            VBox buttons=new VBox();
            Button solve=new Button("Solve");//create solve and remove solution buttons
            solve.setOnMouseClicked(event -> {viewModel.Solve(drawer.getMazeObj());});
            Button removeSolution=new Button("Remove Solution");
            removeSolution.setOnMouseClicked(event -> drawer.removeSolution());
            solve.prefWidthProperty().bind(buttons.widthProperty());
            removeSolution.prefWidthProperty().bind(buttons.widthProperty());
            buttons.getChildren().addAll(solve,removeSolution);
            buttons.setSpacing(15);
            hBox.getChildren().addAll(scrollPane,buttons);
           mainLayout.getChildren().addAll(menuBar,hBox);
           hBox.prefHeightProperty().bind(mainLayout.heightProperty());
           hBox.prefWidthProperty().bind(mainLayout.widthProperty());
           buttons.setId("buttonsLayout");
           buttons.getStylesheets().add("/View/GameWindowStyle.css");
           scrollPane.prefViewportWidthProperty().bind(hBox.widthProperty().multiply(0.9));
           scrollPane.prefViewportHeightProperty().bind(hBox.heightProperty());
           drawer.widthProperty().bind(scrollPane.widthProperty());//binding size
           drawer.heightProperty().bind(scrollPane.heightProperty());
           
           buttons.prefHeightProperty().bind(hBox.heightProperty());
           buttons.prefWidthProperty().bind(hBox.widthProperty().multiply(0.1));
           buttons.setAlignment(Pos.CENTER);
           Scene previousScene=mainStage.getScene();
           Scene s=new Scene(mainLayout,previousScene.getWidth(),previousScene.getHeight());
           s.setOnKeyPressed(event -> viewModel.keyPressed(event.getCode(),drawer.getMazeObj(),drawer.getCurrentPosition()));
           mainLayout.getStylesheets().add("/View/MainView.css");
           mainStage.setScene(s);
           mainStage.setMaximized(true);
           drawer.draw();
           
         }
         
         
    public void propertiesClicked() throws IOException {
        newStage("/View/PropertiesWindow.fxml","Properties");
    }
    
    public void changeKeysClicked() throws IOException {
        ChangeKeysController.setVM(viewModel);
        newStage("/View/ChangeKeysWindow.fxml","Keys Window");
    }
    
    @Override
    public void exitClicked() {
        viewModel.exit();
        closeOpenStages();
    }
    
    /**
     * A function that close all open stages
     */
    public static void closeOpenStages(){
        for(Stage stage : stages) {
            stage.close();
        }
    }
    
    @Override
    public void helpClicked() {
        try {
            newStage("/View/HelpWindow.fxml","HelpWindow");
        } catch(IOException e) {
            e.printStackTrace();
        }
    
    }
    
    public void aboutClicked() throws IOException {
        newStage("/View/AboutWindow.fxml","About Window");
    }
    
    public void setStage(Stage stage){
        stages.add(stage);
        mainStage=stage;
    }
    
    public void setVM(MyViewModel vm){
        viewModel=vm;
        try {
            viewModel.initKeys();
        } catch(IOException e) {
            e.printStackTrace();
        }
        viewModel.addObserver(this);
    }
    
    @Override
    public void update(Observable o, Object arg) {
        if(o instanceof MyViewModel){
            if(arg instanceof Maze){//if maze
                Maze m=(Maze)arg;
                drawMaze(m,null);
            }
            if(arg instanceof Position){//if position
                Position p=(Position)arg;
                drawer.redrawTheCharacter(p);
            }
            if(arg instanceof Object[]){//if solution and maze or maze and position
                Object[] arr=(Object[])arg;
                if( arr[0] instanceof Maze){
                    Maze m=(Maze)arr[0];
                    Position p=(Position)arr[1];
                    drawMaze(m,p);
                }
                else{
                    if(arr[0] instanceof Solution){
                        Solution s=(Solution)arr[0];
                        Maze m=(Maze)arr[1];
                        if(m.checkMaze(drawer.getMazeObj())){
                            drawer.drawSolution(s);
                        }
                    }
                }
            }
            if(arg instanceof NoSuchFileException){
                showAlert();
            }
        }
    }
    
    private void drawMaze(Maze m,Position p){
        if(drawer==null){
            drawer=new MazeDrawer(m,new Character(),p);
        }
        else{
            drawer.setMaze(m,p);
        }
        moveToMazeScene();
    }
}
