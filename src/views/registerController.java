package views;

import javafx.event.ActionEvent;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import model.UserManager;
import model.authentication.UsedUsernameException;

/**
 * Created by DevM on 5/18/2017.
 */
public class registerController {
    private final UserManager manager = UserManager.getInstance();
    public TextField usernameText;
    public PasswordField passwordField;
    public  TextField ageField;
    public void registerAction(ActionEvent actionEvent) {
        try {
            if(!usernameText.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                if (manager.validateRegister(usernameText.getText(), passwordField.getText())) ;
                {
                    manager.registerUser(manager.createUser(usernameText.getText(),passwordField.getText(),Integer.valueOf(ageField.getText())));
                }
            }

        } catch (UsedUsernameException e) {
            e.printStackTrace();
        }
    }
}
