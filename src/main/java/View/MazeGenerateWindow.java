package View;

import ViewModel.MyViewModel;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MazeGenerateWindow implements Initializable{
@FXML private TextField rowsTextField;
@FXML private TextField columnsTextField;
@FXML private VBox layout;
@FXML private Button button;
@FXML private GridPane gridPane;
      private static MyViewModel vm;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        button.setMaxSize(Double.MAX_VALUE,Double.MAX_VALUE);
        button.prefHeightProperty().bind(gridPane.heightProperty());
     }
    
    public void getRowsAndColumns() throws IOException {
        String rows = rowsTextField.getText();
        String columns = columnsTextField.getText();
        if(!vm.validateInput(rows, columns)){//if rows or columns were invalid
            Alert alert=new Alert(Alert.AlertType.ERROR);
            alert.setContentText("Please select valid rows and columns");
            alert.setResizable(false);
            alert.show();
        }
        else{
            Stage s=(Stage)layout.getScene().getWindow();
            s.close();
        }
      }
    
    public static void setViewModel(MyViewModel viewModel){
        vm=viewModel;
    }
    
}
