package com.efrobot.salespromotion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.activity.ModelContentBean;

import java.util.ArrayList;

/**
 * Created by zd on 2017/11/17.
 */
public class ModelDataManager {

    private static ModelDataManager instance;

    private SQLiteDatabase db = null;

    public static Context mContext;

    /**
     * 项目中的内容
     */
    public static String CONTENT_TABLE = "modelcontentbean";


    public static ModelDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new ModelDataManager();
        }
        mContext = context;
        return instance;
    }

    /**
     * 查询所插入项的内容
     *
     * @return 返回动作数据集合
     */
    public ArrayList<ModelContentBean> queryAllContent() {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ModelContentBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, null, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ModelContentBean(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            closDb(db);
        }
        return beans;
    }

    private void closDb(SQLiteDatabase db) {
//        db.close();
    }


    /**
     * 插入项的内容
     *
     * @param bean 内容实体类
     */
    public void insertContent(ModelContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("itemNum", bean.getItemNum());
        values.put("modelName", bean.getModelName());
        values.put("modelType", bean.getModelType());
        values.put("itemType", bean.getItemType());
        values.put("sport", bean.getSport());
        values.put("face", bean.getFace());
        values.put("action", bean.getAction());
        values.put("light", bean.getLight());
        values.put("other", bean.getOther());
        values.put("media", bean.getMedia());
        values.put("time", bean.getTime());
        values.put("music", bean.getMusic());
        values.put("head", bean.getHead());
        values.put("wheel", bean.getWheel());
        values.put("wing", bean.getWing());
        values.put("openLightTime", bean.getOpenLightTime());
        values.put("flickerLightTime", bean.getFlickerLightTime());
        values.put("faceTime", bean.getFaceTime());
        values.put("actionSystemTime", bean.getActionSystemTime());
        values.put("maxTime", bean.getMaxTime());
        values.put("startAppAction", bean.getStartAppAction());
        values.put("startAppName", bean.getStartAppName());
        values.put("startGuestTimePart", bean.getStartGuestTimePart());
        values.put("danceName", bean.getDanceName());
        db.insert(CONTENT_TABLE, null, values);
        closDb(db);
    }

    /**
     * 插入项的内容
     *
     * @param bean 内容实体类
     */
    public Boolean insertContentByResult(ModelContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return false;
        ContentValues values = new ContentValues();
        values.put("itemNum", bean.getItemNum());
        values.put("modelName", bean.getModelName());
        values.put("modelType", bean.getModelType());
        values.put("itemType", bean.getItemType());
        values.put("sport", bean.getSport());
        values.put("face", bean.getFace());
        values.put("action", bean.getAction());
        values.put("light", bean.getLight());
        values.put("other", bean.getOther());
        values.put("media", bean.getMedia());
        values.put("time", bean.getTime());
        values.put("music", bean.getMusic());
        values.put("head", bean.getHead());
        values.put("wheel", bean.getWheel());
        values.put("wing", bean.getWing());
        values.put("openLightTime", bean.getOpenLightTime());
        values.put("flickerLightTime", bean.getFlickerLightTime());
        values.put("faceTime", bean.getFaceTime());
        values.put("actionSystemTime", bean.getActionSystemTime());
        values.put("maxTime", bean.getMaxTime());
        values.put("startAppAction", bean.getStartAppAction());
        values.put("startAppName", bean.getStartAppName());
        values.put("startGuestTimePart", bean.getStartGuestTimePart());
        values.put("danceName", bean.getDanceName());
        db.insert(CONTENT_TABLE, null, values);
        closDb(db);
        return true;
    }


    /**
     * 修改某个项目的某一条内容
     *
     * @param bean
     */
    public void upateContent(ModelContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("itemNum", bean.getItemNum());
        values.put("modelName", bean.getModelName());
        values.put("modelType", bean.getModelType());
        values.put("itemType", bean.getItemType());
        values.put("sport", bean.getSport());
        values.put("face", bean.getFace());
        values.put("action", bean.getAction());
        values.put("light", bean.getLight());
        values.put("other", bean.getOther());
        values.put("media", bean.getMedia());
        values.put("time", bean.getTime());
        values.put("music", bean.getMusic());
        values.put("head", bean.getHead());
        values.put("wheel", bean.getWheel());
        values.put("wing", bean.getWing());
        values.put("openLightTime", bean.getOpenLightTime());
        values.put("flickerLightTime", bean.getFlickerLightTime());
        values.put("faceTime", bean.getFaceTime());
        values.put("actionSystemTime", bean.getActionSystemTime());
        values.put("maxTime", bean.getMaxTime());
        values.put("startAppAction", bean.getStartAppAction());
        values.put("startAppName", bean.getStartAppName());
        values.put("startGuestTimePart", bean.getStartGuestTimePart());
        values.put("danceName", bean.getDanceName());
        db.update(CONTENT_TABLE, values, "_id=? ", new String[]{bean.getId() + ""});
    }


    /**
     * 查询模板
     */
    public ArrayList<ModelContentBean> queryItem(int modelType) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ModelContentBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelType=? ", new String[]{modelType + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ModelContentBean(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            closDb(db);
        }
        return beans;
    }

    /**
     * 查询模板
     */
    public ArrayList<ModelContentBean> queryItem(String modelName) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ModelContentBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelName=? ", new String[]{modelName}, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ModelContentBean(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            closDb(db);
        }
        return beans;
    }

    /**
     * 查询模板
     */
    public boolean queryModelNameExits(String modelName) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ModelContentBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelName=? ", new String[]{modelName}, null, null, null);
            if (c != null && c.getCount() > 0) {
                return true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            closDb(db);
        }
        return false;
    }

    /**
     * 根据id删除某个项目下的某个内容
     *
     * @param id
     */
    public void deleteContentById(int id) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        db.delete(CONTENT_TABLE, "_id = ? ", new String[]{id + ""});
        closDb(db);
    }

}
