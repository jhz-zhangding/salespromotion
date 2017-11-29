package com.efrobot.salespromotion.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.efrobot.library.mvp.utils.L;
import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.service.SalesPromotionService;
import com.efrobot.salespromotion.utils.TtsUtils;

import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by zd on 2017/9/14.
 */
public class ReceiveMaskBroadcast extends BroadcastReceiver {

    /**
     * 监听盖子状态
     * 监听开机状态
     */
    public final String ROBOT_MASK_CHANGE = "android.intent.action.MASK_CHANGED";
    public final String KEYCODE_MASK_ONPROGRESS = "KEYCODE_MASK_ONPROGRESS"; //开闭状态
    public final String KEYCODE_MASK_CLOSE = "KEYCODE_MASK_CLOSE"; //关闭面罩
    public final String KEYCODE_MASK_OPEN = "KEYCODE_MASK_OPEN";  //打开面罩
    private String tts = "开始促销，请用倥鼠操作";

    @Override
    public void onReceive(final Context context, Intent intent) {
        L.i("ReceiveMaskBroadcast", "lidBoardReceive");
        if (ROBOT_MASK_CHANGE.equals(intent.getAction())) {
            boolean isOpen = intent.getBooleanExtra(KEYCODE_MASK_OPEN, false);
            boolean isOpening = intent.getBooleanExtra(KEYCODE_MASK_ONPROGRESS, false);
            boolean isClose = intent.getBooleanExtra(KEYCODE_MASK_CLOSE, false);

            if (isClose) {
                boolean isNeedOpen = SalesApplication.isNeedStartService;
                if(isNeedOpen) {
                    new Timer().schedule(new TimerTask() {
                        @Override
                        public void run() {
                            TtsUtils.sendTts(context, tts);
                            Intent serviceIntent = new Intent(context, SalesPromotionService.class);
                            context.startService(serviceIntent);
                        }
                    },tts.length() * 200);

                }
            }

        } else if(Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
            L.e("ReceiveMaskBroadcast", "boot_completed");

        }
    }
}
