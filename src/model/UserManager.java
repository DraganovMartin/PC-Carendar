package model;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import database.Database;
import model.Stickers.IVignette;
import model.Vehicle.Car;
import model.Vehicle.Motorcycle;
import model.Vehicle.Vehicle;
import model.authentication.IUserAuthenticator;
import model.authentication.WeakPassException;

/**
 * Singleton class UserManager for creating, registering and managing users.
 */
public class UserManager implements IUserAuthenticator,Serializable {

    private static final UserManager manager = new UserManager();
    public static final String SAVE_USER_MANAGER = "USER_MANAGER_SAVE";
    public static UserManager getInstance() {
        return manager;
    };
    private ArrayList<User> registeredUsers;
    private User loggedUser;

    private Database database = null;

    private UserManager() {

        registeredUsers = new ArrayList<>();
        loggedUser = null;
        database = Database.getInstance();
    }

    /**
     * Creates user in database and returns User object.
     */
    public User createUser(String name,String password, int age){
        database.createUser(name,password,age);
        return new User(name,password,age);
    }

    /**
     * Sets the loggedUser with the current user
     * @param username
     * @param pass
     * @return true if the user was logged in false otherwise
     */
    public boolean loginUser(String username, String pass){
        int userAge = database.logInUser(username,pass);
        if(userAge == -1){
            return false;
        }

        loggedUser = new User(username,pass,userAge);
        return true;
    }

    /**
     * @return true if logged user is found and set adequate. False otherwise.
     */
    public boolean setLoggedUserFromInitialLoading(){
        String name,pass;
        int age;
        try {
            String[] data = database.findLoggedUser();
            if (data != null) {
                name = data[0];
                pass = data[1];
                age = Integer.valueOf(data[2]);
                loggedUser = new User(name, pass, age);
                return true;
            }
            else return false;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }

    }

    private boolean isPasswordGood(String password){
        final Pattern passPattern = Pattern.compile( "((?=.*\\d)(?=.*[a-z])(?=.*[A-Z]).{6,20})");
        Matcher matcher = passPattern.matcher(password);
        return matcher.matches();
    }

    public void registerUser(User x){
        registeredUsers.add(x);
    }

    public void addVehicle(Vehicle x){
        loggedUser.addVehicle(x);
        database.addVehicle(getLoggedUserName(),x);
    }


    /**
     * Loads all the vehicles for the currently logged user from db.
     */
    public void loadLoggedUserVehicles(){
       List<Vehicle> vehicles =  database.getLoggedUserVehicles(loggedUser.name);

       if(vehicles != null)
           loggedUser.loadAllVehicles(vehicles);
    }

    /**
     * Loads the currently logged user's car vignettes
     */
    public void loadCarVignettes(){
        List<Car> cars = loggedUser.ownedVehicles.stream()
                                .filter((v) -> v instanceof Car)
                                .map((v) -> (Car) v)
                                .collect(Collectors.toList());

        for(Car c : cars){
           IVignette vignette = database.getVignetteForVehicle(c.getRegistrationPlate());

           // Add only if there is an active vignette
           if(vignette != null)
               c.setVignette(vignette);
        }
    }

    public void removeVehicle(Vehicle v,boolean removeImageAlso){
        loggedUser.removeVehicle(v,removeImageAlso);
    }

    public String getLoggedUserName() {
       return loggedUser.name;
   }

    public User getLoggedUser() {
        return loggedUser;
    }

    public List<Vehicle> getRegisteredUserVehicles(){
        return new ArrayList<>(loggedUser.ownedVehicles);
    }


    /**
     *  setting "loggedUser" to null !!
     */
    public void userLogout(){
        loggedUser = null;
    }

//    /**
//     * IF YOU UPDATE USERMANAGER FROM FILE YOU MUST USE ONLY THIS METHOD AS A SOLUTION !!!
//     * @param x - UserManager Singleton
//     */
//    public void updateFromFile(UserManager x){
//        this.loggedUser = x.loggedUser;
//        this.registeredUsers = x.registeredUsers;
//    }


    /**
     *
     * @param username - String
     * @param password - String
     * @return true if login details are correctly entered, false otherwise
     */
    @Override
    public boolean authenticateLogin(String username, String password)
    {
        for (User x: registeredUsers) {
            if(x.name.equals(username) && x.password.equals(password)){
                loggedUser = x;
                return true;
            }
        }
        return false;
    }

    /**
     *
     * @param username - String
     * @param password - String
     * @return true if username and password are good, false otherwise
     */
    @Override
    public boolean validateRegister(String username, String password) throws WeakPassException{

        if(isPasswordGood(password)) {
            return database.availableToRegister(username, password);
        }
       else {
           throw new WeakPassException();
       }

    }

    private class User {
        private String name;
        private String password;
        private int age;
        private TreeSet<Vehicle> ownedVehicles;

        private User(String name, String password, int age) {
            this.name = name;
            this.age = age;
            this.password = password;
            ownedVehicles = new TreeSet<>();
        }
//        private User(User x){
//            this.name = x.name;
//            this.age = x.age;
//            this.password = x.password;
//            ownedVehicles = x.ownedVehicles;
//            this.id = x.id;
//        }

         void addVehicle(Vehicle x) {
            if (x != null) {
                ownedVehicles.add(x);
            } else {
                throw new NullPointerException();
            }
        }

        public void loadAllVehicles(List<Vehicle> vehicles){
            ownedVehicles.addAll(vehicles);
        }

        public void removeVehicle(Vehicle x,boolean removeImageAlso) {
            if (ownedVehicles.contains(x)) {
                ownedVehicles.remove(x);
                if (removeImageAlso) {
                    removeVehicleImage(x.getPathToImage());
                }
            }
        }

        public void removeVehicleImage(String pathToImage){
            if(pathToImage != null) {
                File imageofCar = new File(pathToImage);
                if (!imageofCar.isDirectory() && imageofCar.length() > 0) imageofCar.delete();
            }
        }

    }
}
