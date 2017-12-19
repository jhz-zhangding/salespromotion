package com.efrobot.salespromotion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.bean.PlayModeBean;

import java.util.ArrayList;

/**
 * Created by zd on 2017/12/18.
 */
public class ModeManager {

    private static ModeManager instance;

    private SQLiteDatabase db = null;

    public static Context mContext;

    /**
     * 项目中的内容
     */
    public static String CONTENT_TABLE = "playmodebean";

    public static ModeManager getInstance(Context context) {
        if (instance == null) {
            instance = new ModeManager();
        }
        mContext = context;
        return instance;
    }

    /**
     * 查询模板
     */
    public ArrayList<PlayModeBean> queryListByName(String modelName) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<PlayModeBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelName=? ", new String[]{modelName + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new PlayModeBean(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return beans;
    }


    /**
     * 查询该是否存在模板
     */
    public boolean isExistByName(String modelName) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelName=? ", new String[]{modelName + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
        }
        return false;
    }

    /**
     * 插入项的内容
     *
     * @param bean 内容实体类
     */
    public void insertContent(PlayModeBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("modelName", bean.getModelName());
        values.put("itemNum", bean.getItemNum());
        values.put("playMode", bean.getPlayMode());
        db.insert(CONTENT_TABLE, null, values);
    }

    /**
     * 插入项的内容
     *
     * @param modeName 模板聪明长续航
     */
    public void deleteContent(String modeName) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ContentValues values = new ContentValues();
        db.delete(CONTENT_TABLE, "modelName = ?", new String[]{modeName});
    }
}
