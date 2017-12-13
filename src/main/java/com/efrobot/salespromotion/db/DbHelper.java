package com.efrobot.salespromotion.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.efrobot.library.mvp.utils.L;
import com.efrobot.salespromotion.bean.ModelContentBean;
import com.efrobot.salespromotion.bean.ModelNameBean;
import com.efrobot.salespromotion.bean.FaceAndActionEntity;
import com.efrobot.salespromotion.bean.MainItemContentBean;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

import java.sql.SQLException;

/**
 * Created by zd on 2017/11/11.
 */
public class DbHelper extends OrmLiteSqliteOpenHelper {
    public final String TAG = this.getClass().getSimpleName();
    private Context context;
    private static int version = 1;
    private static String DB_NAME = "sales_promotion";

    public DbHelper(Context context) {
        super(context, DB_NAME, null, version);
        this.context = context;
    }

    @Override
    public synchronized SQLiteDatabase getWritableDatabase() {
        return super.getWritableDatabase();
    }

    @Override
    public synchronized SQLiteDatabase getReadableDatabase() {
        return super.getReadableDatabase();
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, FaceAndActionEntity.class);
            TableUtils.createTable(connectionSource, MainItemContentBean.class);

            TableUtils.createTable(connectionSource, ModelNameBean.class);
            TableUtils.createTable(connectionSource, ModelContentBean.class);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, ConnectionSource connectionSource, int i, int i1) {
        L.e("DbHelper", "i = " + i + "   i1 = " + i1);
//        try {
//            if (i1 == 2) {
//                /** 更新数据库*/
//                TableUtils.createTableIfNotExists(connectionSource, FaceAndActionEntity.class);
//                TableUtils.createTableIfNotExists(connectionSource, ItemsContentBean.class);
//            }
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
    }
}
