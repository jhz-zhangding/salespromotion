package com.efrobot.salespromotion;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.baidu.mobstat.StatService;
import com.efrobot.salespromotion.Env.SalesConstant;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.db.DbHelper;
import com.efrobot.salespromotion.db.MainDataManager;
import com.efrobot.salespromotion.player.MediaPlayDialog;
import com.efrobot.salespromotion.service.SalesPromotionService;
import com.efrobot.salespromotion.utils.PreferencesUtils;

import java.util.List;

/**
 * Created by zd on 2017/11/10.
 */
public class SalesApplication extends Application {

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

        instance = this;
        PreferencesUtils.putInt(this, SalesConstant.GAME_PLAY_MODE, SalesConstant.ORDER_MODE);
        PreferencesUtils.putInt(this, SalesConstant.HOME_PLAY_MODE, SalesConstant.ORDER_MODE);
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
