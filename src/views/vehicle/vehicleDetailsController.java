package views.vehicle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import model.Stickers.IVignette;
import model.Stickers.Insurance;
import model.Vehicle.Car;
import model.Vehicle.Motorcycle;
import model.Vehicle.Vehicle;
import model.taxes.Tax;
import views.ViewWrapper;

import java.text.SimpleDateFormat;
import java.util.Calendar;


/**
 * Created by dimcho on 18.06.17.
 */
public class vehicleDetailsController {
    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();
    private final Calendar now = Calendar.getInstance();

    private Vehicle vehicle;

    public vehicleDetailsController(){}

    @FXML
    public void initialize(){

        regNumLbl.setText(vehicle.getRegistrationPlate());
        brandLbl.setText(vehicle.getBrand());
        modelLbl.setText(vehicle.getModel());
        yearLbl.setText(String.valueOf(vehicle.getProductionYear()));
        oilChangeLbl.setText(String.valueOf(vehicle.getNextOilChange()));

        // Sets tax data
        Tax tax = vehicle.getTax();
        if(tax != null) {
            taxLbl.setText(String.valueOf(tax.getAmount()));
            taxPayDate.setText(tax.getEndDate());
        }

        // Sets insurance data
        Insurance insurance = vehicle.getInsurance();
        Calendar activeEndDate = insurance.getActiveEndDate();
        if(activeEndDate != null){
            insuranceLbl.setText(String.valueOf(insurance.getPrice()));
            String insuranceTypeCount = "N/A";
            switch (insurance.getTypeCount()){
                case 1: insuranceTypeCount = "One year"; break;
                case 2: insuranceTypeCount = "2x six months"; break;
                case 3: insuranceTypeCount = "3x four months"; break;
                case 4: insuranceTypeCount = "4x three months"; break;
            }

            insurancePayCountLbl.setText(insuranceTypeCount);
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
            insPeriodLbl.setText(insurance.getStartDate() + " - " + formatter.format(activeEndDate.getTime()));

        } else {
            insuranceLbl.setStyle("-fx-text-fill: red");
            insuranceLbl.setText("Not set");
            insurancePayCountLbl.setStyle("-fx-text-fill: red");
            insurancePayCountLbl.setText("None");
            insPeriodLbl.setStyle("-fx-text-fill: red");
            insPeriodLbl.setText("Insurance expired!");
        }


        // Initialize car/motorcycle parts of vehicle object
        if(vehicle instanceof Car){
            Car car = (Car) vehicle;

            if(car.getPathToImage() != null)
                image.setImage(new Image(car.getPathToImage()));
            else
                image.setImage(new Image("/resources/carDefaultIcon.png"));

            typeLbl.setText(car.getCarType());
            engineLbl.setText(car.getEngineType());
            rangeLbl.setText(car.getKmRange());

            IVignette vignette = car.getVignette();
            // The database.getVignetteForVehicle() method returns a vignette only if it is valid
            if(vignette != null) {
                vigTypeLbl.setText(vignette.getType());
                vigPeriodLbl.setText(vignette.getStartDate() + " until " + vignette.getEndDate());

            }else {
                vigTypeLbl.setStyle("-fx-text-fill: red");
                vigTypeLbl.setText("None");

                vigPeriodLbl.setStyle("-fx-text-fill: red");
                vigPeriodLbl.setText("Vignette expired!");
            }



        }else{
            Motorcycle motorcycle = (Motorcycle) vehicle;
            if(motorcycle.getPathToImage() != null)
                image.setImage(new Image(motorcycle.getPathToImage()));
            else
                image.setImage(new Image("/resources/motorcycleDefaultIcon.png"));

            vigTypeLbl.setStyle("-fx-text-fill: red");
            vigTypeLbl.setText("No vignette for this vehicle");
            vigPeriodLbl.setStyle("-fx-text-fill: red");
            vigPeriodLbl.setText("No vignette for this vehicle");

            typeLbl.setText(motorcycle.getMotorcycleType());
            engineLbl.setText(motorcycle.getEngineType());
            rangeLbl.setText(motorcycle.getKmRange());
        }

    }

    @FXML
    private Label regNumLbl;

    @FXML
    private ImageView image;

    @FXML
    private Button backBtn;

    @FXML
    private Label brandLbl;

    @FXML
    private Label modelLbl;

    @FXML
    private Label typeLbl;

    @FXML
    private Label engineLbl;

    @FXML
    private Label yearLbl;

    @FXML
    private Label rangeLbl;

    @FXML
    private Label vigTypeLbl;

    @FXML
    private Label vigPeriodLbl;

    @FXML
    private Label oilChangeLbl;

    @FXML
    private Label taxLbl;

    @FXML
    private Label taxPayDate;

    @FXML
    private Label insuranceLbl;

    @FXML
    private Label insurancePayCountLbl;

    @FXML
    private Label insPeriodLbl;

    // Invoked by ViewWrapper
    public void setObjectExtra(Object extra){
        System.out.println("Object extra received");
        vehicle = (Vehicle) extra;
    }

    public void goBack(ActionEvent actionEvent) {
        try {
            viewWrapper.setStage((Stage) backBtn.getScene().getWindow());
            viewWrapper.setRoot("garage.fxml");
            viewWrapper.setSceneRoot(viewWrapper.getRoot());
            viewWrapper.setStageScene(viewWrapper.getScene());
            viewWrapper.getStage().show();
        }catch (Exception exc){
            exc.printStackTrace();
        }
    }
}
