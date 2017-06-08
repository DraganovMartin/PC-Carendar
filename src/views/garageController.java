package views;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import javafx.util.Callback;
import model.UserManager;
import model.Vehicle.Car;
import model.Vehicle.Vehicle;
import views.listView.VehicleCellAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class garageController {
    private final UserManager userManager = UserManager.getInstance();
    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();
    private final List<String> values;
    private List<Vehicle> vehicles;

    public garageController(){
        values = Arrays.asList("car", "motorcycle");

        // Gets vehicles from UserManager
        vehicles = userManager.getRegisteredUserVehicles();
    }
    @FXML
    private Label usernameLabel;
    @FXML
    private Button addVehicleBtn;
    @FXML
    private ListView<String> vehicleChoiceListView;
    @FXML
    private ListView<Vehicle> vehicleListView;


    @FXML
    public void initialize() {
        usernameLabel.setText(userManager.getLoggedUserName());
        vehicleChoiceListView.setItems(FXCollections.observableList(values));

        if(vehicles != null) {
            // Populates the ListView with the vehicles from UserManager
            vehicleListView.setItems(FXCollections.observableList(vehicles));

            // Applies the custom cell design using the VehicleCellAdapter class
            vehicleListView.setCellFactory(vehicleListView -> new VehicleCellAdapter());
        }
    }

    /**
     * Shows list view of vehicle types to choose
     */
    public void addVehicle(ActionEvent actionEvent) {
        vehicleChoiceListView.setVisible(true);
        vehicleListView.setDisable(true);
        vehicleChoiceListView.refresh();
    }

    public void closeEverythingOpen(MouseEvent mouseEvent) {
        vehicleChoiceListView.setVisible(false);
        vehicleListView.setDisable(false);
        vehicleChoiceListView.refresh();
    }

    public void selectVehicle(MouseEvent mouseEvent) {
        String selected = vehicleChoiceListView.getSelectionModel().getSelectedItem();
        if (selected != null){
            if (selected.equals("car")){
                // Go to add car screen
                try {
                    viewWrapper.setStage((Stage)addVehicleBtn.getScene().getWindow());
                    viewWrapper.setRoot("addCar.fxml");
                    viewWrapper.setSceneRoot(viewWrapper.getRoot());
                    viewWrapper.setStageScene(viewWrapper.getScene());
                    viewWrapper.getStage().show();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            else {
                // TODO : go to addMotorcycle screen
            }
        }
    }

    public void openDetailsView(MouseEvent mouseEvent){
        Vehicle v = vehicleListView.getSelectionModel().getSelectedItem();
        if(v instanceof  Car){
            // TODO goto details view car
            System.out.println("Car");
        }else{
            // TODO goto details view car
            System.out.println("Motorcycle");
        }
    }
}
