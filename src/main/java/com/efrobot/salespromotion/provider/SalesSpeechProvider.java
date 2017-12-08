package com.efrobot.salespromotion.provider;

import android.util.Log;
import android.view.KeyEvent;

import com.efrobot.library.speech.SpeechSdkProvider;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.interfaces.OnKeyEventListener;
import com.efrobot.salespromotion.service.SalesPromotionService;

/**
 * Created by zd on 2017/11/10.
 */
public class SalesSpeechProvider extends SpeechSdkProvider {

    public static OnKeyEventListener mOnKeyEventListener;

    //是否在执行促销TTS语音
    private boolean isExecuteSaleTts = false;

    //执行促销TTS语音被打断
    private boolean isBreakSaleTts = false;

    public static void setOnKeyEventListener(OnKeyEventListener onKeyEventListener) {
        mOnKeyEventListener = onKeyEventListener;
    }

    @Override
    public void onReceiveMessage(String s, String s1) {

    }

    @Override
    public void onKeyUp(int keyCode, KeyEvent event) {
        super.onKeyUp(keyCode, event);
        if (mOnKeyEventListener != null) {
            mOnKeyEventListener.onKeyUp(keyCode);
        }
        Log.e(TAG, "onKeyUp = " + keyCode);
    }

    @Override
    public void onKeyDown(int keyCode, KeyEvent event) {
        super.onKeyDown(keyCode, event);
        if (mOnKeyEventListener != null) {
            mOnKeyEventListener.onKeyDown(keyCode);
        }
        Log.e(TAG, "onKeyDown" + keyCode);
    }

    @Override
    public void onKeyLongPress(int keyCode, KeyEvent event) {
        super.onKeyLongPress(keyCode, event);
        if (mOnKeyEventListener != null) {
            mOnKeyEventListener.onKeyLongPress(keyCode);
        }
        Log.e(TAG, "onKeyLongPress" + keyCode);
    }

    @Override
    public void TTSStart() {
        super.TTSStart();
        Log.e(TAG, "TTSStart：" + "isNeedReceiveTtsEnd  =  " + SalesPromotionService.isNeedReceiveTtsEnd);
        if (SalesPromotionService.isNeedReceiveTtsEnd) {
            if (!isExecuteSaleTts) {
                isExecuteSaleTts = true;
            } else {
                //TTS正在执行但被打断，结束需要重新播
                if(!SalesPromotionService.isUserBreak) {
                    isBreakSaleTts = true;
                }
            }
        }
    }

    @Override
    public void TTSEnd() {
        super.TTSEnd();
        SalesApplication application = SalesApplication.getAppContext();
        Log.e(TAG, "TTSEnd：" + "isNeedReceiveTtsEnd  =  " + SalesPromotionService.isNeedReceiveTtsEnd);

        if (SalesPromotionService.isNeedReceiveTtsEnd) {
            if (!isBreakSaleTts) {
                if (isExecuteSaleTts) {
                    SalesPromotionService.isNeedReceiveTtsEnd = false;
                    if (application != null && application.salesPromotionService != null) {
                        if (application.salesPromotionService.mHandle != null) {
                            application.salesPromotionService.mHandle.sendEmptyMessage(application.salesPromotionService.TTS_FINISH);
                        }
                    }
                    isExecuteSaleTts = false;
                }
            } else {
                //TTS正在执行但被打断，结束需要重新播
                isExecuteSaleTts = false;
                isBreakSaleTts = false;
                if (application != null && application.salesPromotionService != null) {
                    if(!SalesPromotionService.isUserBreak) {
                        application.salesPromotionService.againPlayCurrentItem();
                    } else  {
                        SalesPromotionService.isUserBreak = false;
                    }
                }
            }
        }
    }
}
