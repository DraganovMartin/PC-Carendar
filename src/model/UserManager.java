package model;

//import android.content.Context;
//import android.content.Intent;
//
//import com.carcalendar.dmdev.carcalendar.services.StorageManager;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import database.Database;
import model.Vehicle.Vehicle;
import model.authentication.IUserAuthenticator;
import model.authentication.UsedUsernameException;

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
     * Increments userId and passes it to the created user.
     */
    public User createUser(String name,String password, int age){
        database.createUser(name,password,age);
        return new User(name,password,age);
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

    /**
     * IF YOU UPDATE USERMANAGER FROM FILE YOU MUST USE ONLY THIS METHOD AS A SOLUTION !!!
     * @param x - UserManager Singleton
     */
    public void updateFromFile(UserManager x){
        this.loggedUser = x.loggedUser;
        this.registeredUsers = x.registeredUsers;
    }

    /**
     *  ANDROID SPECIFIC !!!
     *  Serializing the UserManager object to internal storage  with openFileOutput()
     * @param x - UserManager
     */
//    public static void saveDataUserManager(Context context, final UserManager x){
//        Intent intent = new Intent(context,StorageManager.class);
//        intent.putExtra(SAVE_USER_MANAGER,x);
//        context.startService(intent);
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
     * @return true if mail and password are good, false otherwise
     */
    @Override
    public boolean validateRegister(String username, String password) throws UsedUsernameException{

        if(isPasswordGood(password)) {
            return database.availableToRegister(username, password);
        }
       else {
           throw new UsedUsernameException();
       }
//        if(registeredUsers.isEmpty()){
//            return (!username.isEmpty() && isPasswordGood(password));
//        }
//        if(!registeredUsers.isEmpty()) {
//            if(!username.isEmpty() && isPasswordGood(password)){
//                for (User x : registeredUsers) {
//                    if (x.name.equals(username)) {
//                        throw new UsedUsernameException();
//                    }
//                }
//                return true;
//            }
//            else return false;
//        }
//        return false;
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
