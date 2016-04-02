package uk.ac.aber.crh13.temperature_widget;

import android.app.Activity;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;

/**
 * The configuration screen for the {@link TemperatureMonitor TemperatureMonitor} AppWidget.
 */
public class TemperatureMonitorConfigureActivity extends Activity {

    int mAppWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
    private RadioGroup radioSensorGroup;
    private RadioButton radioButtonCelcius;
    private RadioButton radioButtonFahrenheit;
    private RadioButton radioButtonSensor1;
    private RadioButton radioButtonSensor2;
    private RadioGroup radioUnit;

    /**
     * Listens on the click of the open configuration button, and creates an intent for the button
     * that saves the user preferences. Gets the given values, and saves these as a Prferences object
     * which is saved. Sends the user back to the widget.
     */
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        public void onClick(View v) {
            int sensorValue = 1;
            boolean isFahrenheit = false;

            // Get and save user options
            final Context context = TemperatureMonitorConfigureActivity.this;
            DataHandler dataHandler = new DataHandler();
            radioSensorGroup = (RadioGroup) findViewById(R.id.radioSensor);
            int sensor = radioSensorGroup.getCheckedRadioButtonId();
            if(sensor == R.id.radioSensor2)
                sensorValue = 2;

            radioUnit = (RadioGroup) findViewById(R.id.radioUnit);
            int unit = radioUnit.getCheckedRadioButtonId();
            if(unit == R.id.radioF)
                isFahrenheit = true;

            Preferences preferences = new Preferences(sensorValue, isFahrenheit);
            dataHandler.savePref(context, preferences);

            // It is the responsibility of the configuration activity to update the app widget
            Intent intent = new Intent(context, TemperatureMonitor.class);
            intent.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
            int[] ids = {mAppWidgetId};
            intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS,ids);
            sendBroadcast(intent);

            // Make sure we pass back the original appWidgetId
            Intent resultValue = new Intent();
            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, mAppWidgetId);
            setResult(RESULT_OK, resultValue);
            finish();
        }
    };

    public TemperatureMonitorConfigureActivity() {
        super();
    }

    @Override
    public void onCreate(Bundle icicle) {
        super.onCreate(icicle);

        // Set the result to CANCELED.  This will cause the widget host to cancel
        // out of the widget placement if the user presses the back button.
        setResult(RESULT_CANCELED);
        setContentView(R.layout.tempreture_monitor_configure);

        // Load previous settings and populate radio buttons with preferences
        final Context context = TemperatureMonitorConfigureActivity.this;
        DataHandler dataHandler = new DataHandler();
        Preferences preferences = dataHandler.loadPref(context);
        radioButtonSensor1 = (RadioButton)findViewById(R.id.radioSensor1);
        radioButtonSensor2 = (RadioButton)findViewById(R.id.radioSensor2);
        radioButtonCelcius = (RadioButton)findViewById(R.id.radioC);
        radioButtonFahrenheit = (RadioButton)findViewById(R.id.radioF);
        radioButtonSensor1.setChecked(preferences.getSensorNumber() == 1);
        radioButtonSensor2.setChecked(preferences.getSensorNumber() == 2);
        radioButtonCelcius.setChecked(!preferences.isFahrenheit());
        radioButtonFahrenheit.setChecked(preferences.isFahrenheit());
        findViewById(R.id.add_button).setOnClickListener(mOnClickListener);

        // Find the widget id from the intent.
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            mAppWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        // If this activity was started with an intent without an app widget ID, finish with an error.
        if (mAppWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
            return;
        }
    }
}

