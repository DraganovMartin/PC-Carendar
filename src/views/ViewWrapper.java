package views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Screen;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * Created by DevM on 5/21/2017.
 */
public class ViewWrapper {
    private Parent root;
    private Stage stage;
    private Scene scene;
    private static final double SCREEN_WIDTH = 1280;
    private static final double SCREEN_HEIGHT = 800;

    private static ViewWrapper viewWrapper = new ViewWrapper();

    public static ViewWrapper getInstance(){ return viewWrapper;}

    private ViewWrapper(){

    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(String resource) throws IOException {
        this.root = FXMLLoader.load(getClass().getResource(resource));
    }

    public Stage getStage() {
        return stage;
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public Scene getScene() {
        return scene;
    }

    public void setSceneRoot(Parent root) {
        this.scene = new Scene(root,SCREEN_WIDTH,SCREEN_HEIGHT);
    }

    public void setStageScene(Scene scene){
        this.stage.setScene(scene);
    }

    public void emptyData(){
        this.root = null;
        this.scene = null;
        this.stage = null;
    }
}
