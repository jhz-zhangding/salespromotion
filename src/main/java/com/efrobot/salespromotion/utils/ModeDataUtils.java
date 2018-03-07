package com.efrobot.salespromotion.utils;

import com.efrobot.library.mvp.utils.L;
import com.efrobot.salespromotion.bean.PlayModeBean;

import java.util.List;

/**
 * Created by zd on 2017/12/18.
 */
public class ModeDataUtils {

    private static ModeDataUtils instance;

    public static ModeDataUtils getInstance() {
        if (instance == null) {
            instance = new ModeDataUtils();
        }
        return instance;
    }

    public void setData(List<PlayModeBean> list) {
        if (list != null && list.size() > 0) {
            L.e("ModeDataUtils", "setData");


        }
    }

}
