package com.flir.flironeexampleapplication;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;
import android.support.v4.app.NotificationCompat;

/**
 * Lets the user know the app is using foreground priority for services.
 *
 * Created by lancenanek on 5/26/15.
 */
public class ForegroundNotification {

    public static final int NOTIFICATION_ID = 500;

    private static Notification notification;

    public static synchronized Notification show(final Context context) {
        if ( null == notification ) {
            NotificationCompat.Builder builder = new NotificationCompat.Builder(context)
                    .setContentTitle("FLIR SDK")
                    .setContentText("Streaming thermal images")
                    .setSmallIcon(R.drawable.ic_launcher)
                    .setOngoing(true);

            notification = builder.build();
        }

        NotificationManager notifications = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notifications.notify(NOTIFICATION_ID, notification);
        return notification;
    }

}
