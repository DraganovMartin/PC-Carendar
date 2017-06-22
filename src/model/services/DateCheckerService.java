package model.services;

import java.util.Calendar;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

/**
 * Created by DevM on 6/22/2017.
 */
public class DateCheckerService extends TimerTask {
    private final Timer timer;
    DateCheckerService(){
        super();
        timer = new Timer("DateChecker");
    }
    @Override
    public void run() {
        while (true){

        }
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
                PM12.set(Calendar.HOUR_OF_DAY, interval);
                PM12.set(Calendar.SECOND, 0);
                break;
            case "day":
                break;
            case "week":
                break;
            case "month":
                break;
            case "year":
                break;

                default:
                    PM12.set(Calendar.HOUR_OF_DAY, 12);
                    PM12.set(Calendar.SECOND, 0);
        }

        timer.schedule(this,new Date(),TimeUnit.MILLISECONDS.convert(1, TimeUnit.DAYS));
    }
}
