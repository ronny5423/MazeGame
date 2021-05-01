package View;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

public class PropertiesController implements Initializable {
    @FXML private Label upKey;
    @FXML private Label downKey;
    @FXML private Label leftKey;
    @FXML private Label rightKey;
    @FXML private Label digLeftUp;
    @FXML private Label digRightUp;
    @FXML private Label digLeftDown;
    @FXML private Label digRightDown;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Properties properties=null;
        try {
            properties=loadProperties();
        } catch(IOException e) {
            e.printStackTrace();
        }
        upKey.setText(properties.getProperty("UP"));
        downKey.setText(properties.getProperty("DOWN"));
        leftKey.setText(properties.getProperty("LEFT"));
        rightKey.setText(properties.getProperty("RIGHT"));
        digLeftUp.setText(properties.getProperty("Diagonal Left Up"));
        digRightUp.setText(properties.getProperty("Diagonal Right Up"));
        digLeftDown.setText(properties.getProperty("Diagonal Left Down"));
        digRightDown.setText(properties.getProperty("Diagonal Right Down"));
        
    }
    
    private Properties loadProperties() throws IOException {
        Properties properties=new Properties();
        FileInputStream inputStream=new FileInputStream(System.getProperty("user.dir")+"/resources/config.properties");
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }
}
