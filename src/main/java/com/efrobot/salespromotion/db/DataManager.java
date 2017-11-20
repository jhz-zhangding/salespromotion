package com.efrobot.salespromotion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.bean.FaceAndActionEntity;
import com.efrobot.salespromotion.bean.ItemsContentBean;
import com.efrobot.salespromotion.provider.SalesProvider;
import com.efrobot.salespromotion.utils.CsvWrite;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by zd on 2017/8/18.
 */
public class DataManager {

    private static DataManager instance;

    private SQLiteDatabase db = null;

    /**
     * 项目中的内容
     */
    public static String CONTENT_TABLE = SalesProvider.RobotContentColumns.TABLE_NAME;

    /**
     * 动作表
     */
    private static String ACTION_TABLE = SalesProvider.RobotActionColumns.TABLE_NAME;

    public static Context mContext;


    public static DataManager getInstance(Context context) {
        if (instance == null) {
            instance = new DataManager();
        }
        mContext = context;
        return instance;
    }

    /**
     * 动作和表情  1动作 2表情
     *
     * @param context
     * @param path
     */
    public void actionAndFace(Context context, String path, int type) {
        ArrayList<FaceAndActionEntity> beanList = new ArrayList<FaceAndActionEntity>();
        try {
            /**
             * 读取本地资源   <>读取assets的文件</>
             */
            InputStream in = context.getResources().getAssets().open(path);
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
            String line;


            while ((line = reader.readLine()) != null) {
                String item[] = line.split("##");
                int len = item.length;
                if (len > 2) {
                    beanList.add(new FaceAndActionEntity(item[0], item[1], item[2]));
                } else if (len > 1) {
                    beanList.add(new FaceAndActionEntity(item[0], item[1]));
                }
            }
            if (type == 1) {
                //插入动作
                deleteAction();
                insertAction(beanList);
            }
//            else if (type == 2) {
//                //插入表情
//                dao.insertFace(beanList);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 删除保存的动作数据
     */
    public void deleteAction() {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        db.delete(ACTION_TABLE, null, null);
        closDb(db);
    }

    /**
     * 插入动作
     *
     * @param beans 动作类
     */
    public void insertAction(ArrayList<FaceAndActionEntity> beans) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (beans == null || beans.isEmpty())
            return;
        /**
         * 开启事务
         */
        db.beginTransaction();
        try {
            int len = beans.size();
            /**
             * 插入动作
             */
            for (int i = 0; i < len; i++) {
                ContentValues values = new ContentValues();
                values.put(SalesProvider.RobotActionColumns.ACRIONNUM, beans.get(i).index);
                values.put(SalesProvider.RobotActionColumns.ACRIONNAME, beans.get(i).content);
                values.put(SalesProvider.RobotActionColumns.ACRIONTIME, beans.get(i).time);
                db.insert(SalesProvider.RobotActionColumns.TABLE_NAME, null, values);
            }

            /**
             * 设置批量插入成功
             */
            db.setTransactionSuccessful();
        } finally {
            /**
             * 结束事务
             */
            db.endTransaction();
            /**
             * 关闭数据库
             */
            closDb(db);
        }
    }

    /**
     * 查询所有的动作
     *
     * @return 返回动作数据集合
     */
    public ArrayList<FaceAndActionEntity> queryAllAction() {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<FaceAndActionEntity> beans = new ArrayList<FaceAndActionEntity>();
        Cursor c = null;
        try {
            c = db.query(ACTION_TABLE, null, null, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new FaceAndActionEntity(c));
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
     * 查询所插入项的内容
     *
     * @return 返回动作数据集合
     */
    public ArrayList<ItemsContentBean> queryAllContent() {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ItemsContentBean> beans = new ArrayList<ItemsContentBean>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, null, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ItemsContentBean(c));
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
     * 插入项的内容
     *
     * @param bean 内容实体类
     */
    public void insertContent(ItemsContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("itemNum", bean.getItemNum());
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
     * 删除保存的动作数据
     */
    public void deleteAllContent() {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        db.delete(CONTENT_TABLE, null, null);
        closDb(db);
    }

    /**
     * 插入项的内容
     *
     * @param bean 内容实体类
     */
    public boolean insertContentByResult(ItemsContentBean bean) {
        boolean isInsertSuccess = false;
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null) {
            isInsertSuccess = false;
        } else {
            ContentValues values = new ContentValues();
            values.put("itemNum", bean.getItemNum());
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
            isInsertSuccess = true;
        }
        return isInsertSuccess;
    }

    /**
     * 修改某个项目的某一条内容
     *
     * @param bean
     */
    public void upateContent(ItemsContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("itemNum", bean.getItemNum());
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

    public void updateItem(ItemsContentBean bean) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        if (bean == null)
            return;
        ContentValues values = new ContentValues();
        values.put("maxTime", bean.getMaxTime());
        values.put("media", bean.getMedia());
        values.put("music", bean.getMusic());
        db.update(CONTENT_TABLE, values, "_id=? ", new String[]{bean.getId() + ""});
    }

    /**
     */
    public ArrayList<ItemsContentBean> queryItem(int itemNum, int mainType) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ItemsContentBean> beans = new ArrayList<ItemsContentBean>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "itemNum=? and itemType=? ", new String[]{itemNum + "",mainType + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ItemsContentBean(c));
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
    public ArrayList<ItemsContentBean> queryItem(int contentType) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ItemsContentBean> beans = new ArrayList<ItemsContentBean>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "contentType=? ", new String[]{contentType + ""}, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    beans.add(new ItemsContentBean(c));
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
     * 根据id删除某个项目下的某个内容
     *
     * @param id
     */
    public void deleteContentById(int id) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        db.delete(CONTENT_TABLE, "_id = ? ", new String[]{id + ""});
        closDb(db);
    }

    private void closDb(SQLiteDatabase db) {
//        db.close();
    }

    /**
     * 数据库数据写成csv文件
     *
     * @param
     */
    public void writeSql(ArrayList<String> mediaPath) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        Cursor cursorContent = db.query(CONTENT_TABLE, null, null, null, null, null, null);
        CsvWrite.ExportToCSV(cursorContent, "content.csv");
        try {
            CsvWrite.ExportToCopyPath(mediaPath, "copypath.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 数据库媒体文件数据
     *
     * @param
     */
    public ArrayList<String> queryAllMedia() {
        ArrayList<String> list = new ArrayList<String>();
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, null, null, null, null, null);
            if (c != null && c.getCount() > 0) {
                while (c.moveToNext()) {
                    if (c.getString(c.getColumnIndex("media")) != null && !TextUtils.isEmpty(c.getString(c.getColumnIndex("media")))) {
                        if (!list.contains(c.getString(c.getColumnIndex("media")))) {
                            list.add(c.getString(c.getColumnIndex("media")));
                        }
                    }
                    if (c.getString(c.getColumnIndex("music")) != null && !TextUtils.isEmpty(c.getString(c.getColumnIndex("music")))) {
                        if (!list.contains(c.getString(c.getColumnIndex("music")))) {
                            list.add(c.getString(c.getColumnIndex("music")));
                        }
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (c != null)
                c.close();
            if (db != null)
                closDb(db);
        }
        return list;
    }

}
