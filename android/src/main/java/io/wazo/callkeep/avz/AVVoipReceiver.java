package io.wazo.callkeep.avz;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import io.wazo.callkeep.utils.avz.AVNotificationHelper;

public class AVVoipReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Application applicationContext = (Application) context.getApplicationContext();

        AVNotificationHelper avNotificationHelper = new AVNotificationHelper(applicationContext);
        int notificationId = intent.getIntExtra("notificationId",0);

        switch (intent.getAction()){
            case "callDismiss":
                AVRingPlayer.getInstance(context).stopMusic();
                avNotificationHelper.clearNotification(notificationId);
                // rnVoipNotificationHelper.showMissCallNotification(intent.getStringExtra("missedCallTitle"), intent.getStringExtra("missedCallBody"), intent.getStringExtra("callerId"));
                break;
            case "callTimeOut":
                // rnVoipNotificationHelper.showMissCallNotification(intent.getStringExtra("missedCallTitle"), intent.getStringExtra("missedCallBody"), intent.getStringExtra("callerId"));
                break;
            default:
                break;
        }

    }
}
