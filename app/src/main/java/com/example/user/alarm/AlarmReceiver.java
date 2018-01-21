package com.example.user.alarm;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

public class AlarmReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Bundle extraBundle;
        extraBundle = intent.getExtras();
        String extraString = "";
        Intent serviceIntent = new Intent(context, RingtonePlayService.class);
        if (extraBundle != null) {
            extraString = extraBundle.getString("extra");
        }
        serviceIntent.putExtra("extra", extraString);
        context.startService(serviceIntent);
    }
}
