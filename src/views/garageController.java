package views;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import model.UserManager;

import java.util.Arrays;
import java.util.List;

public class garageController {
    private final UserManager userManager = UserManager.getInstance();
    private final List<String> values;

    public garageController(){
        values = Arrays.asList("car", "motorcycle");
    }
    @FXML
    private Label usernameLabel;
    @FXML
    private Button addVehicleBtn;
    @FXML
    private ListView<String> vehicleListView;


    @FXML
    public void initialize() {
        usernameLabel.setText(userManager.getLoggedUserName());
        vehicleListView.setItems(FXCollections.observableList(values));
    }

    /**
     * Shows list view of vehicle types to choose
     */
    public void addVehicle(ActionEvent actionEvent) {
        vehicleListView.setVisible(true);
        vehicleListView.refresh();
    }

    public void closeEverythingOpen(MouseEvent mouseEvent) {
        vehicleListView.setVisible(false);
        vehicleListView.refresh();
    }

    public void selectVehicle(MouseEvent mouseEvent) {
        String selected = vehicleListView.getSelectionModel().getSelectedItem();
        if (selected != null){
            if (selected.equals("car")){
                // TODO : go to addCar screen
            }
            else {
                // TODO : go to addMotorcycle screen
            }
        }
    }
}
