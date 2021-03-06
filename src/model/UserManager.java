package model;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.Database;
import model.Stickers.IVignette;
import model.Stickers.Insurance;
import model.Vehicle.Car;
import model.Vehicle.Vehicle;
import model.authentication.IUserAuthenticator;
import model.authentication.WeakPassException;
import model.taxes.Tax;
import model.taxes.VehicleTax;

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
     * Wraps the user data in a User object and stores it in the registeredUser collection
     *
     * @param name the user's username
     * @param password the user's password
     * @param age the user's age
     */
    public void addToRegisteredUsers(String name, String password, int age){
        registerUser(new User(name,password,age));
    }

    /**
     * Loads all the vehicle data for every single user
     */
    private void addVehiclesForRegisteredUsers(){
        for(User user : registeredUsers){
            loadVehiclesForUser(user);
        }
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

    // Adds cars and vignettes separately for now
    // If there was an error during vignette addition and no vignette was added the user can edit the vehicle later and add a vignette
    public boolean addVehicle(Vehicle x){

        // Adds the tax and insurance
        if(database.addVehicle(getLoggedUserName(),x)){
            // First add the vehicle
            loggedUser.addVehicle(x);

            // Save vignettes for Cars only and skip motorcycles
            if(x instanceof Car){
                IVignette vignette = ((Car)(x)).getVignette();
                // If no vignette was added skip adding a vignette
                if (vignette != null){
                   return database.addVignetteForVehicle(x.getRegistrationPlate(),vignette);
                }
            }

            return true;
        }

        return false;
    }


    /**
     * Loads all the vehicles for the currently logged user from db.
     * Loads vignettes, taxes and insurance as well.
     */
    public void loadVehiclesForUser(User user){
        List<Vehicle> vehicles =  database.getLoggedUserVehicles(user.name);
        System.out.println("vehicle size in usermanager : " + vehicles.size());
        if(vehicles != null) {
           for (Vehicle v : vehicles){
               // Load vignettes
               if(v instanceof Car){
                   IVignette vignette = database.getVignetteForVehicle(v.getRegistrationPlate());

                   // Add only if there is an active vignette
                   if(vignette != null)
                       ((Car) v).setVignette(vignette);
               }

               // Load tax
               Tax tax = database.getTaxForVehicle(v.getRegistrationPlate());
               // Load insurance
               Insurance insurance = database.getInsuranceForVehicle(v.getRegistrationPlate());


               if(tax != null)
                   v.setTax((VehicleTax) tax);

               // Insurance may have expired
               if(insurance != null)
                   v.setInsurance(insurance);
           }

           user.loadAllVehicles(vehicles);
       }

    }

    public void loadAllUsersAndVehicles(){
        // Clear any loaded users and data then reload them again from db
        registeredUsers.clear();

        database.getAndStoreAllUsers();
        addVehiclesForRegisteredUsers();
    }

    public List<Vehicle> getAllVehiclesOfAllUsers(){
        List<Vehicle> allVehicles = new ArrayList<>(registeredUsers.size() * 10);
        for (User user : registeredUsers){
            allVehicles.addAll(user.ownedVehicles);
        }

        return  allVehicles;
    }

    public void removeVehicle(Vehicle v,boolean removeImageAlso) throws Exception{
        boolean result =  database.removeVehicle(v.getRegistrationPlate());

        if(result)
            // Removes taxes (vignettes, insurance) as well
            loggedUser.removeVehicle(v, removeImageAlso);
        else
            throw new Exception("Error deleting vehicle from db!");
    }


    public String getLoggedUserName() {
        if(loggedUser == null)
            return null;

        return loggedUser.name;
   }

    public User getLoggedUser() {
        return loggedUser;
    }

    /**
     * Gets the owner of the specified vehicle.
     *
     * @param vehicle the vehicle for which to find the owner
     * @return the owner's username
     * @throws Exception when no registered users are stored in the UserManager or if no owner was found.
     */
    public String getVehicleOwnerUsername(Vehicle vehicle) throws Exception{
        if(registeredUsers == null){
            throw new Exception("No users stored in UserManager. Please call loadAllUsersAndVehicles() method first!");
        }

        for(User user : registeredUsers){
            if(user.ownedVehicles.contains(vehicle)){
                return user.name;
            }
        }

        throw new Exception("WTF Exception: no vehicle owner found!");

    }

    public List<Vehicle> getRegisteredUserVehicles() {
        // Couldn't figure out why the fuck we need cached user from service, but go on the service is decoupled :)
//        if (registeredUsers != null && loggedUser.ownedVehicles.size() <= 1){
//            // Use cached user from service
//            for (User user : registeredUsers) {
//                if (user.name.equals(loggedUser.name)) {
//                    System.out.println("getRegisteredUserVehicles value : " + user.ownedVehicles.size());
//                    return new ArrayList<>(user.ownedVehicles);
//                }
//            }
//        }else {
            return new ArrayList<>(loggedUser.ownedVehicles);
        //}

        //return null;
    }


    /**
     *  Logout user from database
     *  setting "loggedUser" to null !!
     */
    public boolean userLogout(){
        if(loggedUser == null)
            return true;

        if(!database.logOutUser(loggedUser.name))
            return false;

        loggedUser = null;
        return true;
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
//        for (User x: registeredUsers) {
//            if(x.name.equals(username) && x.password.equals(password)){
//                loggedUser = x;
//                return true;
//            }
//        }
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

         public void addVehicle(Vehicle x) {
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
