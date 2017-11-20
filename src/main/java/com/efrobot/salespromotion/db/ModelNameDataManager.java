package com.efrobot.salespromotion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.activity.ModelNameBean;

import java.util.ArrayList;

/**
 * Created by zd on 2017/11/17.
 */
public class ModelNameDataManager {

    private static ModelNameDataManager instance;

    private SQLiteDatabase db = null;

    public static Context mContext;

    /**
     * 项目中的内容
     */
    public static String CONTENT_TABLE = "modelnamebean";


    public static ModelNameDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new ModelNameDataManager();
        }
        mContext = context;
        return instance;
    }

    /**
     * 查询所插入项的内容
     *
     * @return 返回动作数据集合
     */
    public ArrayList<ModelNameBean> queryAllContent() {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ModelNameBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, null, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ModelNameBean(c));
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
    public void insertContent(ModelNameBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("modelName", bean.getModelName());
        values.put("modelType", bean.getModelType());
        db.insert(CONTENT_TABLE, null, values);
        closDb(db);
    }


    /**
     * 修改某个项目的某一条内容
     *
     * @param bean
     */
    public void upateContent(ModelNameBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("modelName", bean.getModelName());
        values.put("modelType", bean.getModelType());
        db.update(CONTENT_TABLE, values, "_id=? ", new String[]{bean.getId() + ""});
    }


    /**
     * 根据类型查询模板
     */
    public ArrayList<ModelNameBean> queryListByType(int modelType) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ModelNameBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelType=? ", new String[]{modelType + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ModelNameBean(c));
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
     * 查询模板
     */
    public boolean queryModelTypeExits(int modelType) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelType=? ", new String[]{modelType + ""}, null, null, null);
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
