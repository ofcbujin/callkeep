package io.wazo.callkeep.avz;

import static io.wazo.callkeep.Constants.ACTION_END_CALL;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

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
                final Handler handler = new Handler();
                Bundle extras = intent.getExtras();
                HashMap<String, String> extrasMap = this.bundleToMap(extras);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ACTION_END_CALL);
                        if (extrasMap != null) {
                            Bundle extras = new Bundle();
                            extras.putSerializable("attributeMap", extrasMap);
                            intent.putExtras(extras);
                        }
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
                // rnVoipNotificationHelper.showMissCallNotification(intent.getStringExtra("missedCallTitle"), intent.getStringExtra("missedCallBody"), intent.getStringExtra("callerId"));
                break;
            case "callTimeOut":
                // rnVoipNotificationHelper.showMissCallNotification(intent.getStringExtra("missedCallTitle"), intent.getStringExtra("missedCallBody"), intent.getStringExtra("callerId"));
                break;
            case "callAnswer":
                AVRingPlayer.getInstance(context).stopMusic();
                avNotificationHelper.clearNotification(notificationId);
                final Handler handler1 = new Handler();
                Bundle extras1 = intent.getExtras();
                HashMap<String, String> extrasMap1 = this.bundleToMap(extras1);
                handler1.post(new Runnable() {
                    @Override
                    public void run() {
                        Intent intent = new Intent(ACTION_END_CALL);
                        if (extrasMap1 != null) {
                            Bundle extras = new Bundle();
                            extras.putSerializable("attributeMap", extrasMap1);
                            intent.putExtras(extras);
                        }
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
                break;
            default:
                break;
        }

    }

    private HashMap<String, String> bundleToMap(Bundle extras) {
        HashMap<String, String> extrasMap = new HashMap<>();
        Set<String> keySet = extras.keySet();
        Iterator<String> iterator = keySet.iterator();

        while(iterator.hasNext()) {
            String key = iterator.next();
            if (extras.get(key) != null) {
                extrasMap.put(key, extras.get(key).toString());
            }
        }
        return extrasMap;
    }
}
