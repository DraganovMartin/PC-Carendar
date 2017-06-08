package database;

import model.Stickers.AnnualVignette;
import model.Stickers.IVignette;
import model.Stickers.MonthVignette;
import model.Stickers.WeekVignette;
import model.UserManager;
import model.Vehicle.Car;
import model.Vehicle.Motorcycle;
import model.Vehicle.Vehicle;
import model.taxes.Tax;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

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
            connect = DriverManager.getConnection("jdbc:mysql://localhost:3306/carendar","carendar","carendar");
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
                 System.out.println(resultSet.getString(1) + resultSet.getString(2) + resultSet.getString(3));
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

        try {
            preparedStatement = connect.prepareStatement(sql,ResultSet.TYPE_SCROLL_INSENSITIVE,ResultSet.CONCUR_UPDATABLE);
            preparedStatement.setString(1,username);
            preparedStatement.setString(2,pass);
            resultSet = preparedStatement.executeQuery();

            if(!resultSet.first()){
                return -1;
            }
            resultSet.updateInt(3,1);
            resultSet.updateRow();

            return resultSet.getInt(2);

        }catch (SQLException e){
            e.printStackTrace();
            return -1;
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
                " rangeKm ,image_path, productionYear From vehicles Where ownerID = ?";

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

                switch(resultSet.getString(4)){
                    case "Car" :
                        vehicles.add(new Car(registration,brand,model,bodyType,engineType,range,imagePath,productionYear));
                        break;

                    case "Motorcycle" :
                        vehicles.add(new Motorcycle(registration,brand,model,bodyType,engineType,range,imagePath,productionYear));
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
        String sql = "select taxes.type, dateFrom, price from taxes where vehicle_registration = ? And taxes.type Like '%vignette' And dateTo >= NOW()";

        try {
            IVignette vignette = null;

            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1, registration);
            resultSet = preparedStatement.executeQuery();

            // Only one vignette is active per period
            if(resultSet.first()) {

                Date dateFrom = resultSet.getDate(2);
                double price = resultSet.getDouble(3);

                Calendar cDateFrom = Calendar.getInstance();
                cDateFrom.setTime(dateFrom);

                // Create the individual vignettes by model type
                switch (resultSet.getString(1)) {
                    case "annual-vignette":
                        vignette = new AnnualVignette(cDateFrom.get(Calendar.YEAR),
                                cDateFrom.get(Calendar.MONTH),
                                Calendar.DAY_OF_MONTH,
                                price);
                        break;

                    case "month-vignette":
                        vignette = new MonthVignette(cDateFrom.get(Calendar.YEAR),
                                cDateFrom.get(Calendar.MONTH),
                                Calendar.DAY_OF_MONTH,
                                price);
                        break;

                    case "week-vignette":
                        vignette = new WeekVignette(cDateFrom.get(Calendar.YEAR),
                                cDateFrom.get(Calendar.MONTH),
                                Calendar.DAY_OF_MONTH,
                                price);
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
     * Finds if there is logged user.
     */
    public String[] findLoggedUser() throws SQLException {
        String sql = "select username,password,userAge from users where isLogged = 1";
        resultSet = statement.executeQuery(sql);
        if (resultSet.first()) {
            return new String[]{resultSet.getString(1),resultSet.getString(2),resultSet.getString(3)};
        }
        else return null;
    }

    public boolean addVehicle(String loggedUserName, Vehicle x) {
        // By default it should be the logged user !
        String sql = "insert into vehicles(registration,ownerID,brand,model,type,body_type,engine_type,rangeKm,image_path,productionYear)" + "values(?,?,?,?,?,?,?,?,?,?)";
        try {
            preparedStatement = connect.prepareStatement(sql);
            preparedStatement.setString(1,x.getRegistrationPlate());
            preparedStatement.setString(2,loggedUserName);
            preparedStatement.setString(3,x.getBrand());
            preparedStatement.setString(4,x.getModel());
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
            if (added > 0) return true;
            else return false;

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean addTaxesByVehicle(Vehicle x){
        //TODO : implement it ...
        return false;
    }
}
