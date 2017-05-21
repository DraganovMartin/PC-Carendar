package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.UserManager;

import java.io.IOException;

/**
 * Created by dimcho on 18.05.17.
 */
public class loginController {
    private final UserManager manager = UserManager.getInstance();
    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();

    @FXML
    private TextField usernameText;
    @FXML
    private PasswordField passFiled;

    @FXML
    private Button registerBtn;
    @FXML
    private Button loginBtn;

    public void loginAction(ActionEvent actionEvent) {
        String username = usernameText.getText();
        String pass = passFiled.getText();
        if(!username.isEmpty() && !pass.isEmpty()){
           if(manager.loginUser(username,pass)){
               try {
                   viewWrapper.setStage((Stage)loginBtn.getScene().getWindow());
                   viewWrapper.setRoot("garage.fxml");
                   viewWrapper.setSceneRoot(viewWrapper.getRoot());
                   viewWrapper.setStageScene(viewWrapper.getScene());
                   viewWrapper.getStage().show();
               } catch (IOException e) {
                   e.printStackTrace();
               }
           }else{
               Alert alert = new Alert(Alert.AlertType.INFORMATION);
               alert.setTitle("Login error !!!");
               alert.setHeaderText("Username or password is wrong !");
               String s ="Please enter valid username and password";
               alert.setContentText(s);
               alert.show();

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
