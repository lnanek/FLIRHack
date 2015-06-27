package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

/**
 * Offers options for running FLIR One in background or foreground.
 * 
 * Created by lnanek on 6/27/15.
 */
public class MainActivity extends Activity {

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String SIMULATE_PREF_KEY = MainActivity.class.getName() + ".SIMULATE_PREF_KEY";

    public static void setSimulatePref(final Context context, final boolean value) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putBoolean(SIMULATE_PREF_KEY, value);
        editor.commit();
    }


    public static boolean getSimulatePref(final Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean(SIMULATE_PREF_KEY, false);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    private Intent getServiceIntent() {
        final Intent intent = new Intent(this, BackgroundService.class);
        return intent;
    }

    public void onBackgroundClicked(View v){
        Log.d(LOG_TAG, "onBackgroundClicked");

        setSimulatePref(this, false);

        startService(getServiceIntent());
    }

    public void onStopBackgroundClicked(View v){
        Log.d(LOG_TAG, "onStopBackgroundClicked");

        stopService(getServiceIntent());
        ForegroundNotification.hide(this);
    }

    public void onBackgroundSimulatedClicked(View v){
        Log.d(LOG_TAG, "onBackgroundSimulatedClicked");

        setSimulatePref(this, true);

        startService(getServiceIntent());
    }

    public void onForegroundClicked(View v){
        Log.d(LOG_TAG, "onForegroundClicked");

        final Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
    }
}
