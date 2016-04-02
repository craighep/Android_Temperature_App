package uk.ac.aber.crh13.tempreture_widget;

import java.math.BigDecimal;

/**
 * Created by Craig on 01/04/2016.
 */
public class Temperatures {
    private Reading[] readings;
    private String currentTimeString;

    public Temperatures(Reading[] readings, String currentTimeString){
        this.readings = readings;
        if(currentTimeString.length() < 5)
            currentTimeString = "0" + currentTimeString; // to keep in trend with the 24 hour format.
        this.currentTimeString = currentTimeString;
    }

    public Reading[] getReadings(){
        return this.readings;
    }

    public String getCurrentTimeString(){
        return this.currentTimeString;
    }

    public int numOfReadings(){
        return this.readings.length;
    }

    public float getAverageTemperaturePastHour(){
        if(numOfReadings() < 1)
            return 0;
        int loopNum = 13; // In order to loop an hour
        if(numOfReadings() < 13)
            loopNum = numOfReadings(); // otherwise get all data available
        float total = 0;
        for(int i=1;i<loopNum;i++){
            total += readings[numOfReadings()-i].getTemperature();
        }
        return total / 13;
    }

    public float getMinimumTemperature(){
        if(numOfReadings() < 1)
            return 0;
        float lowest = readings[0].getTemperature();
        for (int i = 1; i < readings.length; i++) {
            if (readings[i].getTemperature() < lowest)
                lowest = readings[i].getTemperature();
        }
        return lowest;
    }

    public float getMaximumTemperature(){
        if(numOfReadings() > 0) {
            float highest = readings[0].getTemperature();
            for (int i = 1; i < readings.length; i++) {
                if (readings[i].getTemperature() > highest)
                    highest = readings[i].getTemperature();
            }
            return highest;
        }
        return 0;
    }
}
