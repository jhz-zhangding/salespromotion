package com.efrobot.salespromotion.utils;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.efrobot.library.mvp.utils.L;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Created by admin on 2017/2/27.
 */
public class TtsUtils {
    public static void sendTts(Context context, String content) {
        Log.i("TtsUtils", "content--" + content);
        try {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);
            Intent mIntent = new Intent("com.efrobot.speech.voice.ACTION_TTS");
            mIntent.putExtra("data", jsonObject.toString());
            context.sendBroadcast(mIntent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    /**
     * 关闭TTS
     */
    public static void closeTTs(Context context, String content) {
        try {

            JSONObject jsonObject = new JSONObject();
            jsonObject.put("content", content);
            jsonObject.put("modelType", "speechSleep");
            Intent intent = new Intent("com.efrobot.speech.voice.ACTION_TTS");
            intent.putExtra("data", jsonObject.toString());
            context.sendBroadcast(intent);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    /**
     * 生成Json
     */
    public static String simpleMapToJsonStr(Map<String, Object> map) {
        if (map == null || map.isEmpty()) {
            return "null";
        }
        String jsonStr = "{";
        Set<?> keySet = map.keySet();
        for (Object key : keySet) {
            jsonStr += "\"" + key + "\":\"" + map.get(key) + "\",";
        }
        jsonStr = jsonStr.substring(0, jsonStr.length() - 1);
        jsonStr += "}";
        return jsonStr;
    }
}
