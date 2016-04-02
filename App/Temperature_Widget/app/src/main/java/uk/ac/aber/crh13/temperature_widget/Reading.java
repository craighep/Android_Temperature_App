package uk.ac.aber.crh13.temperature_widget;

/**
 * Created by Craig on 01/04/2016.
 * Stores a single temperature reading received from the api call. Holds the hour and minute of the
 * reading, and the temperature at that point.
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

    /**
     * Gets the hour of the reading.
     * @return hour
     */
    public int getHour(){
        return this.hour;
    }

    /**
     * Gets the minute of the reading.
     * @return minute
     */
    public int getMinute(){
        return this.min;
    }

    /**
     * Gets the temperature of the reading.
     * @return temperature
     */
    public float getTemperature(){
        return this.temp;
    }

}
