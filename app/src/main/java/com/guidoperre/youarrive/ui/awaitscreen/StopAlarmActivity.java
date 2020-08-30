package com.guidoperre.youarrive.ui.awaitscreen;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.widget.TextView;
import android.widget.Toast;

import com.ebanx.swipebtn.SwipeButton;
import com.guidoperre.youarrive.R;
import com.guidoperre.youarrive.controllers.AlarmController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.ui.main.MapsActivity;
import com.guidoperre.youarrive.utilities.Utils;

import java.util.Timer;
import java.util.TimerTask;

public class StopAlarmActivity extends AppCompatActivity {

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();

    private NavigationController navigationController = new NavigationController();

    private TextView time;
    private TextView dayZone;

    private AlarmController alarmController = new AlarmController();
    private Vibrator vibrator;

    private int alarmPosition;
    private int alarmSize;

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.OnStopAlarmTheme);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stop_alarm);
        getExtras();
        onReady();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReady() {
        startAlarm();
        updateTime();
        onStopListener();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        if (getIntent().getExtras() != null) {
            alarmPosition = getIntent().getExtras().getInt("alarm");
            alarmSize = getIntent().getExtras().getInt("alarmSize");
        } else
            Toast.makeText(this, "Hubo un error, intente de nuevo", Toast.LENGTH_SHORT).show();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void startAlarm() {
        alarmController.alarmStart(getApplication(), getSystemService(AUDIO_SERVICE), alarmPosition);
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        long[] pattern = {0, 800};
        if (vibrator != null)
            vibrator.vibrate(pattern, 1);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void stopAlarm(){
        alarmController.alarmStop(getSystemService(AUDIO_SERVICE));
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        if (vibrator != null)
            vibrator.cancel();
        if (alarmPosition+1 < alarmSize)
            navigationController.basicIntent(this, AwaitAlarmActivity.class);
        else
            navigationController.basicIntent(this, MapsActivity.class);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onStopListener(){
        SwipeButton enableButton = findViewById(R.id.stop_alarm_swipe);
        enableButton.setOnStateChangeListener(active -> stopAlarm());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void updateTime(){
        Utils utils = new Utils();
        time = findViewById(R.id.hour);
        dayZone = findViewById(R.id.day_zone);

        timer = new Timer();
        timerTask = new TimerTask() {
            public void run() {
                handler.post(() -> {
                    time.setText(utils.getCurrentTime());
                    dayZone.setText(utils.getDayZone());
                });
            }
        };
        timer.schedule(timerTask, 0, 5000);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onStop() {
        super.onStop();
        if(timer != null){
            handler.removeCallbacks(timerTask);
            timer.cancel();
            timer.purge();
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onResume() {
        super.onResume();
        Utils utils = new Utils();
        if (timer != null && handler != null){
            timerTask = new TimerTask() {
                public void run() {
                    handler.post(() -> {
                        time.setText(utils.getCurrentTime());
                        dayZone.setText(utils.getDayZone());
                    });
                }
            };
            timer.schedule(timerTask, 0, 5000);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
