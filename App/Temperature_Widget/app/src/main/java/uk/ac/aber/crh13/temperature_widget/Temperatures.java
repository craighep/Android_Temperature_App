package uk.ac.aber.crh13.temperature_widget;

/**
 * Created by Craig on 01/04/2016.
 * Holds all data received from the APIs. Contains an array of Readings, and the current time.
 * Useful methods are also provided to get the minimum and maximum temperatures in the data, and the
 * average temperature in the previous hour.
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

    /**
     * Returns all the readings
     * @return Readings
     */
    public Reading[] getReadings(){
        return this.readings;
    }

    /**
     * Returns the last recorded time from the API
     * @return currentTime
     */
    public String getCurrentTimeString(){
        return this.currentTimeString;
    }

    /**
     * Returns the number of readings
     * @return numberOfReadings
     */
    public int numOfReadings(){
        return this.readings.length;
    }

    /**
     * Returns the average temperature for the past hour
     * @return average
     */
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

    /**
     * Returns the minimum temperature for all the readings
     * @return minimum
     */
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

    /**
     * Returns the maximum temperature for all the readings
     * @return maximum
     */
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
