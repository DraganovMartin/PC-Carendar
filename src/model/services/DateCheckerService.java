package model.services;

import model.UserManager;
import model.Vehicle.Vehicle;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by DevM on 6/22/2017.
 */
public class DateCheckerService extends TimerTask {
    private final Timer timer;
    private final UserManager manager;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<Calendar> dates;
    public DateCheckerService(){
        super();
        timer = new Timer("DateChecker");
        manager =  UserManager.getInstance();
        vehicles = new ArrayList<>();
        dates = new ArrayList<>();
    }
    @Override
    public void run() {
        // Loads all users and vehicles in the UserManager
        manager.loadAllUsersAndVehicles();
        // Gets all the vehicles from the UserManager
        vehicles.addAll(manager.getAllVehiclesOfAllUsers());

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
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(interval,TimeUnit.HOURS));
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
