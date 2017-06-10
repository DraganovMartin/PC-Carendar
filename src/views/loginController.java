package views;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import model.UserManager;

import java.io.IOException;

/**
 * Created by dimcho on 18.05.17.
 */
public class loginController {
    private final UserManager manager = UserManager.getInstance();
    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();
    private final LoaderTask loaderTask = new LoaderTask();

    @FXML
    private TextField usernameText;
    @FXML
    private PasswordField passFiled;
    @FXML
    private Label messageLbl;
    @FXML
    private AnchorPane loginPane;
    @FXML
    private Button registerBtn;
    @FXML
    private Button loginBtn;


    @FXML
    public void initialize() {
        // TODO : what the fuck is the problem with constructors !!!
        if(manager.setLoggedUserFromInitialLoading()){
            new Thread(loaderTask).start();
        }else{
            // Show progress pane
            loginPane.setStyle("-fx-background-color: f4f4f4;");
            loginPane.setVisible(true);
        }
    }


    private Alert alert = new Alert(Alert.AlertType.INFORMATION);
    /**
     * If the login is successful the method runs a
     * background task to retrieve the logged user's vehicles
     * @param actionEvent
     */
    public void loginAction(ActionEvent actionEvent) {
        String username = usernameText.getText();
        String pass = passFiled.getText();

        if(!username.isEmpty() && !pass.isEmpty()){
           if(manager.loginUser(username,pass)){
               // Run task
               new Thread(loaderTask).start();

           }else{
               alert.setTitle("Login error !!!");
               alert.setHeaderText("Username or password is wrong !");
               String s ="Please enter valid username and password";
               alert.setContentText(s);
               alert.show();

           }
        }else{
            alert.setTitle("Login error !!!");
            alert.setHeaderText("Empty username and/or password !");
            String s ="Please enter a username and password or register";
            alert.setContentText(s);
            alert.show();
        }
    }

    private class LoaderTask extends Task<Void> {

        @Override
        protected void scheduled() {
            super.scheduled();
            loginPane.setVisible(false);
        }

        @Override
        protected Void call() throws Exception {
            manager.loadLoggedUserVehicles();
            // TODO if you want to load the vignettes with the vehicles remove the line below and change UserManager and Database code
            manager.loadCarVignettes();

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            try {
                viewWrapper.setStage((Stage)loginBtn.getScene().getWindow());
                viewWrapper.setRoot("garage.fxml");
                viewWrapper.setSceneRoot(viewWrapper.getRoot());
                viewWrapper.setStageScene(viewWrapper.getScene());
                viewWrapper.getStage().show();
           } catch (IOException e) {
                e.printStackTrace();
           }
        }
    }

    public void registerAction(ActionEvent actionEvent) {
        try {
            viewWrapper.setStage((Stage)registerBtn.getScene().getWindow());
            viewWrapper.setRoot("register.fxml");
            viewWrapper.setSceneRoot(viewWrapper.getRoot());
            viewWrapper.setStageScene(viewWrapper.getScene());
            viewWrapper.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
