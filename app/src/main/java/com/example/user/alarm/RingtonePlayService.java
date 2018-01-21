package com.example.user.alarm;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

public class RingtonePlayService extends Service {
    MediaPlayer mediaSong;

    int startId;
    boolean isPlaying;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Bundle stateBundle;
        stateBundle = intent.getExtras();
        String state = "";
        if (stateBundle != null) {
            state = stateBundle.getString("extra");
        }

        assert state != null;
        if (state .equals("alarm on")) {
            this.startId = 1;
        } else {
            this.startId = 0;
        }

        if (!this.isPlaying && this.startId == 1) {
            mediaSong = MediaPlayer.create(this, R.raw.new_rington);
            mediaSong.start();
            this.isPlaying = true;
            this.startId = 1;
        } else if (this.isPlaying && this.startId == 0) {
            mediaSong.stop();
            mediaSong.reset();
            this.isPlaying = false;
            this.startId = 0;
        }

        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        // Tell the user we stopped.
        Toast.makeText(this, "on destroy called", Toast.LENGTH_SHORT).show();
    }
}
