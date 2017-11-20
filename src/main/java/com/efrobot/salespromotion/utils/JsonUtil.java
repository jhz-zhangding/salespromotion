package com.efrobot.salespromotion.utils;

import android.text.TextUtils;

import com.efrobot.salespromotion.bean.ItemsContentBean;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @类描述：List相互转化Json
 */
public class JsonUtil {
    private static final String TAG = "JsonUtil";

    /**
     * list集合转为Json字符串
     */
    public static String getJsonStrFromList(List<ItemsContentBean> objects) {
        if (objects != null) {
            if (objects != null && objects.size() > 0) {
                String json = new Gson().toJson(objects);
                return json.toString();
            }
        }
        return "";
    }

    public static List<ItemsContentBean> getListFromJsonStr(String content) {
        Gson gson = new Gson();
        List<ItemsContentBean> list = gson.fromJson(content, new TypeToken<List<ItemsContentBean>>() {
        }.getType());
        return list;
    }

}
