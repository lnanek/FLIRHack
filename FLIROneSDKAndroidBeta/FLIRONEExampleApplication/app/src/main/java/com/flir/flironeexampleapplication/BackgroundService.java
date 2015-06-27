package com.flir.flironeexampleapplication;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.util.Log;

/**
 * Created by lnanek on 6/27/15.
 */
public class BackgroundService extends Service {

    private static final String TAG = BackgroundService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(TAG, "onBind");

        return null;
    }

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");

        super.onCreate();

        startForeground(ForegroundNotification.NOTIFICATION_ID, ForegroundNotification.show(this));

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

        super.onStartCommand(intent, flags, startId);

        startForeground(ForegroundNotification.NOTIFICATION_ID, ForegroundNotification.show(this));

        return START_STICKY;
    }
}
