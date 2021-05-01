package View;

import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;

import java.io.FileInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class ChangeKeysController implements Initializable{
    @FXML private TextField upKey;
    @FXML private TextField downKey;
    @FXML private TextField rightKey;
    @FXML private TextField leftKey;
    @FXML private TextField leftDownDigKey;
    @FXML private TextField leftUpDigKey;
    @FXML private TextField rightUpDigKey;
    @FXML private TextField rightDownDigKey;
    @FXML private GridPane layout;
    @FXML private Button save;
    private static MyViewModel vm;
    
    /**
     * This methos sets view mode
     * @param viewModel view model
     */
    public static void setVM(MyViewModel viewModel){
        vm=viewModel;
    }
    
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        Properties properties = null;
        try {
            properties=loadProperties();
        } catch(IOException e) {
            e.printStackTrace();
        }
        upKey.setText(properties.getProperty("UP"));
        downKey.setText(properties.getProperty("DOWN"));
        rightKey.setText(properties.getProperty("RIGHT"));
        leftKey.setText(properties.getProperty("LEFT"));
        leftDownDigKey.setText(properties.getProperty("Diagonal Left Down"));
        leftUpDigKey.setText(properties.getProperty("Diagonal Left Up"));
        rightDownDigKey.setText(properties.getProperty("Diagonal Right Down"));
        rightUpDigKey.setText(properties.getProperty("Diagonal Right Up"));
        upKey.setOnKeyPressed(event -> setListener(upKey, event));
        downKey.setOnKeyPressed(event -> setListener(downKey, event));
        leftKey.setOnKeyPressed(event -> setListener(leftKey,event));
        rightKey.setOnKeyPressed(event -> setListener(rightKey,event));
        leftUpDigKey.setOnKeyPressed(event -> setListener(leftUpDigKey,event));
        rightUpDigKey.setOnKeyPressed(event -> setListener(rightUpDigKey,event));
        leftDownDigKey.setOnKeyPressed(event -> setListener(leftDownDigKey,event));
        rightDownDigKey.setOnKeyPressed(event -> setListener(rightDownDigKey,event));
        save.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        save.prefWidthProperty().bind(layout.widthProperty());
     }
    
    private Properties loadProperties() throws IOException {
        Properties properties=new Properties();
        FileInputStream inputStream=new FileInputStream(System.getProperty("user.dir")+"/resources/config.properties");
        properties.load(inputStream);
        inputStream.close();
        return properties;
    }
    
    private void setListener(TextField t, KeyEvent event){
        t.setText(event.getCode().getName());
    }
    
    
    /**
     * Method that gets the keys and send them to view model to check and change
     */
    public void saveClicked(){
        KeyCode[] arr=new KeyCode[8];
        arr[0]=KeyCode.valueOf(upKey.getText());
        arr[1]=KeyCode.valueOf(downKey.getText());
        arr[2]=KeyCode.valueOf(leftKey.getText());
        arr[3]=KeyCode.valueOf(rightKey.getText());
        arr[4]=KeyCode.valueOf(leftUpDigKey.getText());
        arr[5]=KeyCode.valueOf(rightUpDigKey.getText());
        arr[6]=KeyCode.valueOf(leftDownDigKey.getText());
        arr[7]=KeyCode.valueOf(rightDownDigKey.getText());
        if(!vm.changeKeys(arr)){
            showAlert();
        }
        else {
            Stage s=(Stage)layout.getScene().getWindow();
            s.close();
        }
       }
    
    /**
     * This methd shows alert
     */
    private void showAlert(){
        Alert alert=new Alert(Alert.AlertType.ERROR);
        alert.setContentText("Keys must be unique!");
        alert.setResizable(false);
        alert.show();
    }
 
}
