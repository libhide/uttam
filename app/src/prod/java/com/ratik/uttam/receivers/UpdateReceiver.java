package com.ratik.uttam.receivers;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import com.ratik.uttam.R;
import com.ratik.uttam.ui.MainActivity;

/**
 * Created by Ratik on 11/11/16.
 */

public class UpdateReceiver extends BroadcastReceiver {

    private static final int OPEN_MAIN = 2;
    private static final int UPDATE_NOTIFY = 3;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getDataString().contains("com.ratik.uttam.prod")) {
            Intent mainIntent = new Intent(context, MainActivity.class);
            mainIntent.putExtra("update", true);
            mainIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            PendingIntent pi = PendingIntent.getActivity(context, OPEN_MAIN, mainIntent,
                    PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
            builder.setContentTitle("Uttam was updated!")
                    .setContentText("There have been some major changes. Click to find out!")
                    .setSmallIcon(R.drawable.ic_stat_uttam)
                    .setAutoCancel(true)
                    .setOngoing(true)
                    .setSound(Uri.parse("android.resource://" +
                            context.getPackageName() + "/" + R.raw.uttam))
                    .setDefaults(Notification.DEFAULT_LIGHTS | Notification.DEFAULT_VIBRATE)
                    .setContentIntent(pi);

            NotificationManager manager = (NotificationManager)
                    context.getSystemService(Context.NOTIFICATION_SERVICE);
            manager.notify(UPDATE_NOTIFY, builder.build());
        }
    }
}
