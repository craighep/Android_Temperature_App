package uk.ac.aber.crh13.temperature_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.view.View;
import android.widget.RemoteViews;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Implementation of App Widget functionality. Is responsible for pulling data from the server, and
 * displaying it on the widget. Also creates button intents in order to refresh data and to open
 * the configuration window.
 * App Widget Configuration implemented in {@link TemperatureMonitorConfigureActivity TemperatureMonitorConfigureActivity}
 */
public class TemperatureMonitor extends AppWidgetProvider {

    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_REFRESH = "RefreshWidget";
    public static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    /**
     * Calls methods to refresh data, update the widget view, and to set intents for both button
     * actions.
     * @param context Context
     * @param appWidgetManager AppWidgetManager
     * @param appWidgetId widgetID
     */
    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {
        // Store this for later use when refreshing data
        this.mAppWidgetId = appWidgetId;
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tempreture_monitor);

        // Sets up the settings button to open the configuration activity
        Intent configIntent = new Intent(context, TemperatureMonitorConfigureActivity.class); // Calls the config window to open
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);
        views.setOnClickPendingIntent(R.id.btnSettings, configPendingIntent);
        configIntent.setAction(ACTION_WIDGET_CONFIGURE); // Identifier for onRecieve method

        //Similarly for refresh button
        Intent intent = new Intent(context, TemperatureMonitor.class);
        intent.setAction(ACTION_WIDGET_REFRESH); // Identifier for onRecieve method
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.refreshButton, pendingIntent);

        new RefreshTemperatureData(views, appWidgetId, appWidgetManager, context).execute(); // Call method to refresh and display data
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        if(intent.getAction().equals(ACTION_WIDGET_REFRESH) ||
                intent.getAction().equals(ACTION_WIDGET_CONFIGURE)) { // Only update if called by a button, not resize
            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
            RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tempreture_monitor);
            new RefreshTemperatureData(views, this.mAppWidgetId, appWidgetManager, context).execute(); // Call method to refresh and display data
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        DataHandler dataHandler = new DataHandler();
        for (int appWidgetId : appWidgetIds) {
            dataHandler.deletePrefs(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
    }

    @Override
    public void onDisabled(Context context) {
    }

    /**
     * Inner class handles the refreshing of data from the server. Extends the AsyncTask class in order
     * to perform the refresh on another thread. Class is started via the execute method, and then
     * runs any preexecute code before performing the pull of data with the doInBackground method. Upon
     * completion, the postExecute method updates the UI of the widget.
     */
    class RefreshTemperatureData extends AsyncTask<String, Void, Temperatures> {

        private RemoteViews views;
        private int appWidgetId;
        AppWidgetManager appWidgetManager;
        private Preferences preferences;
        private Context context;

        public RefreshTemperatureData(RemoteViews remoteViews, int widgetId, AppWidgetManager widgetManager, Context activityContext){
            views = remoteViews;
            appWidgetId = widgetId;
            appWidgetManager = widgetManager;
            context = activityContext;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DataHandler dataHandler = new DataHandler();
            preferences = dataHandler.loadPref(context);
            views.setViewVisibility(R.id.pbProgess, View.VISIBLE);
            views.setViewVisibility(R.id.currentTime, View.GONE);
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        /**
         * Uses the preferences object to call one of the sensor api addresses, and then uses
         * the DocumentBuilder class to pull data from the XML received. An array of Reading
         * objects is created, and these (along with the current time element) are added to a Temperatures
         * object. This is returned ready to be used by the onPostExecute method.
         * @param urls APIs
         * @return
         */
        protected Temperatures doInBackground(String... urls) {
            List<Reading> readingList = new ArrayList<Reading>();
            String currentTimeString = "";
            try {
                String urlString = "http://users.aber.ac.uk/aos/CSM22/temp1data.php";
                if(preferences.getSensorNumber() == 2) // Differ between api addresses
                    urlString = "http://users.aber.ac.uk/aos/CSM22/temp2data.php";

                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(connection.getInputStream());
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("reading"); // Get all the Reading elements
                // loop all readings and cerate Reading objects. Add these to the list
                for (int temp = 0; temp < nList.getLength(); temp++) {
                    Node nNode = nList.item(temp);

                    if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                        Element eElement = (Element) nNode;
                        int hour = Integer.parseInt(eElement.getAttribute("hour"));
                        int min = Integer.parseInt(eElement.getAttribute("min"));
                        float temperature = Float.parseFloat(eElement.getAttribute("temp"));
                        Reading reading = new Reading(hour, min, temperature);
                        readingList.add(reading);
                    }
                }
                // Get the current time element from the XML
                NodeList currentTimeList = doc.getElementsByTagName("currentTime");
                if (currentTimeList.getLength() > 0){ // Get the first
                    Node currentTimeNode = currentTimeList.item(0);
                    currentTimeString = currentTimeNode.getTextContent();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return new Temperatures(new Reading[0], "");
            }
            // Create readings array and return new Temperatures object containing these and the current
            // time.
            Reading[] readings = readingList.toArray(new Reading[readingList.size()]);
            return new Temperatures(readings, currentTimeString);
        }

        /**
         * Takes the Temperatures gathered from the API calll, and uses these to update the app widget
         * view. Sets various text views, and also checks if there was a problem returning data. If so,
         * error is shown.
         * @param temperatures Temperatures
         */
        protected void onPostExecute(Temperatures temperatures) {
            if(temperatures.numOfReadings() < 1){
                // There was nothing returned.
                views.setTextViewText(R.id.currentTime, "No data!");
                // Remove view of progress spinner
                views.setViewVisibility(R.id.pbProgess, View.GONE);
                // And show the current time in its place
                views.setViewVisibility(R.id.currentTime, View.VISIBLE);
                appWidgetManager.updateAppWidget(appWidgetId, views); // Updates the widget view
                return;
            }
            Reading[] readings = temperatures.getReadings();
            String currentTemp = getStringValue(readings[temperatures.numOfReadings() - 1].getTemperature());
            String averageTemp = getStringValue(temperatures.getAverageTemperaturePastHour());
            String minTemp = getStringValue(temperatures.getMinimumTemperature());
            String maxTemp = getStringValue(temperatures.getMaximumTemperature());
            views.setTextViewText(R.id.currentTemp, currentTemp);
            views.setTextViewText(R.id.currentTime, temperatures.getCurrentTimeString());
            views.setTextViewText(R.id.averageTemp, averageTemp);
            views.setTextViewText(R.id.max, "\u1401" + minTemp);
            views.setTextViewText(R.id.min, "\u1403 " + maxTemp);
            views.setTextViewText(R.id.sensorLabel, "Sensor " + preferences.getSensorNumber());
            views.setViewVisibility(R.id.pbProgess, View.GONE);
            views.setViewVisibility(R.id.currentTime, View.VISIBLE);
            views.setTextViewText(R.id.pasthrLabel, "Hr average:");
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        /**
         * Takes the temperature to be shown, rounds it, adds the current unit, and converts it to
         * fahrenheit if told by the user preferences.
         * @param value Temperature
         * @return stringValue
         */
        private String getStringValue(float value){
            if(preferences.isFahrenheit())
                return String.valueOf(convertToFahrenheit(value))+" \u2109";
            return String.valueOf(Math.round(value))+" \u2103";
        }

        /**
         * Converts celcius to fahrenheit and rounds.
         * @param cel Celcius
         * @return fahrenheit
         */
        private int convertToFahrenheit(float cel){
            cel = cel * 9;
            cel = cel / 5;
            cel = cel + 32;
            return Math.round(cel);
        }
    }
}

