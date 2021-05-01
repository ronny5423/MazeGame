package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.GridPane;
import javafx.scene.shape.Rectangle;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.net.URL;
import java.util.ResourceBundle;

public class HelpController implements Initializable {
    @FXML private ImageView smiley;
    @FXML private ImageView wall;
    @FXML private GridPane layout;
    @FXML private Rectangle start;
    @FXML private Rectangle finish;
    @FXML private  Rectangle solPath;
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String pathToResources = System.getProperty("user.dir") + "/resources";
        FileInputStream imageWall = null;
        FileInputStream smileyStream=null;
        try {
            imageWall = new FileInputStream(pathToResources + "/Wall.jpg");
            smileyStream=new FileInputStream(pathToResources+"/smiley.png");
        } catch(FileNotFoundException e) {
            e.printStackTrace();
        }
        Image wallImage = new Image(imageWall, 50,50, false, false);
        Image smileyImage=new Image(smileyStream,50,50,false,false);
        wall.setImage(wallImage);
        GridPane.setMargin(wall, new Insets(0,0,0,10));
        smiley.setImage(smileyImage);
        GridPane.setMargin(smiley,new Insets(0,0,0,10));
        wall.fitWidthProperty().bind(layout.widthProperty());
        smiley.fitHeightProperty().bind(layout.heightProperty().divide(10));
        wall.fitHeightProperty().bind(layout.heightProperty().divide(10));
        start.widthProperty().bind(wallImage.widthProperty());
        start.heightProperty().bind(layout.heightProperty().divide(10));
        finish.heightProperty().bind(layout.heightProperty().divide(10));
        finish.widthProperty().bind(wallImage.widthProperty());
        solPath.heightProperty().bind(layout.heightProperty().divide(10));
        solPath.widthProperty().bind(wallImage.widthProperty());
        layout.setGridLinesVisible(true);
    }
}
