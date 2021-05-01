package sample;

import Model.MyModel;
import View.MyViewController;
import ViewModel.MyViewModel;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception{
        FXMLLoader fxmlLoader=new FXMLLoader(getClass().getResource("/View/MyView.fxml"));
        Parent root = fxmlLoader.load();
        primaryStage.setTitle("Hello World");
        
        MyViewModel vm=new MyViewModel(new MyModel());
        MyViewController controller= fxmlLoader.getController();
        controller.setVM(vm);
        
        controller.setStage(primaryStage);
        primaryStage.setOnCloseRequest(event -> {
            vm.exit();
            MyViewController.closeOpenStages();
        });
        Scene scene=new Scene(root,600,400);
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setMaximized(true);
       }
    
    public static void main(String[] args) {
        launch(args);
    }
}
