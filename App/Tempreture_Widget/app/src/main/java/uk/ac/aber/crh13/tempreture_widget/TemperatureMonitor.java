package uk.ac.aber.crh13.tempreture_widget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.widget.RemoteViews;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.math.BigDecimal;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

/**
 * Implementation of App Widget functionality.
 * App Widget Configuration implemented in {@link TemperatureMonitorConfigureActivity TemperatureMonitorConfigureActivity}
 */
public class TemperatureMonitor extends AppWidgetProvider {

    public static String ACTION_WIDGET_CONFIGURE = "ConfigureWidget";
    public static String ACTION_WIDGET_REFRESH = "RefreshWidget";
    public static int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    public void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                       int appWidgetId) {

        // Construct the RemoteViews object
        this.mAppWidgetId = appWidgetId;
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tempreture_monitor);

        // Sets up the settings button to open the configuration activity
        Intent configIntent = new Intent(context, TemperatureMonitorConfigureActivity.class);
        configIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
        PendingIntent configPendingIntent = PendingIntent.getActivity(context, appWidgetId, configIntent, 0);
        views.setOnClickPendingIntent(R.id.btnSettings, configPendingIntent);
        configIntent.setAction(ACTION_WIDGET_CONFIGURE + Integer.toString(appWidgetId));

        //for refresh button
        Intent intent = new Intent(context, TemperatureMonitor.class);
        intent.setAction(ACTION_WIDGET_CONFIGURE);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(context, 0, intent, 0);
        views.setOnClickPendingIntent(R.id.refreshButton, pendingIntent);

        new RefreshTemperatureData(views, appWidgetId, appWidgetManager, context).execute();
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
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.tempreture_monitor);

        new RefreshTemperatureData(views, this.mAppWidgetId, appWidgetManager, context).execute();
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        // When the user deletes the widget, delete the preference associated with it.
        DataHandler dataHandler = new DataHandler();
        for (int appWidgetId : appWidgetIds) {
            dataHandler.deleteTitlePref(context, appWidgetId);
        }
    }

    @Override
    public void onEnabled(Context context) {
        // Enter relevant functionality for when the first widget is created
    }

    @Override
    public void onDisabled(Context context) {
        // Enter relevant functionality for when the last widget is disabled
    }

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
        }

        protected Temperatures doInBackground(String... urls) {
            List<Reading> readingList = new ArrayList<Reading>();
            String currentTimeString = "";
            try {
                String urlString = "http://users.aber.ac.uk/aos/CSM22/temp1data.php";
                if(preferences.getSensorNumber() == 2)
                    urlString = "http://users.aber.ac.uk/aos/CSM22/temp2data.php";

                URL url = new URL(urlString);
                URLConnection connection = url.openConnection();
                DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
                Document doc = dBuilder.parse(connection.getInputStream());
                doc.getDocumentElement().normalize();
                NodeList nList = doc.getElementsByTagName("reading");

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
                NodeList currentTimeList = doc.getElementsByTagName("currentTime");
                if (currentTimeList.getLength() > 0){
                    Node currentTimeNode = currentTimeList.item(0);
                    currentTimeString = currentTimeNode.getTextContent();
                }

            } catch (Exception e) {
                e.printStackTrace();
                return new Temperatures(new Reading[0], "");
            }
            Reading[] readings = readingList.toArray(new Reading[readingList.size()]);
            return new Temperatures(readings, currentTimeString);
        }

        protected void onPostExecute(Temperatures temperatures) {
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
            views.setTextViewText(R.id.sensorLabel, "Sensor "+preferences.getSensorNumber());
            appWidgetManager.updateAppWidget(appWidgetId, views);
        }

        private String getStringValue(float value){
            if(preferences.isFahrenheit())
                return String.valueOf(convertToFahrenheit(value))+" \u2109";
            return String.valueOf(Math.round(value))+" \u2103";
        }

        private int convertToFahrenheit(float cel){
            cel = cel * 9;
            cel = cel / 5;
            cel = cel + 32;
            return Math.round(cel);
        }
    }
}

