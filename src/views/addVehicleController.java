package views;

/**
 * Created by DevM on 5/22/2017.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Stickers.*;
import model.UserManager;
import model.Vehicle.Car;
import model.Vehicle.Motorcycle;
import model.Vehicle.Vehicle;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

public class addVehicleController {
    private UserManager userManager = null;
    private Vehicle vehicle;
    private String extra;
    private Object receivedObject;

    public addVehicleController(){
        userManager = UserManager.getInstance();
    }

    @FXML
    public void initialize(){
        if(extra != null){
            if (extra.equals("car"))
                vehicle = new Car();
            else
                vehicle = new Motorcycle();
        }
        if (receivedObject != null) {
            vehicle = (Vehicle) receivedObject;
            System.out.println("Yes it's a car");
            carImage.setImage(new Image(vehicle.getPathToImage()));
            regNumTF.setText(vehicle.getRegistrationPlate());
            brandTF.setText(vehicle.getBrand());
            modelTF.setText(vehicle.getModel());
            oilChangeTF.setText(vehicle.getNextOilChange());
            yearTF.setText(String.valueOf(vehicle.getProductionYear()));
//            taxTF.setText(String.valueOf(vehicle.getTax().getAmount()));
//            System.out.println(vehicle.getTax().getEndDate());
            //taxPayDP.setValue(new LocalDate());

            if (receivedObject instanceof Car) {
                //String type = vehicle.getCarType();
            }
        }
    }

    @FXML
    private TextField regNumTF;

    @FXML
    private ImageView carImage;

    @FXML
    private Button cancelBtn;

    @FXML
    private Button saveBtn;

    @FXML
    private TextField brandTF;

    @FXML
    private TextField modelTF;

    @FXML
    private ComboBox<String> typeCombo;

    @FXML
    private ComboBox<String> engineCombo;

    @FXML
    private TextField yearTF;

    @FXML
    private TextField rangeTF;

    @FXML
    private ComboBox<String> vigTypeCombo;

    @FXML
    private DatePicker vigStartDP;

    @FXML
    private TextField oilChangeTF;

    @FXML
    private TextField taxTF;

    @FXML
    private DatePicker taxPayDP;

    @FXML
    private TextField insuranceTF;

    @FXML
    private ComboBox<String> insurancePayCountDP;

    @FXML
    private DatePicker insDateStartDP;

    private URL imageUrl;

    private IVignette setAndGetVignette(){
        IVignette vignette;
        if (vigTypeCombo.getValue() == null){
            showDialogError("Please choose vignette type !");
            vigTypeCombo.requestFocus();
            return null;
        }
        if (vigStartDP.getValue() == null){
            showDialogError("Please choose vignette start date !");
            vigStartDP.requestFocus();
            return null;
        }

        LocalDate date = vigStartDP.getValue();
        String vigType = vigTypeCombo.getValue();
        //System.out.println("Year : " + date.getYear() +"Month : " + (date.getMonth().getValue()-1) + "Day : " + date.getDayOfMonth());
        switch (vigType){
            case "Week":
                vignette = new WeekVignette(date.getYear(),date.getMonth().getValue()-1,date.getDayOfMonth(),15);
                break;
            case "Month" :
                vignette = new MonthVignette(date.getYear(),date.getMonth().getValue()-1,date.getDayOfMonth(),30);
                break;
            case "Annual":
                vignette = new AnnualVignette(date.getYear(),date.getMonth().getValue()-1,date.getDayOfMonth(),97);
                break;
                default:
                    vignette = null;
        }
        return vignette;
    }

    private Insurance setAndGetInsurance(){
        Insurance insurance = new Insurance();
        if (insuranceTF.getText().equals("")){
            showDialogError("Please set insurance tax !");
            insuranceTF.requestFocus();
            return null;
        }

        if (insurancePayCountDP.getValue() == null){
            showDialogError("Please choose insurance payment count !");
            insurancePayCountDP.requestFocus();
            return null;
        }

        if (insDateStartDP.getValue() == null){
            showDialogError("Please choose insurance start day !");
            insDateStartDP.requestFocus();
            return null;
        }

        insurance.setPrice(Double.parseDouble(insuranceTF.getText()));

        switch (insurancePayCountDP.getValue()){
            case "ONE":
                insurance.setTypeCount(Insurance.Payments.ONE);
                break;
            case "TWO":
                insurance.setTypeCount(Insurance.Payments.TWO);
                break;
            case "THREE":
                insurance.setTypeCount(Insurance.Payments.THREE);
                break;
            case "FOUR":
                insurance.setTypeCount(Insurance.Payments.FOUR);
                break;

        }
        LocalDate date = insDateStartDP.getValue();
        insurance.setStartDate(date.getYear(),date.getMonthValue()-1,date.getDayOfMonth());


        return insurance;
    }

    @FXML
    void saveCar(ActionEvent event) {
        // Changed because when getText() is empty it returns empty string instead of null
        if (regNumTF.getText().equals("")) {
            showDialogError("Please enter vehicle's registration number !");
            regNumTF.requestFocus();
            return;
        } else {
            vehicle.setRegistrationPlate(regNumTF.getText());
        }

        if (modelTF.getText().equals("")) {
            showDialogError("Please enter car model !");
            modelTF.requestFocus();
            return;
        } else {
            vehicle.setModel(modelTF.getText());
        }

        if (brandTF.getText().equals("")) {
            showDialogError("Please enter car brand !");
            brandTF.requestFocus();
            return;
        } else {
            vehicle.setBrand(brandTF.getText());
        }

        if (yearTF.getText().equals("")) {
            showDialogError("Please enter production year !");
            yearTF.requestFocus();
            return;
        } else {
            vehicle.setProductionYear(Integer.parseInt(yearTF.getText()));
        }

        if (oilChangeTF.getText().equals("")){
            showDialogError("Please on what km's is next oil change !");
            oilChangeTF.requestFocus();
            return;
        }
        else {
            vehicle.setNextOilChange(oilChangeTF.getText());
        }

        if (taxTF.getText().equals("")){
            showDialogError("Please enter vehicle's tax !");
            taxTF.requestFocus();
            return;
        }
        else {
            vehicle.setTaxAmount(Double.parseDouble(taxTF.getText()));
        }

        if (taxPayDP.getValue() == null){
            showDialogError("Please choose tax payment date !");
            taxPayDP.requestFocus();
            return;
        }
        else {
            LocalDate date = taxPayDP.getValue();
            vehicle.getTax().setEndDate(date.getYear(),date.getMonthValue()-1,date.getDayOfMonth());
        }

        if(imageUrl != null){
            vehicle.setPathToImage(imageUrl.toString());
        }

        Insurance insurance = setAndGetInsurance();
        if(insurance == null){ // No return was present and a Car was added even if insurance was empty
            return;
        }else {
            vehicle.setInsurance(insurance);
        }

        // Can skip check for empty, because I added a default value in addCar.fxml
        // To remove default value remove the <value> tag in the ComboBox in fxml
        String vehicleType;
        if (typeCombo.getValue() == null) {
            showDialogError("Please choose car type !");
            typeCombo.requestFocus();
            return;
        } else {
            vehicleType = typeCombo.getValue();
        }

        String engineType;
        if (engineCombo.getValue() == null) {
            showDialogError("Please choose engine type !");
            engineCombo.requestFocus();
            return;
        } else {
           engineType = engineCombo.getValue();
        }

        String range;
        if (rangeTF.getText().equals("")) {
            showDialogError("Please enter how many km's vehicle travelled !");
            rangeTF.requestFocus();
            return;
        } else {
            range = rangeTF.getText();
        }


        // Initialize car/motorcycle parts of the object
        if(extra.equals("car")){
            IVignette vignette = setAndGetVignette();
            if (vignette == null) {
                return;
            }

            Car car = (Car) vehicle;

            car.setCarType(vehicleType);
            car.setEngineType(engineType);
            car.setKmRange(range);
            car.setVignette(vignette);

            if(!userManager.addVehicle(car)){
                showDialogError("Error while adding vehicle!");
            }

        }else{
            Motorcycle motorcycle = (Motorcycle) vehicle;
            motorcycle.setMotorcycleType(vehicleType);
            motorcycle.setEngineType(engineType);
            motorcycle.setKmRange(range);

            if(!userManager.addVehicle(motorcycle)){
                showDialogError("Error while adding vehicle!");
            }
        }

        cancelBtn.fire();
    }

    @FXML
    void cancel(ActionEvent event) {
        try {
            ViewWrapper.getInstance().setStage((Stage) cancelBtn.getScene().getWindow());
            ViewWrapper.getInstance().setRoot("garage.fxml");
            ViewWrapper.getInstance().setSceneRoot(ViewWrapper.getInstance().getRoot());
            ViewWrapper.getInstance().setStageScene(ViewWrapper.getInstance().getScene());
            ViewWrapper.getInstance().getStage().show();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @FXML
    private void changeImage(MouseEvent mouseEvent) {
        Stage thisStage = (Stage)carImage.getScene().getWindow();
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose image for your car ");
        fileChooser.getExtensionFilters().
                add(new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg"));
        File selectedFile = fileChooser.showOpenDialog(thisStage);
        imageUrl = null;
        try {
            if(selectedFile != null)
                imageUrl = selectedFile.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (imageUrl != null) {
            carImage.setImage(new Image(imageUrl.toString()));
        }
    }

    private void showDialogError(String text){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Data error");
        alert.setHeaderText("No data provided");
        alert.setContentText(text);
        alert.show();
    }

    // Called by ViewWrapper by reflection ...
    public void setExtra(String extra){
        System.out.println("Extra " + extra + " received!");
        this.extra = extra;
    }

    // Called by ViewWrapper by reflection ...
    public void setObjectExtra(Object extra){
        System.out.println("Extra " + extra + " received!");
        this.receivedObject = extra;
    }
}
