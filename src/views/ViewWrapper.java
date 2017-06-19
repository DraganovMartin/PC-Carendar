package views;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ResourceBundle;

/**
 * Created by DevM on 5/21/2017.
 */
public class ViewWrapper {
    private Parent root;
    private Stage stage;
    private Scene scene;
    private FXMLLoader loader;
    private String extra = null;
    private Object extraObject = null;
    private static final double SCREEN_WIDTH = 1280;
    private static final double SCREEN_HEIGHT = 800;

    private static ViewWrapper viewWrapper = new ViewWrapper();

    public static ViewWrapper getInstance(){ return viewWrapper;}

    /**
     * Initializes a new ViewWrapper by creating a non static FXMLLoader with a custom ControllerFactory.
     * The controller factory is used for passing an extra to a specified controller
     */
    private ViewWrapper(){
        loader = new FXMLLoader();

        // Sets a custom ControllerFactory and passes extra if needed
        loader.setControllerFactory(controllerClass -> {
            try {
                Object controller = controllerClass.newInstance();

                // Invokes all the setExtra methods of the controller if such method exists
                for(Method method : controllerClass.getMethods()) {
                    if (method.getName().equals("setExtra")) {
                        method.invoke(controller, extra);
                    }else if(method.getName().equals("setObjectExtra")){
                        method.invoke(controller, extraObject);
                    }
                }

                return controller;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        });
    }

    public Parent getRoot() {
        return root;
    }

    public void setRoot(String resource) throws IOException {
        //this.root = FXMLLoader.load(getClass().getResource(resource));

        // Using non static loader so it's possible to pass an extra to controller
        loader.setLocation(getClass().getResource(resource));

        // When load() is invoked the call() method of ControllerFactory is called
        this.root = loader.load();
        clearLoader();
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

    public void putExtra(String extra) {
        this.extra = extra;
    }
    public void putExtra(Object obj){
        this.extraObject = obj;
    }


    public void emptyData(){
        this.root = null;
        this.scene = null;
        this.stage = null;
    }

    private void clearLoader(){
        loader.setLocation(null);
        loader.setController(null);
        loader.setRoot(null);
    }
}
