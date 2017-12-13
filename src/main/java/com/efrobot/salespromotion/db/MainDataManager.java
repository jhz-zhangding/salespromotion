package com.efrobot.salespromotion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.efrobot.salespromotion.provider.SalesProvider;

import java.util.ArrayList;

/**
 * Created by zd on 2017/11/13.
 */
public class MainDataManager {

    private static MainDataManager instance;

    private SQLiteDatabase db = null;

    /**
     * 项目表
     */
    public static String CONTENT_TABLE = SalesProvider.MainItemColumns.TABLE_NAME;

    public static Context mContext;

    public static MainDataManager getInstance(Context context) {
        if (instance == null) {
            instance = new MainDataManager();
        }
        mContext = context;
        return instance;
    }

    /**
     * 查询所插入项的内容
     *
     * @return 返回数据集合
     */
    public ArrayList<MainItemContentBean> queryAllContent() {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<MainItemContentBean> beans = new ArrayList<MainItemContentBean>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, null, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new MainItemContentBean(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            closeDb(db);
        }
        return beans;
    }

    public ArrayList<MainItemContentBean> queryItem(int id) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<MainItemContentBean> beans = new ArrayList<MainItemContentBean>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "_id=?", new String[]{id + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new MainItemContentBean(c));
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            closeDb(db);
        }
        return beans;
    }

    /**
     * 插入项的内容
     *
     * @param bean 内容实体类
     */
    public void insertContent(MainItemContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("itemType", bean.getItemType());
        values.put("goodsName", bean.getGoodsName());
        values.put("goodsGroup", bean.getGoodsGroup());
        values.put("goodsDescription", bean.getGoodsDescription());
        values.put("spareOne", bean.getSpareOne());
        values.put("spareTwo", bean.getSpareTwo());
        db.insert(CONTENT_TABLE, null, values);
        closeDb(db);
    }

    /**
     * 插入项的内容
     *
     * @param bean 内容实体类
     */
    public void updateContent(MainItemContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("itemType", bean.getItemType());
        values.put("goodsName", bean.getGoodsName());
        values.put("goodsGroup", bean.getGoodsGroup());
        values.put("goodsDescription", bean.getGoodsDescription());
        values.put("spareOne", bean.getSpareOne());
        values.put("spareTwo", bean.getSpareTwo());
        db.update(CONTENT_TABLE, values, "_id=? ", new String[]{bean.getId() + ""});
    }

    /**
     * 根据id删除某个项目下的某个内容
     *
     * @param id
     */
    public void deleteContentById(int id) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        db.delete(CONTENT_TABLE, "_id = ? ", new String[]{id + ""});
        closeDb(db);
    }


    private void closeDb(SQLiteDatabase db) {
//        db.close();
    }

}
