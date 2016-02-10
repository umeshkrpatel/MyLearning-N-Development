package com.github.umeshkrpatel.growthmonitor.data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.umeshkrpatel.growthmonitor.GrowthActivity;
import com.github.umeshkrpatel.growthmonitor.R;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String message = "Alarm Test";
        message = VaccineScheduler.notificationMessage();
        handleAction(message);
    }

    private void handleAction(String param) {
        Log.d("NotificationService", "Preparing to send notification...: " + param);
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GrowthActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder = new NotificationCompat.Builder(
                this).setContentTitle("Alarm").setSmallIcon(R.mipmap.ic_launcher)
                .setStyle(new NotificationCompat.BigTextStyle().bigText(param))
                .setContentText(param);

        alamNotificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
}
