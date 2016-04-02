package uk.ac.aber.crh13.temperature_widget;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Craig on 01/04/2016.
 * Class stores and recieves data from SharedPreferences. Uses the Preferences object in this project
 * to insert key value pairs into SharedPreferences for this application. Loads and re-creates a
 * Preferences object to recover constant data.
 */
public class DataHandler {
    private static final String PREFS_NAME = "uk.ac.aber.crh13.temperature_widget.TemperatureMonitor";
    private static final String FAHRENHEIT_KEY = "UNIT";
    private static final String SENSOR_KEY = "SENSOR";

    /** Write the prefix to the SharedPreferences object for this widget
     * Uses the package name of the app in order to be unique. Writes Preferences object elements to
     * key value pairs.
     * @param context Context
     * @param preferences Preferences
     */
    public void savePref(Context context, Preferences preferences) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(SENSOR_KEY, preferences.getSensorNumber());
        prefs.putBoolean(FAHRENHEIT_KEY, preferences.isFahrenheit());
        prefs.apply();
    }

    /** Read the prefix from the SharedPreferences object for this widget.
     * If there is no preference saved, get the default from a resource
     * @param context Context
     * @return Preferences
     */
    public Preferences loadPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int sensor = prefs.getInt(SENSOR_KEY, 1);
        boolean isFahrenheit = prefs.getBoolean(FAHRENHEIT_KEY, false);
        return new Preferences(sensor, isFahrenheit);
    }

    /**
     * Deletes the shared preferences for this app.
     * @param context Context
     * @param appWidgetId widgetID
     */
    public void deletePrefs(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(FAHRENHEIT_KEY + appWidgetId);
        prefs.remove(SENSOR_KEY + appWidgetId);
        prefs.apply();
    }
}
