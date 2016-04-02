package uk.ac.aber.crh13.tempreture_widget;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Craig on 01/04/2016.
 */
public class DataHandler {
    private static final String PREFS_NAME = "uk.ac.aber.crh13.tempreture_widget.TemperatureMonitor";
    private static final String FAHRENHEIT_KEY = "UNIT";
    private static final String AUTO_UPDATE_KEY = "AUTOUPDATE";
    private static final String SENSOR_KEY = "SENSOR";

    // Write the prefix to the SharedPreferences object for this widget
    public void savePref(Context context, Preferences preferences) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.putInt(SENSOR_KEY, preferences.getSensorNumber());
        prefs.putBoolean(FAHRENHEIT_KEY, preferences.isFahrenheit());
        prefs.putBoolean(AUTO_UPDATE_KEY, preferences.isAutoUpdate());
        prefs.apply();
    }

    // Read the prefix from the SharedPreferences object for this widget.
    // If there is no preference saved, get the default from a resource
    public Preferences loadPref(Context context) {
        SharedPreferences prefs = context.getSharedPreferences(PREFS_NAME, 0);
        int sensor = prefs.getInt(SENSOR_KEY, 1);
        boolean isFahrenheit = prefs.getBoolean(FAHRENHEIT_KEY, false);
        boolean autoUpdate = prefs.getBoolean(AUTO_UPDATE_KEY, false);
        return new Preferences(sensor, isFahrenheit, autoUpdate);
    }

    public void deleteTitlePref(Context context, int appWidgetId) {
        SharedPreferences.Editor prefs = context.getSharedPreferences(PREFS_NAME, 0).edit();
        prefs.remove(FAHRENHEIT_KEY + appWidgetId);
        prefs.remove(SENSOR_KEY + appWidgetId);
        prefs.remove(AUTO_UPDATE_KEY + appWidgetId);
        prefs.apply();
    }
}
