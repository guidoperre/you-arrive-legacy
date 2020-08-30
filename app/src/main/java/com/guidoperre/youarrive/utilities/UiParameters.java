package com.guidoperre.youarrive.utilities;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;


public class UiParameters {

    public void setFullScreenFlags(Activity activity){
        Window w = activity.getWindow();
        w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
    }

}
