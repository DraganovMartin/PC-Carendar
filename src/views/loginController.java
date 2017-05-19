package views;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.UserManager;

/**
 * Created by dimcho on 18.05.17.
 */
public class loginController {
    private final UserManager manager = UserManager.getInstance();

    @FXML
    private TextField usernameText;
    @FXML
    private PasswordField passFiled;

    public void loginAction(ActionEvent actionEvent) {
        String username = usernameText.getText();
        String pass = passFiled.getText();
        if(!username.isEmpty() && !pass.isEmpty()){
           if(manager.loginUser(username,pass)){
               // TODO Login
           }else{
               // TODO Display error message
           }
        }
    }
}
