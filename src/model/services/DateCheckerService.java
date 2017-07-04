package model.services;

import com.sun.javafx.stage.StageHelper;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.Stickers.IVignette;
import model.Stickers.Insurance;
import model.UserManager;
import model.Vehicle.Car;
import model.Vehicle.Vehicle;
import views.ViewWrapper;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by DevM on 6/22/2017.
 */
public class DateCheckerService extends TimerTask {

    private final ViewWrapper viewWrapper = ViewWrapper.getInstance();
    private final Timer timer;
    private final UserManager manager;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<Calendar> dates;

    /**
     * Notifies the user for every expired tax of every registered vehicle.
     * The method maps the vehicle and it's expired taxes.
     *
     */
    private void notifyUserForExpiredTaxes(){
        Map<Vehicle,String> expiredTaxesMap = new TreeMap<>();
        String taxes;
        // Finds the expired taxes and maps the to the vehicle
        for (Vehicle v : vehicles) {
            taxes = findExpiredTaxes(v);
            if(!taxes.equals(""))
                expiredTaxesMap.put(v, taxes);
        }

        showExpirationStatusNotification(expiredTaxesMap);
    }

    /**
     * Finds all the expired taxes per vehicle and returns them as new line separated string.
     * The method also returns the taxes which are close to expiring (2 days before expiration).
     *
     * @param v the vehicle which is checked for expired taxes.
     * @return a new line separated string with the expired taxes.
     */
    private String findExpiredTaxes(Vehicle v) {

            StringBuilder expiredTaxBuilder = new StringBuilder(200);

            // Checks a vehicle's tax

            // Clone tax in order to use roll (roll changes the tax reference)
            Calendar taxEndDate = v.getTax().getEndDateAsCalendarObject();
            Calendar now = Calendar.getInstance();

            if (taxEndDate.before(now)) {
                expiredTaxBuilder.append("Vehicle tax expired!");
                expiredTaxBuilder.append(System.getProperty("line.separator"));
            } else {
                int daysLeftUntilExpiration = shouldNotifyBeforeExpiration(taxEndDate);

                if(daysLeftUntilExpiration != -1) {
                    expiredTaxBuilder.append(daysLeftUntilExpiration);
                    expiredTaxBuilder.append(" day(s) until vehicle tax expires!");
                    expiredTaxBuilder.append(System.getProperty("line.separator"));
                }
            }

            // Checks a vehicle's insurance

            Insurance insurance = v.getInsurance();
            Calendar insuranceEndDate = insurance.getActiveEndDate();
            if(insuranceEndDate == null){
                expiredTaxBuilder.append("Vehicle insurance expired!");
                expiredTaxBuilder.append(System.getProperty("line.separator"));
            } else {
                int daysLeftUntilExpiration = shouldNotifyBeforeExpiration(insuranceEndDate);

                if(daysLeftUntilExpiration != -1) {
                    expiredTaxBuilder.append(daysLeftUntilExpiration);
                    expiredTaxBuilder.append(" day(s) until vehicle insurance expires!");
                    expiredTaxBuilder.append(System.getProperty("line.separator"));
                }
            }

            // Check a vehicle's vignette

            if (v instanceof Car) {
                IVignette vignette = ((Car) (v)).getVignette();
                // Assumes that the db return null when a vignette has expired
                //TODO may be change that
                if (((Car) (v)).getVignette() == null) {
                    expiredTaxBuilder.append("Vehicle vignette expired!");
                    expiredTaxBuilder.append(System.getProperty("line.separator"));
                }else{
                    int daysLeftUntilExpiration = shouldNotifyBeforeExpiration(vignette.getEndDateAsCalender());
                    if(daysLeftUntilExpiration != -1) {
                        expiredTaxBuilder.append(daysLeftUntilExpiration);
                        expiredTaxBuilder.append(" day(s) until vehicle vignette expires!");
                        expiredTaxBuilder.append(System.getProperty("line.separator"));
                    }
                }
            }

            return expiredTaxBuilder.toString();
    }

