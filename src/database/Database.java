package database;

import model.Stickers.*;
import model.UserManager;
import model.Vehicle.Car;
import model.Vehicle.Motorcycle;
import model.Vehicle.Vehicle;
import model.taxes.Tax;
import model.taxes.VehicleTax;

import java.sql.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by DevM on 5/17/2017.
 */
public class Database {
    private Connection connect = null;
    private Statement statement = null;
    private PreparedStatement preparedStatement = null;
    private ResultSet resultSet = null;

    private static Database ourInstance = new Database();

    public static Database getInstance() {
        return ourInstance;
    }

    private Database() {
        try {
            connect = DriverManager.getConnection("jdbc:sqlite:D:\\Programming\\Databases\\Carendar sqlite\\Carendar.db");
            statement = connect.createStatement();
            System.out.println("Connection success");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void closeDatabase(){
        try {
            connect.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ResultSet executeQuery(String query) throws SQLException{
        return statement.executeQuery(query);
    }

    /**
     *
     * @param username - String
     * @param pass - String
     * @param age - pass
     * @return Returns true if user is successfully registered in database, false otherwise
     */
    public boolean createUser(String username,String pass,int age){
        try {
            String sql = "insert into users(username,password,userAge)" + "values(?,?,?)";
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,pass);
            preparedStatement.setInt(3,age);
            int added = preparedStatement.executeUpdate();
            preparedStatement.close();
            if(added >0) return true;
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean availableToRegister(String username,String pass){
        String sql = "select username from users where username = ?";
        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,username);
             resultSet = preparedStatement.executeQuery();
             while (resultSet.next()){
                // System.out.println(resultSet.getString(1) + resultSet.getString(2) + resultSet.getString(3));
                 return false;
             }
             return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Checks user credentials from db
     * @param username
     * @param pass
     * @return the age of the logged user, 0 if the value in the db is NULL and -1 if the login was unsuccessful
     */
    public int logInUser(String username, String pass){
        String sql = "select username,userAge,isLogged from users where username = ? AND password = ?";
        int age = 0;
        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, pass);
            resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                return -1;
            }

//            resultSet.updateInt(4, 1);
//            resultSet.updateRow();
            age = resultSet.getInt(3);
        }
        catch (SQLException ex){
            ex.printStackTrace();
        }

        // Added because of SQLite if using MySQL below lines must be commented and uncomment above lines
            String updateStatus = "UPDATE users set isLogged = 1 where username = ?;";
            int result=-1;
        try {
            preparedStatement = connect.prepareStatement(updateStatus);
            preparedStatement.setString(1,username);
           result = preparedStatement.executeUpdate();
        } catch (SQLException e1) {
            e1.printStackTrace();
        }
        System.out.println("Update result : " + result);

        return age;
    }

    /**
     * Logs out a user by setting 0 to isLogged field in the db.
     *
     * @param username the user to logout
     * @return true if logout was successful false otherwise
     */
    public boolean logOutUser(String username){
        String sql = "Update users Set isLogged = 0 Where username = ?";

        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,username);

            if(preparedStatement.executeUpdate() == 0)
                return false;

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Gets the user's vehicles from the database as an ArrayList.
     *
     * The method constructs the individual Vehicle objects (Car, Motorcycle) and stores them in an ArrayList.
     *
     * @param username the unique id that identifies which vehicles are owned by this user
     * @return a list of the user's vehicles, null if any errors occurred or no vehicles are stored in the db
     */
    /*
    "select brand, model,vehicles.type,registration,ownerID, productionYear," +
                " taxes.type, dateFrom, dateTo, price" +
                " from users Inner Join vehicles On (users.username = vehicles.ownerID)" +
                " Left Join taxes On (vehicles.idVehicles=taxes.taxes_vehicleID)" +
                " Where username=?";
     */
    public List<Vehicle> getLoggedUserVehicles(String username){
        String sql = "select registration, brand, model,vehicles.type,body_type, engine_type," +
                " rangeKm ,image_path, productionYear,nextOilChange From vehicles Where ownerID = ?";

        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,username);
            resultSet = preparedStatement.executeQuery();

            List<Vehicle> vehicles = new ArrayList<>(20);

            while(resultSet.next()){

                String registration = resultSet.getString(1);
                String brand = resultSet.getString(2);
                String model = resultSet.getString(3);
                String bodyType = resultSet.getString(5);
                String engineType = resultSet.getString(6);
                String range = resultSet.getString(7);
                String imagePath = resultSet.getString(8);
                int productionYear = resultSet.getInt(9);
                int nextOilChange = resultSet.getInt(10);

                switch(resultSet.getString(4)){
                    case "Car" :
                        vehicles.add(new Car(registration,brand,model,bodyType,engineType,range,imagePath,productionYear,nextOilChange));
                        break;

                    case "Motorcycle" :
                        vehicles.add(new Motorcycle(registration,brand,model,bodyType,engineType,range,imagePath,productionYear,nextOilChange));
                        break;

                    default: throw new Exception("No such vehicle type! Please check the type in table vehicles.");
                }
            }

            return vehicles;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the current vignette data from the db as an ArrayList
     * Only one vignette is active per period.
     * The currently active vignette has a endDate >= Now()
     *
     * @param registration the vehicle's registration number
     * @return the current active vignette
     */
    public IVignette getVignetteForVehicle(String registration){
        String sql = "select taxes.type, dateFrom, price from taxes where vehicle_registration = ? And taxes.type Like '%vignette' And dateTo >= date('now')";

        try {
            IVignette vignette = null;

            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, registration);
            resultSet = preparedStatement.executeQuery();

            // Only one vignette is active per period
            if(resultSet.next()) {

                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                java.util.Date dateFrom = formatter.parse(resultSet.getString(2));
                double price = resultSet.getDouble(3);

                Calendar cDateFrom = Calendar.getInstance();
                cDateFrom.setTime(dateFrom);

                // Create the individual vignettes by model type
                switch (resultSet.getString(1)) {
                    case "annual-vignette":
                        vignette = new AnnualVignette(cDateFrom.get(Calendar.YEAR), cDateFrom.get(Calendar.MONTH), cDateFrom.get(Calendar.DAY_OF_MONTH), price);
                        break;

                    case "month-vignette":
                        vignette = new MonthVignette(cDateFrom.get(Calendar.YEAR), cDateFrom.get(Calendar.MONTH), cDateFrom.get(Calendar.DAY_OF_MONTH),price);
                        break;

                    case "week-vignette":
                        vignette = new WeekVignette(cDateFrom.get(Calendar.YEAR), cDateFrom.get(Calendar.MONTH), cDateFrom.get(Calendar.DAY_OF_MONTH), price);
                        break;

                    default:
                        throw new Exception("No such vehicle type! Please check the type in table vehicles.");
                }
            }

            return vignette;

        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    /**
     * Gets the vehicle's (specified by registration) tax
     *
     * @param registration the vehicle's registration
     * @return the vehicle tax
     */
    public VehicleTax getTaxForVehicle(String registration){
        String sql = "Select dateTo, price from taxes where vehicle_registration = ? And taxes.type = 'tax' ";

        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,registration);

            resultSet = preparedStatement.executeQuery();
            VehicleTax tax = null;

            if(resultSet.next()){
                System.out.println("inside result of taxes");
                tax = new VehicleTax();

                tax.setAmount(resultSet.getDouble(2));

                Calendar endDate = Calendar.getInstance();
                // Adding because of SQLite
                SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
                endDate.setTime(formatter.parse(resultSet.getString(1)));
                Calendar check = Calendar.getInstance();
                if (endDate.after(check) || endDate.compareTo(check) == 0) {
                    System.out.println(endDate.get(Calendar.DAY_OF_MONTH));
                    tax.setEndDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                }
            }

            return tax;

        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Gets the insurance data for the specified by 'registration' vehicle from db.
     *
     * No that the returned insurance may have expired.
     *
     * @param registration the vehicles's registration
     * @return an Insurance object
     */
    public Insurance getInsuranceForVehicle(String registration){

            try {
                String getInsuranceDataSql = "select type, price, dateFrom From taxes Where vehicle_registration = ? And type Like '%insurance' And dateFrom = (SELECT max(dateFrom) FROM taxes where type Like '%insurance');";
                preparedStatement = connect.prepareStatement(getInsuranceDataSql);


                // Get all other insurance data
                preparedStatement.setString(1, registration);
                resultSet = preparedStatement.executeQuery();

//            if(!resultSet.next()) {
//                return null;
//            }
                Insurance insurance = null;
                if (resultSet.next()) {
                    double insurancePrice = resultSet.getDouble(2);
                    Insurance.Payments payments = null;
                    switch (resultSet.getString(1)) {
                        case "1-insurance":
                            payments = Insurance.Payments.ONE;
                            break;
                        case "2-insurance":
                            payments = Insurance.Payments.TWO;
                            break;
                        case "3-insurance":
                            payments = Insurance.Payments.THREE;
                            break;
                        case "4-insurance":
                            payments = Insurance.Payments.FOUR;
                            break;
                    }

                    insurance = new Insurance();
                    insurance.setPrice(insurancePrice);
                    insurance.setTypeCount(payments);

                    Calendar endDate = Calendar.getInstance();
                    String startingDate = resultSet.getString(3);
                    String[] dates = startingDate.split("-");
                    Calendar startDate = Calendar.getInstance();
                    startDate.set(Integer.parseInt(dates[0]),Integer.parseInt(dates[1]),Integer.parseInt(dates[2]));
                    endDate.setTime(startDate.getTime());
                    insurance.setStartDate(endDate.get(Calendar.YEAR), endDate.get(Calendar.MONTH), endDate.get(Calendar.DAY_OF_MONTH));
                }

                // TODO discuss Insurance may have expired, could check validity here - the external service will be monitoring everything important with data. This app serves only as a client :)
                return insurance;
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
            }
    }

    /**
     * Gets all users from db and adds them to the registeredUsers collection in the UserManager class.
     */
    public void getAndStoreAllUsers(){
        // Get all registered users
        String sql = "Select * from users";

        UserManager manager = UserManager.getInstance();

        try {
            preparedStatement = connect.prepareStatement(sql);

            resultSet = preparedStatement.executeQuery();

            while(resultSet.next()){
                 manager.addToRegisteredUsers(resultSet.getString(1),
                        resultSet.getString(2),
                        resultSet.getInt(3));

            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Finds if there is logged user.
     */
    public String[] findLoggedUser() throws SQLException {
        String sql = "select username,password,userAge from users where isLogged = 1";
        resultSet = statement.executeQuery(sql);
        if (resultSet.next()) {
            return new String[]{resultSet.getString(1),resultSet.getString(2),resultSet.getString(3)};
        }
        else return null;
    }

    /**
     * Adds a vehicle to the db including the tax and insurance.
     *
     *
     * @param loggedUserName the user who ones the vehicle
     * @param x the user's vehicle
     * @return true if the vehicle was added successfully
     */
    public boolean addVehicle(String loggedUserName, Vehicle x) {
        // By default it should be the logged user !
        String sql = "insert into vehicles(registration,ownerID,brand,model,type,body_type,engine_type,rangeKm,image_path,productionYear,nextOilChange)" + "values(?,?,?,?,?,?,?,?,?,?,?)";
        try {

            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,x.getRegistrationPlate());
            preparedStatement.setString(2,loggedUserName);
            preparedStatement.setString(3,x.getBrand());
            preparedStatement.setString(4,x.getModel());
            preparedStatement.setInt(11,x.getNextOilChange());
            if (x instanceof Car){
                Car c = (Car) x;

                preparedStatement.setString(5,"Car");
                preparedStatement.setString(6,c.getCarType());
                preparedStatement.setString(7,c.getEngineType());
                preparedStatement.setString(8,c.getKmRange());
                preparedStatement.setString(9,c.getPathToImage());
                preparedStatement.setInt(10,c.getProductionYear());
            }
            else {
                Motorcycle m = (Motorcycle) x;

                preparedStatement.setString(5,"Motorcycle");
                preparedStatement.setString(6,m.getMotorcycleType());
                preparedStatement.setString(7,m.getEngineType());
                preparedStatement.setString(8,m.getKmRange());
                preparedStatement.setString(9,m.getPathToImage());
                preparedStatement.setInt(10,m.getProductionYear());
            }

            int added = preparedStatement.executeUpdate();
            if (added > 0){
                // Add tax as well
                if(!addTaxesByVehicle(x.getRegistrationPlate(), x.getTax())){
                    // TODO maybe return false and show error popup inside addCar/Motorcycle controller
                    System.out.println("Tax not added !");
                    Logger.getGlobal().log(Level.SEVERE, "Error adding tax to db!");
                }

                // Add insurance as well
                if(!addInsuranceForVehicle(x.getRegistrationPlate(),x.getInsurance())){
                    // TODO maybe return false and show error popup inside addCar/Motorcycle controller
                    System.out.println("Insurance not added");
                    Logger.getGlobal().log(Level.SEVERE, "Error adding insurance to db!");
                }

                return true;
            }

            else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    /**
     * Adds a vignette for the specified car in the db.
     * A transaction is used so if any errors occur no data will be saved to the db.
     *
     * @param registration the unique identifier of the vehicle
     * @param vignette the vignette object to be inserted
     * @return true if the vignette was added false otherwise
     */
    public boolean addVignetteForVehicle(String registration,IVignette vignette){
        String sql = "Insert into taxes(vehicle_registration,type,dateFrom,dateTo,price) values(?,?,?,?,?)";

        try {
            // Sets up a transaction
            connect.setAutoCommit(false);

            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,registration);
            preparedStatement.setString(3, vignette.getStartDate());
            preparedStatement.setString(4,vignette.getEndDate());
            preparedStatement.setDouble(5,vignette.getPrice());

            switch(vignette.getType()){
                case "Annual":
                    preparedStatement.setString(2,"annual-vignette");
                    break;
                case "Month":
                    preparedStatement.setString(2,"month-vignette");
                    break;
                case "Week":
                    preparedStatement.setString(2,"week-vignette");
                    break;

            }

            int added = preparedStatement.executeUpdate();
            if (added > 0){
                // Commits the changes if everything is ok
                connect.commit();
                return true;
            }
            else{
                // Rolls back otherwise
                connect.rollback();
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();

            if (connect != null) {
                try {
                    System.err.print("Transaction is being rolled back");
                    connect.rollback();
                } catch(SQLException err) {
                    err.printStackTrace();
                }
            }

            return false;
        }finally {
            // In case an exception is thrown before the end of the first try block, setAutocommit(true) in finally block
            try {
                connect.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }

    private boolean addTaxesByVehicle(String registration, Tax tax){
        String sql = "Insert into taxes(vehicle_registration, type, dateFrom,dateTo, price) values(?,?,?,?,?)";

        //String dateFrom = new SimpleDateFormat("yyyy-MM-dd").format(Calendar.getInstance().getTime());

        try {
            preparedStatement = connect.prepareStatement(sql);

            preparedStatement.setString(1,registration);
            preparedStatement.setString(2,"tax");
            preparedStatement.setString(3,tax.getEndDate());
            preparedStatement.setString(4,tax.getEndDate());
            preparedStatement.setDouble(5,tax.getAmount());

            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean addInsuranceForVehicle(String registration, Insurance insurance){
        String sql = "Insert into taxes(vehicle_registration, type, dateFrom,dateTo, price) values(?,?,?,?,?)";

        try {
            preparedStatement = connect.prepareStatement(sql);

            preparedStatement.setString(1,registration);

            switch (insurance.getTypeCount()){
                case 1: preparedStatement.setString(2,"1-insurance"); break;
                case 2: preparedStatement.setString(2,"2-insurance"); break;
                case 3: preparedStatement.setString(2,"3-insurance"); break;
                case 4: preparedStatement.setString(2,"4-insurance"); break;
            }

            preparedStatement.setString(3,insurance.getStartDate());
            // Ignore end date because it is calculated inside insurance
            preparedStatement.setString(4,"2017-1-1");
            preparedStatement.setDouble(5,insurance.getPrice());

            // TODO : check bug when editing not creating ... parameter 2 no value specified ...

            preparedStatement.executeUpdate();

            return true;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }


    /**
     * Deletes every type of tax (vignettes, insurance, tax) for this vehicle.
     * Note that first the records from taxes should be deleted and them the records from vehicles because of integrity
     *
     * @param registration the unique identifier of the vehicle
     */
    private void deleteTaxesForVehicle(String registration){
        String sql = "Delete From taxes Where vehicle_registration = ?";

        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,registration);

            preparedStatement.executeUpdate();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Removes a vehicle from the vehicles table using registration.
     * Note that the method also removes all the vehicle's taxes.
     *
     * @param registration the vehicle's registration number
     * @return true if operation wa successful false otherwise
     */
    public boolean removeVehicle(String registration){
        String sql = "Delete From vehicles Where registration = ?";

        try {
            // First delete all the taxes for the vehicle
            deleteTaxesForVehicle(registration);

            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,registration);

            preparedStatement.executeUpdate();

            return true;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }
}
