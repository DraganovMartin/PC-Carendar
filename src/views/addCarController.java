package views;

/**
 * Created by DevM on 5/22/2017.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.Stickers.*;
import model.UserManager;
import model.Vehicle.Car;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDate;

public class addCarController {
    private UserManager userManager = null;
    private Car vehicle;

    public addCarController(){
        userManager = UserManager.getInstance();
        vehicle = new Car();
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
        if (insuranceTF.getText() == null){
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
        if(regNumTF.getText() == null){
            showDialogError("Please enter vehicle's registration number !");
            regNumTF.requestFocus();
            return;
        }
        else {
            vehicle.setRegistrationPlate(regNumTF.getText());
        }

        if (typeCombo.getValue()== null){
            showDialogError("Please choose car type !");
            typeCombo.requestFocus();
            return;
        }
        else {
            vehicle.setCarType(typeCombo.getValue());
        }

        if (modelTF.getText()== null){
            showDialogError("Please enter car model !");
            modelTF.requestFocus();
            return;
        }
        else {
            vehicle.setModel(modelTF.getText());
        }

        if (brandTF.getText()== null){
            showDialogError("Please enter car brand !");
            brandTF.requestFocus();
            return;
        }
        else {
            vehicle.setBrand(brandTF.getText());
        }

        if (engineCombo.getValue()== null){
            showDialogError("Please choose engine type !");
            engineCombo.requestFocus();
            return;
        }
        else {
            vehicle.setEngineType(engineCombo.getValue());
        }

        if (yearTF.getText()== null){
            showDialogError("Please enter production year !");
            yearTF.requestFocus();
            return;
        }
        else {
            vehicle.setProductionYear(Integer.parseInt(yearTF.getText()));
        }

        if (rangeTF.getText()== null){
            showDialogError("Please enter how many km's vehicle travelled !");
            rangeTF.requestFocus();
            return;
        }
        else {
            vehicle.setKmRange(rangeTF.getText());
        }

        if (setAndGetVignette() != null) {
            vehicle.setVignette(setAndGetVignette());
        }

        if (oilChangeTF.getText()== null){
            showDialogError("Please on what km's is next oil change !");
            oilChangeTF.requestFocus();
            return;
        }
        else {
            vehicle.setNextOilChange(oilChangeTF.getText());
        }

        if (taxTF.getText()== null){
            showDialogError("Please enter vehicle's tax !");
            taxTF.requestFocus();
            return;
        }
        else {
            vehicle.setTax(Double.parseDouble(taxTF.getText()));
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

        vehicle.setInsurance(setAndGetInsurance());
        if(!userManager.addVehicle(vehicle)){
            showDialogError("Error while adding vehicle!");
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
            imageUrl = selectedFile.toURI().toURL();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        if (selectedFile != null) {
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
}
