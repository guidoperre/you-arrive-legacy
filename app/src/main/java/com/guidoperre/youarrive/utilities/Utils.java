package com.guidoperre.youarrive.utilities;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.DisplayMetrics;
import android.view.Surface;
import android.view.WindowManager;

import androidx.core.content.res.ResourcesCompat;

import com.guidoperre.youarrive.R;

import java.util.Calendar;
import java.util.Locale;

public class Utils {

    ////////////////////////////////////////////////////////////////////////////////////////////
    public int getTextWidth(String text, Context context){
        Paint paint = new Paint();
        paint.setTextSize(14);
        Typeface typeface = ResourcesCompat.getFont(context, R.font.quicksand_medium);
        paint.setTypeface(typeface);
        paint.setColor(context.getColor(R.color.colorAccent));
        Rect result = new Rect();
        paint.getTextBounds(text, 0, text.length(), result);
        return (int) (result.width() / Resources.getSystem().getDisplayMetrics().density);
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public int getLayoutWidth(){
        int widthPixels = Resources.getSystem().getDisplayMetrics().widthPixels;
        double widthDp = (widthPixels / Resources.getSystem().getDisplayMetrics().density);
        double finalWidth =  widthDp*0.8*0.7;
        return (int) finalWidth;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    ////////////////////////////////////////////////////////////////////////////////////////////
    public float getLayoutHeight(){
        double heightPixels = Resources.getSystem().getDisplayMetrics().heightPixels;
        return (float) heightPixels;
    }
    ////////////////////////////////////////////////////////////////////////////////////////////

    public String getCurrentTime(){
        Calendar calendar = Calendar.getInstance();
        int currentHour = calendar.get(Calendar.HOUR);
        int currentMinutes = calendar.get(Calendar.MINUTE);
        String hour  ;
        String minutes;

        if (currentHour < 10)
            hour = "0"+currentHour;
        else
            hour = String.valueOf(currentHour);
        if (currentMinutes < 10)
            minutes = "0"+currentMinutes;
        else
            minutes = String.valueOf(currentMinutes);

        return  hour + ":" + minutes;
    }

    public String getDayZone(){
        Calendar calendar = Calendar.getInstance();
        String dayZone = "AM";

        int am_pm = calendar.get(Calendar.AM_PM);
        if (am_pm == 1)
            dayZone = "PM";

        return dayZone;
    }

    public static void lockScreenOrientation(Activity activity)
    {
        WindowManager windowManager =  (WindowManager) activity.getSystemService(Context.WINDOW_SERVICE);
        Configuration configuration = activity.getResources().getConfiguration();
        int rotation = 0;
        if (windowManager != null) {
            rotation = windowManager.getDefaultDisplay().getRotation();
        }

        // Search for the natural position of the device
        if(configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && (rotation == Surface.ROTATION_0 || rotation == Surface.ROTATION_180) || configuration.orientation == Configuration.ORIENTATION_PORTRAIT && (rotation == Surface.ROTATION_90 || rotation == Surface.ROTATION_270)) {
            // Natural position is Landscape
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
            }
        } else {
            // Natural position is Portrait
            switch (rotation)
            {
                case Surface.ROTATION_0:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    break;
                case Surface.ROTATION_90:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    break;
                case Surface.ROTATION_180:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT);
                    break;
                case Surface.ROTATION_270:
                    activity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE);
                    break;
            }
        }
    }

}
