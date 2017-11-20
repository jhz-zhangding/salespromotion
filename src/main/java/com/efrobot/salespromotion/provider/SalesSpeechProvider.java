package com.efrobot.salespromotion.provider;

import android.util.Log;
import android.view.KeyEvent;

import com.efrobot.library.speech.SpeechSdkProvider;
import com.efrobot.salespromotion.interfaces.OnKeyEventListener;

/**
 * Created by zd on 2017/11/10.
 */
public class SalesSpeechProvider extends SpeechSdkProvider {

    public static OnKeyEventListener mOnKeyEventListener;

    public static void setOnKeyEventListener(OnKeyEventListener onKeyEventListener) {
        mOnKeyEventListener = onKeyEventListener;
    }

    @Override
    public void onReceiveMessage(String s, String s1) {

    }

    @Override
    public void onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if(mOnKeyEventListener != null) {
            mOnKeyEventListener.onKeyUp(keyCode);
        }
        Log.e(TAG, "onKeyUp = " + keyCode);
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if(mOnKeyEventListener != null) {
            mOnKeyEventListener.onKeyDown(keyCode);
        }
        Log.e(TAG, "onKeyDown" + keyCode);
    }

    @Override
    public void onKeyLongPress(int keyCode, KeyEvent event) {
        super.onKeyLongPress(keyCode, event);
        if(mOnKeyEventListener != null) {
            mOnKeyEventListener.onKeyLongPress(keyCode);
        }
        Log.e(TAG, "onKeyLongPress" + keyCode);
    }
}
