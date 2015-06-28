package com.flir.flironeexampleapplication;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

/**
 * Controls starting and stopping background thermal capture.
 *
 * Created by lnanek on 6/28/15.
 */
public class ThermalControl {

    private static final String LOG_TAG = ThermalControl.class.getSimpleName();

    private static final String SIMULATE_PREF_KEY = MainActivity.class.getName() + ".SIMULATE_PREF_KEY";

    public static void startBackgroundThermalCapture(final Context context){
        Log.d(LOG_TAG, "startBackgroundThermalCapture");

        setSimulatePref(context, false);

        context.startService(getServiceIntent(context));
    }

    public static void stopBackgroundThermalCapture(final Context context){
        Log.d(LOG_TAG, "stopBackgroundThermalCapture");

        context.stopService(getServiceIntent(context));
        ForegroundNotification.hide(context);
    }

    public static void startBackgroundThermalCaptureSimulated(final Context context){
        Log.d(LOG_TAG, "startBackgroundThermalCaptureSimulated");

        setSimulatePref(context, true);

        context.startService(getServiceIntent(context));
    }

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


    private static Intent getServiceIntent(final Context context) {
        final Intent intent = new Intent(context, BackgroundService.class);
        return intent;
    }

}
