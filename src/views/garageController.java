package views;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import model.UserManager;

public class garageController {
    private final UserManager userManager = UserManager.getInstance();
    @FXML
    private Label usernameLabel;


    public void authenticateLogin(ActionEvent actionEvent) {

    }

    @FXML
    public void initialize() {
        usernameLabel.setText(userManager.getLoggedUserName());
    }
}
