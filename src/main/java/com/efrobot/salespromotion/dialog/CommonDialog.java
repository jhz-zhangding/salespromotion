package com.efrobot.salespromotion.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.view.View;

import com.efrobot.salespromotion.R;

/**
 * Created by chirenjie on 2016/11/2.
 */
public class CommonDialog extends Dialog {
    public static final int FULL_SCREEN = 0;//全屏
    public static final int NOT_FULL_SCREEN = 1;//非全屏
    private int type = FULL_SCREEN;


    public CommonDialog(Context context) {
        super(context, R.style.DialogTransparent);
    }

    public CommonDialog(Context context, int style) {
        super(context, style);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        switch (type) {
            case FULL_SCREEN:
                fullScreen();
                break;
            case NOT_FULL_SCREEN:

                break;

            default:
                fullScreen();
                break;
        }
    }

    public int getType() {
        return type;
    }

    /**
     * 设置是否全屏
     *
     * @param type
     */
    public void setType(int type) {
        this.type = type;
    }

    /**
     * 全屏显示方法
     */
    public void fullScreen() {
        int uiFlags = View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION // hide nav bar
                | View.SYSTEM_UI_FLAG_FULLSCREEN; // hide status bar

        if (Build.VERSION.SDK_INT >= 19) {
            uiFlags |= 0x00001000;    //SYSTEM_UI_FLAG_IMMERSIVE_STICKY: hide navigation bars - compatibility: building API level is lower thatn 19, use magic number directly for higher API target level
        } else {
            uiFlags |= View.SYSTEM_UI_FLAG_LOW_PROFILE;
        }
        getWindow().getDecorView().setSystemUiVisibility(uiFlags);
    }

}
