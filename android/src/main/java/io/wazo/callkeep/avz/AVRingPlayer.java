package io.wazo.callkeep.avz;

import android.content.Context;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;

import org.json.JSONException;
import org.json.JSONObject;

import io.wazo.callkeep.R;

public class AVRingPlayer {
    private static AVRingPlayer sInstance;
    private Context mContext;
    private MediaPlayer mMediaPlayer;

    public AVRingPlayer(Context context) {
        mContext = context;
    }

    public static AVRingPlayer getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new AVRingPlayer(context);
        }
        return sInstance;
    }

    public void playMusic(JSONObject jsonObject) throws JSONException {
        String fileName = jsonObject.getString("ringtune");
        notificationRingtune(fileName);
        mMediaPlayer.setLooping(true);
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.start();
            cancelWithTimeOut(jsonObject);
        }
    }

    public void notificationRingtune(String fileName) {
        int resId;
        Uri sounduri;
        try {
            resId = mContext.getResources().getIdentifier(fileName, "raw", mContext.getPackageName());
            if (resId != 0) {
                sounduri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + resId);
            } else {
                sounduri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.ringtune);
            }

        } catch (Exception e) {
            sounduri = Uri.parse("android.resource://" + mContext.getPackageName() + "/" + R.raw.ringtune);
        }

        mMediaPlayer = MediaPlayer.create(mContext, sounduri);

    }


    public void playRingtune(String fileName, Boolean isLooping) {
        notificationRingtune(fileName);
        if (!mMediaPlayer.isPlaying()) {
            mMediaPlayer.setLooping(isLooping);
            mMediaPlayer.start();
        }
    }

    public void cancelWithTimeOut(JSONObject jsonObject) throws JSONException {
        int duration = jsonObject.getInt("duration");
        final JSONObject json = jsonObject;
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        if (mMediaPlayer.isPlaying()) {
                            Intent intent = new Intent(mContext, AVVoipReceiver.class);
                            intent.setAction("callTimeOut");
                            try {
                                intent.putExtra("callerId", json.getString("callerId"));
                                intent.putExtra("missedCallTitle", json.getString("missedCallTitle"));
                                intent.putExtra("missedCallBody", json.getString("missedCallBody"));
                                mContext.sendBroadcast(intent);
                                stopMusic();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }, duration);
    }

    public void stopMusic() {
        if (mMediaPlayer != null) {
            mMediaPlayer.stop();
            mMediaPlayer.seekTo(0);
        }
    }
}