package io.wazo.callkeep.utils.avz;

import static io.wazo.callkeep.Constants.EXTRA_CALL_UUID;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.PowerManager;
import android.widget.Toast;
import android.os.Handler;
import android.view.WindowManager;


import androidx.core.app.NotificationCompat;

import org.json.JSONException;
import org.json.JSONObject;

import io.wazo.callkeep.R;
import io.wazo.callkeep.avz.AVLockscreenCalling;
import io.wazo.callkeep.avz.AVVoipReceiver;

public class AVNotificationHelper {
    public final String callChannel = "Call";
    public  final  String notificationChannel = "NotificationChannel";
    private Context context;

    public AVNotificationHelper(Application context){
        this.context = context;
    }
    
    public JSONObject configJson(String caller_id, String number, String caller_name){

        JSONObject config = new JSONObject();
        try {
            config.put("callerId", caller_id);
            config.put("ringtuneSound", true);
            config.put("ringtune", "ringtune");
            config.put("duration", 60000);
            config.put("vibration", true);
            config.put("channel_name", "call1asd");
            config.put("notificationId", 18347);
            config.put("notificationTitle", "Llamada entrante");
            config.put("notificationBody", caller_name + " llamando");
            config.put("answerActionTitle", "Contestar");
            config.put("declineActionTitle", "Colgar");
            config.put("missedCallTitle", caller_name);
            config.put("missedCallBody", "Tiene una llamada perdida de" + caller_name);


        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return config;
    }

    public void powerOnScreen(){
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        boolean isScreenOn = pm.isInteractive(); // check if screen is on
        if (!isScreenOn) {
            PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_DIM_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "myApp:notificationLock");
            wl.acquire(1); //set your time in milliseconds
        }
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    public void sendNotification(JSONObject json) throws JSONException {
        powerOnScreen();

        int notificationID = json.getInt("notificationId");
        Toast.makeText(context, "sendNotification", Toast.LENGTH_SHORT).show();
        Intent dissmissIntent = new Intent(context, AVVoipReceiver.class);
        dissmissIntent.setAction("callDismiss");
        dissmissIntent.putExtra("notificationId",notificationID);
        dissmissIntent.putExtra("callerId", json.getString("callerId"));
        dissmissIntent.putExtra(EXTRA_CALL_UUID, json.getString("callerId"));
        dissmissIntent.putExtra("missedCallTitle", json.getString("missedCallTitle"));
        dissmissIntent.putExtra("missedCallBody", json.getString("missedCallBody"));
        PendingIntent callDismissIntent = PendingIntent.getBroadcast(context,0, dissmissIntent ,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent answerIntent = new Intent(context, AVVoipReceiver.class);
        answerIntent.setAction("callAnswer");
        answerIntent.putExtra("notificationId",notificationID);
        answerIntent.putExtra("callerId", json.getString("callerId"));
        answerIntent.putExtra(EXTRA_CALL_UUID, json.getString("callerId"));
        PendingIntent callAnswerIntent = PendingIntent.getBroadcast(context,0, answerIntent ,PendingIntent.FLAG_UPDATE_CURRENT);

        Intent fullScreenIntent = new Intent(context, AVLockscreenCalling.class);
        PendingIntent fullScreenPendingIntent = PendingIntent.getActivity(context, 0, fullScreenIntent, PendingIntent.FLAG_UPDATE_CURRENT);


        Uri sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        Notification notification = new NotificationCompat.Builder(context,callChannel)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_ALL)
                .setOngoing(true)
                .setTimeoutAfter(json.getInt("duration"))
                .setOnlyAlertOnce(true)
                .setSmallIcon(R.drawable.ic_call_black_24)
                .setPriority(Notification.PRIORITY_HIGH)
                .setCategory(Notification.CATEGORY_CALL)
                .setFullScreenIntent(fullScreenPendingIntent , true)
//                .setFullScreenIntent(getPendingIntent(notificationID, "fullScreenIntent", json) , true)
                .setContentIntent(getPendingIntent(notificationID, "contentTap", json))
                .setContentTitle(json.getString("notificationTitle"))
                .setSound(sounduri)
                .setContentText(json.getString("notificationBody"))
                .addAction(0, json.getString("answerActionTitle"), callAnswerIntent)
                .addAction(0, json.getString("declineActionTitle"), callDismissIntent)
                .build();

        NotificationManager notificationManager = notificationManager();
        createCallNotificationChannel(notificationManager, json);

        new Handler().postDelayed(() -> notificationManager.notify(notificationID,notification),500);


    }

    public void createCallNotificationChannel(NotificationManager manager, JSONObject json) throws JSONException {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Toast.makeText(context, "createCall", Toast.LENGTH_SHORT).show();
            Uri sounduri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
            NotificationChannel channel = new NotificationChannel(callChannel, json.getString("channel_name"), NotificationManager.IMPORTANCE_HIGH);
            channel.setDescription("Call Notifications");
            channel.setSound(sounduri ,
                    new AudioAttributes.Builder().setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                            .setUsage(AudioAttributes.USAGE_UNKNOWN).build());
            channel.setVibrationPattern(new long[]{0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000, 0, 1000, 500, 1000});
            channel.enableVibration(json.getBoolean("vibration"));
            manager.createNotificationChannel(channel);
        }
    }

    public PendingIntent getPendingIntent(int notificationID , String type, JSONObject json) throws JSONException {
        Class intentClass = getMainActivityClass();
        Intent intent = new Intent(context, intentClass);
        intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        intent.putExtra("notificationId",notificationID);
        intent.putExtra("callerId", json.getString("callerId"));
        intent.putExtra(EXTRA_CALL_UUID, json.getString("callerId"));
        intent.putExtra("action", type);
        intent.setAction(type);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, notificationID, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    public Class getMainActivityClass() {
        String packageName = context.getPackageName();
        Intent launchIntent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        String className = launchIntent.getComponent().getClassName();
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void clearNotification(int notificationID) {
        NotificationManager notificationManager = notificationManager();
        notificationManager.cancel(notificationID);
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    public void clearAllNorifications(){
        NotificationManager manager = notificationManager();
        manager.cancelAll();
        context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }


    private NotificationManager notificationManager() {
        return (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    }
}
