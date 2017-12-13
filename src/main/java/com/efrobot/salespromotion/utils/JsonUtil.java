package com.efrobot.salespromotion.utils;

import com.efrobot.salespromotion.bean.ModelContentBean;
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
    public static String getJsonStrFromList(List<ModelContentBean> objects) {
        if (objects != null) {
            if (objects != null && objects.size() > 0) {
                String json = new Gson().toJson(objects);
                return json.toString();
            }
        }
        return "";
    }

    public static List<ModelContentBean> getListFromJsonStr(String content) {
        Gson gson = new Gson();
        List<ModelContentBean> list = gson.fromJson(content, new TypeToken<List<ModelContentBean>>() {
        }.getType());
        return list;
    }

}
