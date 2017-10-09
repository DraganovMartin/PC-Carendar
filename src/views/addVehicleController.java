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
import java.util.Arrays;
import java.util.Calendar;

public class addVehicleController {
    private UserManager userManager = null;
    private Vehicle vehicle;
    private String extra;
    private Object receivedObject;

    private static final String LICENCE_PLATE_REGEX = "[ABEKMHOPCTYX]{1,2}[0-9]{4,6}[ABEKMHOPCTYX]{1,2}";

    private boolean isNumber(String text) {
        char[] textChars = text.toCharArray();
        for(char ch : textChars) {
            // Skip '.' symbol if the number is a double
            if((int) ch == 46) {
                continue;
            }

            if((int) ch < 48 || (int) ch > 57) {
                return false;
            }
        }

        return true;
    }

    private boolean isInteger(String text) {
        return isNumber(text) && !text.contains(".");

    }

    private boolean isDouble(String text) {
        if(isNumber(text) && text.contains(".")) {
          return true;
        }

        return isInteger(text);

    }

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
            if (vehicle.getPathToImage() != null) {
                carImage.setImage(new Image(vehicle.getPathToImage()));
            }
            regNumTF.setText(vehicle.getRegistrationPlate());
            brandTF.setText(vehicle.getBrand());
            modelTF.setText(vehicle.getModel());
            oilChangeTF.setText(String.valueOf(vehicle.getNextOilChange()));
            yearTF.setText(String.valueOf(vehicle.getProductionYear()));
            taxTF.setText(String.valueOf(vehicle.getTax().getAmount()));
            System.out.println(vehicle.getTax().getEndDate());
            String taxDate = vehicle.getTax().getEndDate();
            String[] splittedDate = taxDate.split("-");
            taxPayDP.setValue(LocalDate.of(Integer.parseInt(splittedDate[0]),Integer.parseInt(splittedDate[1]),Integer.parseInt(splittedDate[2])));

