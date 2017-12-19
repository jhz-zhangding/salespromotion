package com.efrobot.salespromotion;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.TextUtils;

import com.baidu.mobstat.StatService;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.DbHelper;
import com.efrobot.salespromotion.db.MainDataManager;
import com.efrobot.salespromotion.main.MainActivity;
import com.efrobot.salespromotion.player.MediaPlayDialog;
import com.efrobot.salespromotion.service.SalesPromotionService;
import com.efrobot.salespromotion.utils.PreferencesUtils;

import java.util.List;

/**
 * Created by zd on 2017/11/10.
 */
public class SalesApplication extends Application {

    String ROBOT_MASK_CHANGE = "android.intent.action.MASK_CHANGED";
    public final static String KEYCODE_MASK_ONPROGRESS = "KEYCODE_MASK_ONPROGRESS";
    public final static String KEYCODE_MASK_CLOSE = "KEYCODE_MASK_CLOSE";
    public final static String KEYCODE_MASK_OPEN = "KEYCODE_MASK_OPEN";

    private static SalesApplication instance;

    private DbHelper mDbHelper;

    public static boolean isNeedStartService = false;

    public MediaPlayDialog mediaPlayDialog;

    public SalesPromotionService salesPromotionService;

    public MainItemContentBean mainItemContentBean;

    @Override
    public void onCreate() {
        super.onCreate();

        StatService.setDebugOn(true);

        registerDynamicStateReceiver();

        instance = this;
        PreferencesUtils.putInt(this, SalesConstant.POWER_PLAY_MODE, SalesConstant.CIRCLE_MODE);
    }

    private void registerDynamicStateReceiver() {
        MaskBroadCast maskBroadCast = new MaskBroadCast();
        IntentFilter filter = new IntentFilter();
        filter.addAction(ROBOT_MASK_CHANGE);
        registerReceiver(maskBroadCast, filter);
    }

    private class MaskBroadCast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();

            if (action.equals(ROBOT_MASK_CHANGE)) {
                boolean maskOnProgress = intent.getBooleanExtra(KEYCODE_MASK_ONPROGRESS, false);
                boolean maskClose = intent.getBooleanExtra(KEYCODE_MASK_CLOSE, false);
                boolean maskOpen = intent.getBooleanExtra(KEYCODE_MASK_OPEN, false);

                if (maskClose) {
                    if (isNeedStartService) {
                        Intent intentService = new Intent(context, SalesPromotionService.class);
                        intentService.putExtra("currentModelName", MainActivity.currentModelName);
                        intentService.putExtra("currentType", MainActivity.currentType);
                        context.startService(intentService);
                    }
                } else if (maskOpen) {
                    if (salesPromotionService != null) {
                        salesPromotionService.stopSelf();
                    }
                }

            }
        }
    }


    public static SalesApplication getAppContext() {
        return instance;
    }

    public void setSalesPromotionService(SalesPromotionService salesPromotionService) {
        this.salesPromotionService = salesPromotionService;
    }

    public void updateMainItemContentBean() {
        //替换默认商品词条
        List<MainItemContentBean> list = MainDataManager.getInstance(this).queryAllContent();
        if (null != list && list.size() > 0) {
            this.mainItemContentBean = list.get(list.size() - 1);
        }
    }

    public MainItemContentBean getMainItemContentBean() {
        List<MainItemContentBean> list = MainDataManager.getInstance(this).queryAllContent();
        if (null != list && list.size() > 0) {
            this.mainItemContentBean = list.get(list.size() - 1);
        }
        return mainItemContentBean;
    }


    /***
     * 播放DIY视频
     */
    public void playGuestVideoByPath(String path) {

        if (!TextUtils.isEmpty(path)) {
            mediaPlayDialog = new MediaPlayDialog(this);
            mediaPlayDialog.setFilePath(path);
            mediaPlayDialog.show();
        }

    }

    public void dismissGuestVideo() {
        if (mediaPlayDialog != null) {
            mediaPlayDialog.dismiss();
            mediaPlayDialog = null;
        }

    }


    /**
     * 获取数据库操作类
     *
     * @return 数据库操作类
     */

    public synchronized DbHelper getDataBase() {
        if (mDbHelper == null)
            mDbHelper = new DbHelper(getApplicationContext());
        return mDbHelper;
    }

    /**
     * 初始化
     *
     * @param context Application
     * @return Application
     */
    public static SalesApplication from(Context context) {
        if (context != null)
            return (SalesApplication) context.getApplicationContext();
        else return null;
    }
}
