package uk.ac.aber.crh13.tempreture_widget;

/**
 * Created by Craig on 01/04/2016.
 */
public class Reading {

    private int hour;
    public int min;
    public float temp;

    public Reading(int hour, int min, float temp){
        this.hour = hour;
        this.min = min;
        this.temp = temp;
    }

    public int getHour(){
        return this.hour;
    }

    public int getMinute(){
        return this.min;
    }

    public float getTemperature(){
        return this.temp;
    }

}
