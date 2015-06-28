package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import com.flir.flironeexampleapplication.nursethermalibrary.R;

/**
 * Offers options for running FLIR One in background or foreground.
 *
 * Created by lnanek on 6/27/15.
 */
public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }


    public void onBackgroundClicked(View v){
        Log.d(LOG_TAG, "onBackgroundClicked");
        ThermalControl.startBackgroundThermalCapture(this);
    }

    public void onStopBackgroundClicked(View v){
        Log.d(LOG_TAG, "onStopBackgroundClicked");

        ThermalControl.stopBackgroundThermalCapture(this);
    }

    public void onBackgroundSimulatedClicked(View v){
        Log.d(LOG_TAG, "onBackgroundSimulatedClicked");

        ThermalControl.startBackgroundThermalCaptureSimulated(this);
    }

    public void onForegroundClicked(View v){
        Log.d(LOG_TAG, "onForegroundClicked");

        final Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
    }

    public void onCompareActivityClicked(View v){
        Log.d(LOG_TAG, "onCompareActivityClicked");

        final Intent intent = new Intent(this, CompareActivity.class);
        startActivity(intent);
    }

    public void onListActivityClicked(View v){
        Log.d(LOG_TAG, "onListActivityClicked");

        final Intent intent = new Intent(this, ListActivity.class);
        startActivity(intent);
    }
}
