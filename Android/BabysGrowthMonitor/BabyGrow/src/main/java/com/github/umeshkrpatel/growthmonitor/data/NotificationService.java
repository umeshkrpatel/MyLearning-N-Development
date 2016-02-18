package com.github.umeshkrpatel.growthmonitor.data;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import android.text.SpannableStringBuilder;
import android.util.Log;

import com.github.umeshkrpatel.growthmonitor.GrowthActivity;
import com.github.umeshkrpatel.growthmonitor.R;

public class NotificationService extends IntentService {

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        SpannableStringBuilder message = IVaccines.notificationMessage();
        handleAction(message);
    }

    private void handleAction(SpannableStringBuilder msg) {
        Log.d("NotificationService", "Preparing to send notification...: " + msg);
        NotificationManager notificationManager = (NotificationManager) this
                .getSystemService(Context.NOTIFICATION_SERVICE);

        PendingIntent contentIntent = PendingIntent.getActivity(this, 0,
                new Intent(this, GrowthActivity.class), 0);

        NotificationCompat.Builder alamNotificationBuilder =
                new NotificationCompat.Builder(this)
                        .setContentTitle(getString(R.string.vaccine_pending_msg))
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setStyle(new NotificationCompat.BigTextStyle().bigText(msg))
                        .addAction (R.drawable.ic_menu_add_baby,
                                getString(R.string.app_name), null)
                        .addAction (R.drawable.ic_menu_growth,
                                getString(R.string.addInformation), null);

        alamNotificationBuilder.setContentIntent(contentIntent);
        notificationManager.notify(1, alamNotificationBuilder.build());
        Log.d("AlarmService", "Notification sent.");
    }
}
