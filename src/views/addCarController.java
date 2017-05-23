package views;

/**
 * Created by DevM on 5/22/2017.
 */

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

public class addCarController {

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
    private ComboBox<?> typeCombo;

    @FXML
    private ComboBox<?> engineCombo;

    @FXML
    private TextField yearTF;

    @FXML
    private TextField rangeTF;

    @FXML
    private ComboBox<?> vigTypeCombo;

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
    private ComboBox<?> insurancePayCountDP;

    @FXML
    private DatePicker insDateStartDP;

    @FXML
    void cancel(ActionEvent event) {

    }

    @FXML
    void saveCar(ActionEvent event) {

    }

    @FXML
    private void changeImage(MouseEvent mouseEvent) {
    }
}
