package model.services;

import model.UserManager;
import model.Vehicle.Vehicle;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * Created by DevM on 6/22/2017.
 */
public class DateCheckerService extends TimerTask {
    private final Timer timer;
    private ArrayList<Vehicle> vehicles;
    private ArrayList<Calendar> dates;
    DateCheckerService(){
        super();
        timer = new Timer("DateChecker");
        vehicles = new ArrayList<>();
        dates = new ArrayList<>();
    }
    @Override
    public void run() {
        //UserManager.getInstance().get
    }

    /**
     * Starts the service at specified hour and repeats at the given interval
     * @param whatHour - hour to start the service - 0 to 24
     * @param interval - interval to repeat it by given repeatInfo value
     * @param repeatInfo - Choose the type of repeating, e.g hour,day,week,month or year
     */
    public void startService(int whatHour,int interval,String repeatInfo){
        Calendar PM12 = Calendar.getInstance();
        // TODO : implement left out cases
        switch (repeatInfo.toLowerCase()){
            case "hour":
                PM12.set(Calendar.HOUR_OF_DAY, whatHour);
                PM12.set(Calendar.SECOND, 0);
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(interval, TimeUnit.HOURS));
                break;
            case "day":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(interval, TimeUnit.DAYS));
                break;
            case "week":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                int weeks = interval * 7;
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(weeks, TimeUnit.DAYS));
                break;
            case "month":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                // TODO : get days of month
               // int month = interval * PM12.
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(interval, TimeUnit.DAYS));
                break;
            case "year":
                PM12.set(Calendar.HOUR_OF_DAY,whatHour);
                PM12.set(Calendar.SECOND,0);
                //TODO : get days of week
                timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(interval, TimeUnit.DAYS));
                break;

                default:
                    PM12.set(Calendar.HOUR_OF_DAY, 12);
                    PM12.set(Calendar.SECOND, 0);
                    timer.schedule(this,PM12.getTime(),TimeUnit.MILLISECONDS.convert(12, TimeUnit.HOURS));
        }


    }
}
