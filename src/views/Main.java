package views;

import database.Database;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;
import model.UserManager;
import model.authentication.UsedUsernameException;

public class Main extends Application {
    private ViewWrapper viewWrapper = ViewWrapper.getInstance();

    @Override
    public void start(Stage primaryStage) throws Exception{
        Parent root = FXMLLoader.load(getClass().getResource("addCar.fxml"));
        primaryStage.setTitle("Carendar");
        primaryStage.setScene(new Scene(root,1280,800));
        primaryStage.show();
    }


    public static void main(String[] args) {
        launch(args);
    }


}
