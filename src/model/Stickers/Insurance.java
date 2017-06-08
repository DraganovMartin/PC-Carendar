package model.Stickers;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by dimcho on 10.03.17.
 * Worked by Marto-DevM on 29.03.2017
 */

public class Insurance implements Serializable{

    private int type;
    private double price;
    private  Calendar startDate;
    private Calendar endDate;
    private Calendar[] endDates;
    public  enum Payments {
        ONE (1),
        TWO (2),
        THREE (3),
        FOUR (4);

        private final int levelCode;

        private Payments(int levelCode) {
            this.levelCode = levelCode;
        }

    }

    public Insurance(){
        Calendar initDate = Calendar.getInstance();
        endDate = initDate;
        endDates = new Calendar[4];
        startDate = initDate;
    }
    public Insurance(Payments count,double price,Calendar startDate,Calendar endDate){
        this.type = count.levelCode;
        this.price = price;
        this.startDate = startDate;
        this.endDate=endDate;
    }

    public int getTypeCount() {
        return type;
    }

    public void setTypeCount(Payments type) {
        switch (type){
            case ONE:
                this.type = type.levelCode;
                break;
            case TWO:
                this.type = type.levelCode;
                break;
            case THREE:
                this.type = type.levelCode;
                break;
            case FOUR:
                this.type = type.levelCode;
                break;
                default:
                    this.type = 0;
        }

    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        if (price >= 0.0) {
            this.price = price;
        }
    }

    public void setStartDate(int year,int month,int day){
        if (year > 0 && month >= 0 && day >= 0) {
            startDate.clear();
            endDate.clear();
            startDate.set(year, month, day);
            if (type > 0 && type == (Payments.ONE.levelCode)) {
                endDate.set(year + 1, month, day);
                endDates[0] = (Calendar) endDate.clone();
            }
            if (type > 0 && type ==(Payments.TWO.levelCode)) {
                endDate.set(year, month+6, day);
                endDates[0] = (Calendar) endDate.clone();
                endDates[1] = new Calendar.Builder().setDate(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH)+6,day).build();
            }
            if (type > 0 && type == (Payments.THREE.levelCode)) {
                endDate.set(year, month +4, day);
                endDates[0] = (Calendar) endDate.clone();
                endDates[1] = new Calendar.Builder().setDate(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH)+4,day).build();
                endDates[2] = new Calendar.Builder().setDate(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH)+4,day).build();

            }
            if (type > 0 && type == (Payments.FOUR.levelCode)) {
                endDate.set(year, month + 3, day);
                endDates[0] = (Calendar) endDate.clone();
                endDates[1] = new Calendar.Builder().setDate(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH)+3,day).build();
                endDates[2] = new Calendar.Builder().setDate(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH)+3,day).build();
                endDates[3] = new Calendar.Builder().setDate(endDate.get(Calendar.YEAR),endDate.get(Calendar.MONTH)+3,day).build();
            }
        }
    }

    public Calendar[] getEndDates() {
        return endDates;
    }
}
