package View;

import ViewModel.MyViewModel;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Observer;

public interface IView extends Observer {
    void newClicked() throws IOException;
    void saveClicked() throws IOException;
    void loadClicked();
    
    /**
     * A function to move to game scene
     */
    void moveToMazeScene();
    void propertiesClicked() throws IOException;
     void exitClicked();
    void helpClicked();
    void aboutClicked() throws IOException;
    void changeKeysClicked() throws IOException;
    
    /**
     * Update primary stage
     * @param s primary stage
     */
    void setStage(Stage s);
    
    /**
     * upate view model
     * @param vm view model
     */
    void setVM(MyViewModel vm);
}
