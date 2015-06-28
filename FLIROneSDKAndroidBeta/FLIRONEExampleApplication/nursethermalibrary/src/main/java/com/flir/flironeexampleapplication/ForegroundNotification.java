package com.flir.flironeexampleapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import com.flir.flironeexampleapplication.nursethermalibrary.R;

/**
 * Lets the user know the app is using foreground priority for services.
 *
 * Created by lancenanek on 5/26/15.
 */
public class ForegroundNotification {

    public static final int NOTIFICATION_ID = 500;

    private static Notification notification;

    public static synchronized Notification show(final Context context) {
        if (null == notification) {

            CharSequence tickerText = "Nurse Therma";
            long when = System.currentTimeMillis();
            notification = new Notification(R.drawable.ic_launcher, tickerText, when);
            notification.defaults |= Notification.DEFAULT_SOUND|Notification.DEFAULT_VIBRATE|Notification.DEFAULT_LIGHTS;;
            notification.setLatestEventInfo(context, "Nurse Therma", "Streaming thermal images", null);

        }

        NotificationManager notifications = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications.notify(NOTIFICATION_ID, notification);
        return notification;
    }

    public static synchronized void hide(final Context context) {
        NotificationManager notifications = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications.cancelAll();
    }

}
