package uk.ac.aber.crh13.tempreture_widget;

/**
 * Created by Craig on 01/04/2016.
 */
public class Preferences {
    private int sensorNumber = 1;
    private boolean isFahrenheit = false;
    private boolean autoUpdate = false;

    public Preferences(int sensorNumber, boolean isFahrenheit, boolean autoUpdate){
        this.sensorNumber = sensorNumber;
        this.isFahrenheit = isFahrenheit;
        this.autoUpdate = autoUpdate;
    }

    public int getSensorNumber(){
        return this.sensorNumber;
    }

    public Boolean isFahrenheit(){
        return this.isFahrenheit;
    }

    public Boolean isAutoUpdate(){
        return this.autoUpdate;
    }
}
