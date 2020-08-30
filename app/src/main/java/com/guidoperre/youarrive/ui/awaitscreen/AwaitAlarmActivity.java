package com.guidoperre.youarrive.ui.awaitscreen;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.TextView;
import android.widget.Toast;

import com.guidoperre.youarrive.R;

import com.guidoperre.youarrive.controllers.DialogController;
import com.guidoperre.youarrive.controllers.NavigationController;
import com.guidoperre.youarrive.controllers.RoutesController;
import com.guidoperre.youarrive.models.RecoveryData;
import com.guidoperre.youarrive.models.RemainingTime;
import com.guidoperre.youarrive.models.Route;
import com.guidoperre.youarrive.repositories.AlarmLogRepository;
import com.guidoperre.youarrive.repositories.RecoveryDataRepository;
import com.guidoperre.youarrive.repositories.RemainingTimeRepository;
import com.guidoperre.youarrive.services.MyService;
import com.guidoperre.youarrive.ui.finalconfirmation.RoutePreviewActivity;
import com.guidoperre.youarrive.ui.finalconfirmation.SetRouteAlarmsActivity;
import com.guidoperre.youarrive.ui.finalconfirmation.SetAlarmActivity;
import com.guidoperre.youarrive.ui.main.MapsActivity;
import com.guidoperre.youarrive.utilities.Utils;

import java.util.List;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class AwaitAlarmActivity extends AppCompatActivity {

    private Timer timer;
    private TimerTask timerTask;
    private Handler handler = new Handler();
    private CountDownTimer countDownTimer;

    private NavigationController navigationController = new NavigationController();

    private TextView time;
    private TextView dayZone;
    private TextView nextAlarmTime;

    //This variables are only for onBack case
    private String lastScreen;
    private String address;
    private Route route;
    private double myLatitude;
    private double myLongitude;
    private double latitude;
    private double longitude;

    private int remainingTime;

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.AwaitTheme);
        Utils.lockScreenOrientation(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_await_alarm);
        startService();
        initializeViewModel();
        onReady();
        getExtras();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void onReady(){
        nextAlarmTime = findViewById(R.id.next_alarm_text);
        updateTime();
        setListeners();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    private void initializeViewModel(){
        AwaitAlarmViewModel model = new AwaitAlarmViewModel(getApplication());
        model.getTime().observe(this,this::setRemainingTime);
    }

    /////////////////////////////////////Start Service///////////////////////////////////////////
    private void startService(){
        if (!MyService.serviceActive){
            Intent service = new Intent(this, MyService.class);
            MyService.serviceActive = true;

            AlarmLogRepository alarmLogRepository = new AlarmLogRepository(getApplication());
            RemainingTimeRepository remainingTimeRepository = new RemainingTimeRepository(getApplication());
            alarmLogRepository.deleteAll();
            remainingTimeRepository.deleteAll();

            startService(service);
        }
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void getExtras(){
        if (getIntent().getExtras() != null) {
            lastScreen = getIntent().getExtras().getString("screen");
            route =  new RoutesController().parseRouteJson(Objects.requireNonNull(getIntent().getExtras()).getString("route"));
            address = getIntent().getExtras().getString("address");
            myLatitude = getIntent().getExtras().getDouble("myLatitude");
            myLongitude = getIntent().getExtras().getDouble("myLongitude");
            latitude = getIntent().getExtras().getDouble("latitude");
            longitude = getIntent().getExtras().getDouble("longitude");
        } else {
            RecoveryDataRepository recoveryDataRepository = new RecoveryDataRepository(getApplication());
            RecoveryData recoveryData = recoveryDataRepository.getRecoveryData().get(0);

            lastScreen = recoveryData.getLastScreen();
            route =  new RoutesController().parseRouteJson(recoveryData.getRouteJSON());
            address = recoveryData.getAddress();
            myLatitude = recoveryData.getMyLatitude();
            myLongitude = recoveryData.getMyLongitude();
            latitude = recoveryData.getLatitude();
            longitude = recoveryData.getLongitude();
        }
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
    private void setRemainingTime(List<RemainingTime> time){
        if (time != null && time.size() > 0){
            remainingTime = time.get(time.size()-1).getRemainingTime();
            String referText = getResources().getString(R.string.default_next_alarm_time_text);
            String newText = referText.replaceAll("\\?", String.valueOf(remainingTime));
            nextAlarmTime.setText(newText);

            if (countDownTimer != null)
                countDownTimer.cancel();

            countDownTimer = new CountDownTimer(time.get(time.size()-1).getRemainingTime()*60*1000, 60000){
                public void onTick(long millisUntilFinished){
                    remainingTime--;
                    String referText = getResources().getString(R.string.default_next_alarm_time_text);
                    String newText = referText.replaceAll("\\?", String.valueOf(remainingTime));
                    nextAlarmTime.setText(newText);
                }
                public void onFinish(){
                }
            }.start();
        }
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
        if (countDownTimer != null)
            countDownTimer.cancel();
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
    private void setListeners(){
        ConstraintLayout edit = findViewById(R.id.edit_alarm_view);
        ConstraintLayout view = findViewById(R.id.look_alarm_view);
        ConstraintLayout cancel = findViewById(R.id.cancel_alarm_view);

        edit.setOnClickListener(v -> checkScreen());

        view.setOnClickListener(v -> {
            if (lastScreen.equals("manual"))
                navigationController.semiFullIntent(this, RoutePreviewActivity.class,myLatitude,myLongitude,latitude,longitude,address);
            else
                navigationController.fullIntent(this, RoutePreviewActivity.class,myLatitude,myLongitude,latitude,longitude,address,route,null);
        });

        cancel.setOnClickListener(v -> cancelRoute());
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    private void cancelRoute(){
        new DialogController().openCancelRouteDialog(this,this,MapsActivity.class,new Intent(this,MyService.class),0,0,0,0,"");
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    private void checkScreen(){
        if (lastScreen.equals("manual"))
            navigationController.semiFullIntent(this,SetAlarmActivity.class,myLatitude,myLongitude,latitude,longitude,address);
        else
            navigationController.fullIntent(this, SetRouteAlarmsActivity.class,myLatitude,myLongitude,latitude,longitude,address,route,null);
    }

    ////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public void onBackPressed() {
        checkScreen();
    }
    ////////////////////////////////////////////////////////////////////////////////////////////
}
