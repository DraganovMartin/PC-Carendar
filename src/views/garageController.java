package views;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import model.UserManager;
import model.Vehicle.Car;
import model.Vehicle.Vehicle;
import views.listView.VehicleCellAdapter;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class garageController {
    private final UserManager userManager = UserManager.getInstance();
    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();
    private final List<String> values;
    private List<Vehicle> vehicles;
    private final ContextMenu contextMenu = new ContextMenu();

    public garageController(){
        values = Arrays.asList("car", "motorcycle");

        // Gets vehicles from UserManager
        vehicles = userManager.getRegisteredUserVehicles();
        //System.out.println("size of vehicles : " + vehicles.size());
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
            vehicleListView.setCellFactory(vehicleListView -> {
                VehicleCellAdapter cellAdapter = new VehicleCellAdapter();
                final ContextMenu contextMenu = new ContextMenu();
                MenuItem editItem = new MenuItem("Edit");
                editItem.setOnAction(e -> {
                    try {
                        Vehicle v = vehicleListView.getSelectionModel().getSelectedItem();
                        viewWrapper.setStage((Stage) addVehicleBtn.getScene().getWindow());
                        viewWrapper.putExtra(v);
                        if(v instanceof Car) {
                            viewWrapper.setRoot("addCar.fxml");
                        }
                        else viewWrapper.setRoot("addMotorcycle.fxml");
                        viewWrapper.setSceneRoot(viewWrapper.getRoot());
                        viewWrapper.setStageScene(viewWrapper.getScene());
                        viewWrapper.getStage().show();
                    }
                    catch (IOException ex){
                        ex.printStackTrace();
                    }
                });
                MenuItem deleteItem = new MenuItem("Delete");
                deleteItem.setOnAction(e -> {
                    int selectedIndex = vehicleListView.getSelectionModel().getSelectedIndex();

                    Vehicle v = vehicleListView.getItems().get(selectedIndex);
                    try {
                        userManager.removeVehicle(v,false);
                        vehicleListView.getItems().remove(selectedIndex);

                        showInfoDialog("Vehicle deletion","Vehicle deleted successfully!");

                    } catch (Exception err) {
                        err.printStackTrace();
                        Logger.getGlobal().log(Level.SEVERE, "Error deleting vehicle! \nStack trace:\n" + err.getMessage());
                    }

                });

               contextMenu.getItems().addAll(editItem, deleteItem);

                //cellAdapter.textProperty().bind(cell.itemProperty());

                cellAdapter.emptyProperty().addListener((obs, wasEmpty, isNowEmpty) -> {
                    if (isNowEmpty) {
                        cellAdapter.setContextMenu(null);
                    } else {
                        cellAdapter.setContextMenu(contextMenu);
                    }
                });
                return cellAdapter ;

            });
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

            try {
                if (selected.equals("car")) {
                    // Go to add car screen
                    viewWrapper.setStage((Stage) addVehicleBtn.getScene().getWindow());
                    viewWrapper.putExtra("car");
                    viewWrapper.setRoot("addCar.fxml");
                    viewWrapper.setSceneRoot(viewWrapper.getRoot());
                    viewWrapper.setStageScene(viewWrapper.getScene());
                    viewWrapper.getStage().show();
                } else {
                    // Go to add motorcycle screen
                    viewWrapper.putExtra("motorcycle");
                    viewWrapper.setRoot("addMotorcycle.fxml");
                    viewWrapper.setSceneRoot(viewWrapper.getRoot());
                    viewWrapper.setStageScene(viewWrapper.getScene());
                    viewWrapper.getStage().show();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public void openDetailsView(MouseEvent mouseEvent) {
        Vehicle v = vehicleListView.getSelectionModel().getSelectedItem();

        if (v != null && mouseEvent.getButton() == MouseButton.PRIMARY){
            try {
                // Go to details screen
                viewWrapper.putExtra(v);
                viewWrapper.setRoot("vehicle/vehicleDetailsView.fxml");
                viewWrapper.setSceneRoot(viewWrapper.getRoot());
                viewWrapper.setStageScene(viewWrapper.getScene());
                viewWrapper.getStage().show();

                mouseEvent.consume();
            }catch (Exception exc){
                exc.printStackTrace();
            }
        }
    }

    @FXML
    public void logoutUser(MouseEvent event){
        if(!userManager.userLogout()){
            showInfoDialog("Logout","Error, could not logout user!");
        }

        goToLoginScreen();
    }

    private void showInfoDialog(String headerText, String text){
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Info");
        alert.setHeaderText(headerText);
        alert.setContentText(text);
        alert.show();
    }

    private void goToLoginScreen(){
        try {
            viewWrapper.setRoot("login.fxml");
            viewWrapper.setSceneRoot(viewWrapper.getRoot());
            viewWrapper.setStageScene(viewWrapper.getScene());
            viewWrapper.getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
