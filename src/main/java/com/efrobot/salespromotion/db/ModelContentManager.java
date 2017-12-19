package com.efrobot.salespromotion.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.text.TextUtils;

import com.efrobot.salespromotion.SalesApplication;
import com.efrobot.salespromotion.bean.FaceAndActionEntity;
import com.efrobot.salespromotion.bean.ModelContentBean;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by zd on 2017/11/17.
 */
public class ModelContentManager {

    private static ModelContentManager instance;

    private SQLiteDatabase db = null;

    public static Context mContext;

    /**
     * 项目中的内容
     */
    public static String CONTENT_TABLE = "modelcontentbean";

    /**
     * 动作表
     */
    private static String ACTION_TABLE = "faceandactionentity";

    String ACRIONNUM = "actionNum";
    String ACRIONNAME = "actionName";
    String ACRIONTIME = "actionTime";


    public static ModelContentManager getInstance(Context context) {
        if (instance == null) {
            instance = new ModelContentManager();
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
                values.put(ACRIONNUM, beans.get(i).index);
                values.put(ACRIONNAME, beans.get(i).content);
                values.put(ACRIONTIME, beans.get(i).time);
                db.insert(ACTION_TABLE, null, values);
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
        values.put("playMode", bean.getPlayMode());
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
        values.put("playMode", bean.getPlayMode());
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
        values.put("playMode", bean.getPlayMode());
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
    public ArrayList<ModelContentBean> queryItem(String modelName, int modelType) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        ArrayList<ModelContentBean> beans = new ArrayList<>();
        Cursor c = null;
        try {
            c = db.query(CONTENT_TABLE, null, "modelName=? and itemNum=? ", new String[]{modelName, modelType + ""}, null, null, null);
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

    /**
     * 根据modelName删除某个项目下的某个内容
     *
     * @param modelName
     */
    public void deleteContentByModelName(String modelName) {
        db = SalesApplication.from(mContext).getDataBase().getWritableDatabase();
        db.delete(CONTENT_TABLE, "modelName = ? ", new String[]{modelName + ""});
        closDb(db);
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
