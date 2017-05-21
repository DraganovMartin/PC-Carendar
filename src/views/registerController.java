package views;

import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import model.UserManager;
import model.authentication.WeakPassException;

import java.io.IOException;

/**
 * Created by DevM on 5/18/2017.
 */
public class registerController {
    private final UserManager manager = UserManager.getInstance();
    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();
    public TextField usernameText;
    public PasswordField passwordField;
    public  TextField ageField;
    public void registerAction(ActionEvent actionEvent) {
        try {
            if(!usernameText.getText().isEmpty() && !passwordField.getText().isEmpty()) {
                if (manager.validateRegister(usernameText.getText(), passwordField.getText())) ;
                {
                    manager.registerUser(manager.createUser(usernameText.getText(),passwordField.getText(),Integer.valueOf(ageField.getText())));
                    try {
                        viewWrapper.setRoot("login.fxml");
                        viewWrapper.setStage((Stage)usernameText.getScene().getWindow());
                        viewWrapper.setSceneRoot(viewWrapper.getRoot());
                        viewWrapper.setStageScene(viewWrapper.getScene());
                        viewWrapper.getStage().show();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }

        } catch (WeakPassException e) {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Password error !!!");
            alert.setHeaderText("Password is weak !");
            String s ="Password must meet following rules : one capital letter, one small letter a number and six digits length. ";
            alert.setContentText(s);
            alert.show();
        }
    }
}
