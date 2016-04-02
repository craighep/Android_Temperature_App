package uk.ac.aber.crh13.temperature_widget;

/**
 * Created by Craig on 01/04/2016.
 * Stores any app preferences, such as the sensor selection or the unit to show the temperature in.
 */
public class Preferences {
    private int sensorNumber = 1;
    private boolean isFahrenheit = false;

    public Preferences(int sensorNumber, boolean isFahrenheit){
        this.sensorNumber = sensorNumber;
        this.isFahrenheit = isFahrenheit;
    }

    /**
     * Gets the sensor selected.
     * @return sensorNumber
     */
    public int getSensorNumber(){
        return this.sensorNumber;
    }

    /**
     * Gets the unit selected.
     * @return isFahrenheit
     */
    public Boolean isFahrenheit(){
        return this.isFahrenheit;
    }
}
