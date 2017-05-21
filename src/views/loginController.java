package views;

import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
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
    @FXML
    private Label messageLbl;
    @FXML
    private AnchorPane progressPane;
    @FXML
    private ProgressBar progressBar;

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
               // Bind progressBar to task progress
               LoaderTask loaderTask = new LoaderTask();
               progressBar.progressProperty().bind(loaderTask.progressProperty());

               // Show progress pane
               progressPane.setStyle("-fx-background-color: f4f4f4;");
               progressPane.setVisible(true);

               // Run task
               new Thread(loaderTask).start();

           }else{
               messageLbl.setText("No such username or password!");
           }
        }else{
            messageLbl.setText("Both fields are required!");
        }
    }

    private class LoaderTask extends Task<Void> {

        @Override
        protected Void call() throws Exception {

            return null;
        }

        @Override
        protected void succeeded() {
            super.succeeded();
            // TODO redirect to garage view
        }
    }
}
