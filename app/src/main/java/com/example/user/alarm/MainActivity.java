package com.example.user.alarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.TimePicker;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    Button alarmOn, alarmOff, fileChoose;
    TextView alarmState;
    TimePicker alarmPicker;
    AlarmManager alarmManager;
    PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        alarmOn = findViewById(R.id.alarm_on);
        alarmOff = findViewById(R.id.alarm_off);
        alarmState = findViewById(R.id.alarm_state);
        alarmPicker = findViewById(R.id.timePicker);
        alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
        fileChoose = findViewById(R.id.button_chooser);

        final Calendar calendar = Calendar.getInstance();

        final Intent intent = new Intent(getApplicationContext(), AlarmReceiver.class);

        alarmOn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                int hour, minute;
                if (Build.VERSION.SDK_INT >= 23) {
                    hour = alarmPicker.getHour();
                    minute = alarmPicker.getMinute();
                } else {
                    hour = alarmPicker.getCurrentHour();
                    minute = alarmPicker.getCurrentMinute();
                }
                calendar.set(Calendar.HOUR_OF_DAY, hour);
                calendar.set(Calendar.MINUTE, minute);
                String hourStr = String.valueOf(hour);
                String minuteStr = minute < 10
                        ? "0" + String.valueOf(minute)
                        : String.valueOf(minute);
                setAlarmState("Alarm is switched on " + hourStr + ":" + minuteStr + " on.");

                intent.putExtra("extra", "alarm on");

                pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT );

                alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
            }
        });

        alarmOff.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setAlarmState("Alarm is switched off");
                alarmManager.cancel(pendingIntent);
                intent.putExtra("extra", "alarm off");
                sendBroadcast(intent);
            }
        });

        fileChoose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                OpenFileDialog fileDialog = new OpenFileDialog(MainActivity.this);
                fileDialog.show();
            }
        });
    }

    private void setAlarmState(String state) {
        alarmState.setText(state);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
