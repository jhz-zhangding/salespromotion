package com.efrobot.salespromotion.Env;

import android.os.Environment;

import java.io.File;

/**
 * Created by zd on 2017/11/10.
 */
public class SalesConstant {
    public static final String PATH_ROOT = Environment.getExternalStorageDirectory() + File.separator + "sales";
    public static final String FILE_PATH = Environment.getExternalStorageDirectory() + File.separator + "salespromotionzipfile";//压缩包文件夹
    public static final String FILE_JSON = Environment.getExternalStorageDirectory() + File.separator + "salespromotionzipfile" + File.separator + "salespromotionsource" + File.separator + "SalesPromotionJson.txt";

    //    public static final String PATH_USB = "/storage/usbhost/editdiyzipfile";//usb文件 （拷贝到本机）
    public static final String PATH_USB = "/storage/usbhost";//usb文件 （拷贝到本机）
    public static final String PATH_USB_8_4 = "/storage/usbhost/8_4";//usb文件 （拷贝到本机）
    public static final String PATH_SD = "/storage/extsd";//SD
    public final static String UPDATAACTION = "com.efrobot.compounded.diy.UPDATAACTION";//刷新通知

    //记录上次打开的活动ID
    public static String LAST_OPEN_ACTIVITY_ID = "last_open_activity_id";

    //记录播放模式
    public static String PLAY_MODE = "play_mode";

    public static String POWER_PLAY_MODE = "power_play_mode";
    public static String GAME_PLAY_MODE = "game_play_mode";
    public static String HOME_PLAY_MODE = "home_play_mode";
    public static String BACK_PLAY_MODE = "back_play_mode";

    public static int CIRCLE_MODE = 1;
    public static int ORDER_MODE = 2;

    public static class ProjectInfo {
        public static String PRODUCT_NAME = "<产品名称>";
        public static String PRODUCT_GROUP = "<产品类别>";
        public static String PRODUCT_DETAIL = "<活动描述>";
    }


    public static class ItemType {
        public final static int POWER_TYPE = 1;
        public final static int GAME_TYPE = 2;
        public final static int HOME_TYPE = 3;
        public final static int BACK_TYPE = 4;

    }

}
