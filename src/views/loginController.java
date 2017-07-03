package views;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import javafx.scene.layout.AnchorPane;
import model.UserManager;
import model.Vehicle.Vehicle;

import java.io.IOException;

/**
 * Created by dimcho on 18.05.17.
 */
public class loginController {
    private final UserManager manager = UserManager.getInstance();
    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();
    private final LoaderTask loaderTask = new LoaderTask();
    private Vehicle vehicleExtra;
    private String stringExtra;
    private boolean hasExtraFromService = false;

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
        // Set a non transparent background to the loginPane
        loginPane.setStyle("-fx-background-color: f4f4f4;");

        // The extras are passed by the DateCheckerService
        if(stringExtra != null && vehicleExtra != null){
            hasExtraFromService = true;
            usernameText.setText(stringExtra);
            usernameText.setDisable(true);
            loginPane.setVisible(true);

        } else if(manager.setLoggedUserFromInitialLoading()) {  // Checks if a direct login is possible
            new Thread(loaderTask).start();
        } else {
            // Show login pane
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
               if(!hasExtraFromService){
                   // Run task
                   new Thread(loaderTask).start();
               }else {
                   // Show vehicle details
                   try {
                       viewWrapper.setStage((Stage)loginBtn.getScene().getWindow());
                       viewWrapper.putExtra(vehicleExtra);
                       viewWrapper.setRoot("vehicle/vehicleDetailsView.fxml");
                       viewWrapper.setSceneRoot(viewWrapper.getRoot());
                       viewWrapper.setStageScene(viewWrapper.getScene());
                       viewWrapper.getStage().show();
                   } catch (IOException e) {
                       e.printStackTrace();
                   }
               }

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

            // Loads all vehicle data for the logged user
            manager.loadVehiclesForUser(manager.getLoggedUser());
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

    // Called by ViewWrapper by reflection ...
    public void setObjectExtra(Object extra){
        System.out.println("Object extra received");
        vehicleExtra = (Vehicle) extra;
    }

    // Called by ViewWrapper by reflection ...
    public void setExtra(String extra){
        System.out.println("Extra " + extra + " received!");
        stringExtra = extra;
    }
}
