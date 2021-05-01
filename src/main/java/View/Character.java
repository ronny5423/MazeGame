package View;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import java.io.FileInputStream;
import java.io.IOException;


public class Character implements ICharacter {
    
    
    @Override
    public void drawCharacter(GraphicsContext drawer, double rowIndex, double columnIndex,double cellHeight,double cellWidth) {
        try {
            FileInputStream input=new FileInputStream(System.getProperty("user.dir")+"/resources/smiley.png");
            Image smiley=new Image(input,cellWidth,cellHeight,false,false);
            drawer.drawImage(smiley,rowIndex,columnIndex,cellWidth,cellHeight);
            input.close();
        } catch(IOException e) {
            e.printStackTrace();
        }
    
    }
}