    /**
     * Checks to see if the time has come for a notification to be displayed.
     * For now a notification will be displayed two days before the expiration date if the tax.
     *
     * @param taxEndDate the expiration date if the tax
     * @return -1 if a notification is not yet required otherwise
     *          the number of days during which the tax is still valid.
     */
    private int shouldNotifyBeforeExpiration(Calendar taxEndDate){
        taxEndDate.set(Calendar.MINUTE,0);
        taxEndDate.set(Calendar.SECOND,0);

        Calendar now = Calendar.getInstance();
        int daysLeft = (int) (taxEndDate.getTimeInMillis() - now.getTimeInMillis()) / (1000 * 60 * 60 * 24);

        // Default value is 2 for now
        if(daysLeft <= 2 && daysLeft > 0){
            return daysLeft;
        }

        return -1;
    }

    /**
     * Constructs a new Alert using a GridPane and shows the expired taxes per vehicle.
     * @param whatExpired a map mapping a vehicle with its expired taxes.
     */
    private void showExpirationStatusNotification(Map<Vehicle,String> whatExpired) {

        // Sets up the grid pane
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        // Constructs Labels and Buttons for each car
        // JavaFX specific
        int row = 0, col = 0;
        for(Map.Entry<Vehicle, String> entry : whatExpired.entrySet()){
            // Adds the label which shows the vehicle registration number
            Label whichCarLbl = new Label("Expired taxes found for vehicle: " + entry.getKey().getRegistrationPlate());
            whichCarLbl.setStyle("-fx-font-weight: bold");
            grid.add(whichCarLbl,col,row);

            // Adds the details button next to the registration number
            Button button = new Button("Go to details");
            button.addEventHandler(MouseEvent.MOUSE_PRESSED, (e) -> openDetailsView(entry.getKey()));
            grid.add(button, col + 1 , row);

            // Shows a bit more information about what has expired
            Label expiredTaxesLbl = new Label(entry.getValue());
            expiredTaxesLbl.setPadding(new Insets(0,0,0,15));
            expiredTaxesLbl.setStyle("-fx-text-fill: red");
            grid.add(expiredTaxesLbl, col , ++ row);

            // Goes to the next row
            row ++;

        }

        // Shows the alert dialog with the custom GridPane
        Platform.runLater(() -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.getDialogPane().setContent(grid);
            alert.setHeaderText("You have expired taxes!");
            alert.setTitle("Tax info");
            alert.initModality(Modality.NONE);
            alert.setContentText("View detailed info about the vehicle?");
            alert.show();
        });
    }

    private void closeOldNotificationIfStillOpen(){
        ObservableList<Stage> availableStages = StageHelper.getStages();

        for(Stage stage : availableStages){
            if(stage.getTitle().equals("Tax info")){
                Platform.runLater(stage::close);
                break;
            }
        }
    }

    /**
     * Opens the vehicleDetailsView when the button of an expired tax is clicked.
     * If the user who owns the vehicle is not logged in this method redirects the user to the login screen.
     * If there is a logged user and the details view is requested for another user
     * the current user is automatically logged out.
     *
     * @param v the vehicle which has an expired tax
     */
    private void openDetailsView(Vehicle v){
        try {
            String vehicleOwnerUsername = manager.getVehicleOwnerUsername(v);
            String loggedUser = manager.getLoggedUserName();

            if(loggedUser != null) {
                if (loggedUser.equals(vehicleOwnerUsername)) {
                    // Go to details screen directly
                    goToDetailsScreen(v);
                }else {
                    manager.userLogout();
                    goToLoginScreen(vehicleOwnerUsername, v);
                }
            }else {
                goToLoginScreen(vehicleOwnerUsername, v);
            }


        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the vehicleDetailsView on a new stage or on an existing stage.
     *
     * @param extra the vehicle extra required for the vehicleDetailsView
     * @throws Exception if there was a problem with redirecting the user to the view
     */
    private void goToDetailsScreen(Vehicle extra) throws Exception{
        // Open the details view in an existing stage or create a new stage if no stages are open
        viewWrapper.setStage(getAvailableStage());

        viewWrapper.putExtra(extra);
        viewWrapper.setRoot("/views/vehicle/vehicleDetailsView.fxml");
        viewWrapper.setSceneRoot(viewWrapper.getRoot());
        viewWrapper.setStageScene(viewWrapper.getScene());
        viewWrapper.getStage().show();
    }

    /**
     * Gets an available stage in order to display content (if app window is open).
     * If a stage is not available a new one is created.
     *
     * @return a stage in which to display content
     * @throws Exception if a stage wasn't found and a new one could not be created
     */
    private Stage getAvailableStage() throws Exception{
        ObservableList<Stage> availableStages = StageHelper.getStages();
        Stage resultStage = null;

        // Look fo an existing stage or create a new one
        boolean stageIsSet = false;
        if(availableStages.size() >= 2){
            for (Stage stage : availableStages) {
                // Add other titles over time and if required
                if (stage.getTitle().equals("Carendar")) {
                    resultStage = stage;
                    stageIsSet = true;
                    break;
                }
            }
        }else{
            Stage stage = new Stage();
            stage.setTitle("Carendar");
            resultStage = stage;

            stageIsSet = true;
        }

        if(!stageIsSet)
            throw new Exception("No suitable stage found!");

        return resultStage;
    }

    // JavaFX specific

    /**
     * Redirects the user to the app login screen.
     * Two extras are passed to the login controller, the username of the user which ones the vehicle
     * and the vehicle object itself required as an extra in the vehicleDetailsView.
     *
     * @param username the username of the user which ones the vehicle
     * @param v the vehicle object to pass to the vehicleDetailsView
     * @throws Exception if the redirection fails
     */
    private void goToLoginScreen(String username, Vehicle v) throws Exception{
        // No PasswordFieldDialog in javaFX ;(
        // Open the details view in an existing stage or create a new stage if no stages are open
        viewWrapper.setStage(getAvailableStage());

        viewWrapper.putExtra(username);
        viewWrapper.putExtra(v);
        viewWrapper.setRoot("/views/login.fxml");
        viewWrapper.setSceneRoot(viewWrapper.getRoot());
        viewWrapper.setStageScene(viewWrapper.getScene());
        viewWrapper.getStage().show();
    }

    public DateCheckerService(){
        super();
        timer = new Timer("DateChecker");
        manager =  UserManager.getInstance();
        vehicles = new ArrayList<>();
        dates = new ArrayList<>();
    }

    @Override
    public void run() {
        // Close old alert if still open
        closeOldNotificationIfStillOpen();
        // Loads all users and vehicles in the UserManager
        manager.loadAllUsersAndVehicles();
        // Clear the vehicles
        vehicles.clear();
        // Gets all the vehicles from the UserManager
        vehicles.addAll(manager.getAllVehiclesOfAllUsers());

        // Notify the user
        notifyUserForExpiredTaxes();

        Logger.getGlobal().log(Level.SEVERE, "Working!!!");

    }

    /**
     * Starts the service at specified hour and repeats at the given interval
     * @param whatHour - hour to start the service - 0 to 24
     * @param interval - interval to repeat it by given repeatInfo value
     * @param repeatInfo - Choose the type of repeating, e.g hour,day,week,month or year
     */
    public void startService(int whatHour,int interval,String repeatInfo){
        Calendar PM12 = Calendar.getInstance();

        switch (repeatInfo.toLowerCase()){
            case "hour":
                PM12.set(Calendar.HOUR_OF_DAY, whatHour);
                PM12.set(Calendar.SECOND, 0);
                PM12.set(Calendar.MINUTE, 0);
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(10,TimeUnit.MINUTES));
                break;
            case "day":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                PM12.set(Calendar.MINUTE, 0);
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(interval, TimeUnit.DAYS));
                break;
            case "week":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                PM12.set(Calendar.MINUTE, 0);
                int weeks = interval * 7;
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(weeks, TimeUnit.DAYS));
                break;
            case "month":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                PM12.set(Calendar.MINUTE, 0);

                int month = interval * 31;
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(month, TimeUnit.DAYS));
                break;
            case "year":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                PM12.set(Calendar.MINUTE, 0);

                int year = interval * 365;
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(year, TimeUnit.DAYS));
                break;

                default:
                    PM12.set(Calendar.HOUR_OF_DAY, 12);
                    PM12.set(Calendar.SECOND, 0);
                    PM12.set(Calendar.MINUTE, 0);
                    timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(12, TimeUnit.HOURS));
        }
    }
}