            if (receivedObject instanceof Car) {
                Car received = (Car) receivedObject;
                IVignette vignette = received.getVignette();
                // The db returns null if vignette is invalid for now
                // Couldn't find bug, help
                if (vignette != null) {
                    // TODO : check why vignette date is wrong...
                    System.out.println("Vignette is valid");
                    vigTypeCombo.setValue(vignette.getType());
                    String[] vignetteDates = received.getVignette().getStartDate().split("-");
                    System.out.println(Arrays.toString(vignetteDates));
                    vigStartDP.setValue(LocalDate.of(Integer.parseInt(vignetteDates[0]),Integer.parseInt(vignetteDates[1]),Integer.parseInt(vignetteDates[2])));
                }
                engineCombo.setValue(received.getEngineType());
                String type = received.getCarType();
                typeCombo.setValue(type);
                rangeTF.setText(received.getKmRange());

                Insurance insurance = received.getInsurance();
                if(insurance.isValid()) {
                    insuranceTF.setText(String.valueOf(insurance.getPrice()));
                    switch (insurance.getTypeCount()) {
                        case 1:
                            insurancePayCountDP.setValue("ONE");
                            break;
                        case 2:
                            insurancePayCountDP.setValue("TWO");
                            break;
                        case 3:
                            insurancePayCountDP.setValue("THREE");
                            break;
                        case 4:
                            insurancePayCountDP.setValue("FOUR");
                            break;
                    }
                    Calendar[] insuranceDates = insurance.getEndDates();
                    Calendar check = Calendar.getInstance();
                    // LocalDate class uses values 1-12 for Months, but the Calendar class uses values 0-11
                    // so adding 1 to the Calender range
                    for (Calendar date : insuranceDates) {
                        if (date.compareTo(check) == 0 || date.compareTo(check) > 0) {
                            Calendar tempDate = Calendar.getInstance(); // for setting date first here, because if month is lower than subtracted number crashes ...
                            tempDate.clear();
                            switch (insurance.getTypeCount()) {
                                case 1:
                                    tempDate = (Calendar) date.clone();
                                    tempDate.set(Calendar.YEAR,tempDate.get(Calendar.YEAR)-1);
                                    insDateStartDP.setValue(LocalDate.of(tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH) + 1, tempDate.get(Calendar.DAY_OF_MONTH)));
                                    break;
                                case 2:
                                    tempDate = (Calendar) date.clone();
                                    tempDate.set(Calendar.MONTH,tempDate.get(Calendar.MONTH)-6);
                                    insDateStartDP.setValue(LocalDate.of(tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH) + 1, tempDate.get(Calendar.DAY_OF_MONTH)));
                                    break;
                                case 3:
                                    tempDate = (Calendar) date.clone();
                                    tempDate.set(Calendar.MONTH,tempDate.get(Calendar.MONTH)-4);
                                    insDateStartDP.setValue(LocalDate.of(tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH) + 1, tempDate.get(Calendar.DAY_OF_MONTH)));
                                    break;
                                case 4:
                                    tempDate = (Calendar) date.clone();
                                    tempDate.set(Calendar.MONTH,tempDate.get(Calendar.MONTH)-3);
                                    insDateStartDP.setValue(LocalDate.of(tempDate.get(Calendar.YEAR), tempDate.get(Calendar.MONTH) + 1, tempDate.get(Calendar.DAY_OF_MONTH)));
                                    break;
                            }
                            break;
                        }
                    }
                }
            }

            if (receivedObject instanceof Motorcycle) {
                Motorcycle received = (Motorcycle) receivedObject;
                engineCombo.setValue(received.getEngineType());
                String type = received.getMotorcycleType();
                typeCombo.setValue(type);
                rangeTF.setText(received.getKmRange());
                Insurance insurance = received.getInsurance();

                if(insurance.isValid()) {
                    insuranceTF.setText(String.valueOf(insurance.getPrice()));
                    switch (insurance.getTypeCount()){
                        case 1:
                            insurancePayCountDP.setValue("ONE");
                            break;
                        case 2:
                            insurancePayCountDP.setValue("TWO");
                            break;
                        case 3:
                            insurancePayCountDP.setValue("THREE");
                            break;
                        case 4:
                            insurancePayCountDP.setValue("FOUR");
                            break;
                    }

                    Calendar[] insuranceDates = insurance.getEndDates();
                    Calendar check = Calendar.getInstance();
                    for (Calendar date : insuranceDates) {
                        if (date.compareTo(check) == 0 || date.compareTo(check) > 0) {
                            switch (insurance.getTypeCount()) {
                                case 1:
                                    insDateStartDP.setValue(LocalDate.of(date.get(Calendar.YEAR) - 1, date.get(Calendar.MONTH) + 1, date.get(Calendar.DAY_OF_MONTH)));
                                    break;
                                case 2:
                                    insDateStartDP.setValue(LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) - 5, date.get(Calendar.DAY_OF_MONTH)));
                                    break;
                                case 3:
                                    insDateStartDP.setValue(LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) - 3, date.get(Calendar.DAY_OF_MONTH)));
                                    break;
                                case 4:
                                    insDateStartDP.setValue(LocalDate.of(date.get(Calendar.YEAR), date.get(Calendar.MONTH) - 2, date.get(Calendar.DAY_OF_MONTH)));
                                    break;
                            }
                            break;
                        }
                    }
                }
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
        if (insuranceTF.getText().equals("") || !isDouble(insuranceTF.getText())){
            showDialogError("Please set a valid insurance tax !");
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
    void saveVehicle(ActionEvent event) {
        // Changed because when getText() is empty it returns empty string instead of null
        if (regNumTF.getText().equals("") || !regNumTF.getText().matches(LICENCE_PLATE_REGEX)) {
            showDialogError("Please enter a valid vehicle registration number !");
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

        String yearFieldText = yearTF.getText();
        int year = -1;
        if(isInteger(yearFieldText)) {
            year = Integer.parseInt(yearFieldText);
        }

        if (yearFieldText.equals("") || year < 1900 || yearFieldText.length() < 3 || yearFieldText.length() > 4) {
            showDialogError("Please enter a valid production year ! Note that the year must be grater than 1900.");
            yearTF.requestFocus();
            return;
        } else {
            vehicle.setProductionYear(Integer.parseInt(yearTF.getText()));
        }

        String range;
        // Allowing a 9 digit number only. Look at the Oil change checks as well.
        if (rangeTF.getText().equals("") || !isInteger(rangeTF.getText()) || rangeTF.getText().length() > 9) {
            showDialogError("Please enter how many km's vehicle travelled !");
            rangeTF.requestFocus();
            return;
        } else {
            range = rangeTF.getText();
        }

        // Oil change checks
        String oilChangeTxt = oilChangeTF.getText();
        long iRange = Integer.parseInt(range);
        int iOilChange = -1;
        if(isInteger(oilChangeTxt)) {
            iOilChange = Integer.parseInt(oilChangeTxt);
        }

        // oilChangeTxt.length() > 9  int allows a 10 digit number at most. Allowing only 9 digit numbers
        if (oilChangeTxt.equals("") || oilChangeTxt.length() > 9 || iOilChange < iRange){
            showDialogError("Please on what km's is next oil change ! The value can not be lower than the vehicle's range");
            oilChangeTF.requestFocus();
            return;
        }
        else {
            vehicle.setNextOilChange(iOilChange);
        }

        // Tax amount checks
        if (taxTF.getText().equals("") || !isDouble(taxTF.getText()) || taxTF.getText().length() > 6){
            showDialogError("Please enter a valid vehicle tax !");
            taxTF.requestFocus();
            return;
        }
        else {
            vehicle.setTaxAmount(Double.parseDouble(taxTF.getText()));
        }

        // Tax payment day checks
        if (taxPayDP.getValue() == null || taxPayDP.getValue().isBefore(LocalDate.now())){
            showDialogError("Please choose a correct tax payment date !");
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
        if(insurance == null){
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

        // Avoiding null pointer exception
        if (extra == null) extra = "";
        // Initialize car/motorcycle parts of the object
        if(extra.equals("car") || receivedObject != null && receivedObject instanceof Car){

            IVignette vignette = setAndGetVignette();
            if (vignette == null) {
                return;
            }

            Car car = (Car) vehicle;
            try {
                userManager.removeVehicle(car,false);
            } catch (Exception e) {
                e.printStackTrace();
            }

            car.setCarType(vehicleType);
            car.setEngineType(engineType);
            car.setKmRange(range);
            car.setVignette(vignette);

            if(!userManager.addVehicle(car)){
                showDialogError("Error while adding vehicle!");
            }

        }else{

            Motorcycle motorcycle = (Motorcycle) vehicle;
            if (receivedObject != null && receivedObject instanceof Motorcycle){
                try {
                    userManager.removeVehicle(motorcycle,false);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
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
        alert.setHeaderText("Incorrect data provided");
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
