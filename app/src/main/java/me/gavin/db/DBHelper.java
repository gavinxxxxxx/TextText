package me.gavin.db;

import android.content.Context;

import org.greenrobot.greendao.query.QueryBuilder;

import me.gavin.db.dao.DaoMaster;
import me.gavin.db.dao.DaoSession;

/**
 * 数据库工具类
 *
 * @author gavin.xiong 2018/1/5
 */
public class DBHelper {

    private static final String DEFAULT_DB_NAME = "text.db";

    private static String DB_NAME;
    private static DaoMaster daoMaster;
    private static DaoSession daoSession;

    public static void init(boolean debug) {
        init(DEFAULT_DB_NAME, debug);
    }

    public static void init(String dbName, boolean debug) {
        DB_NAME = dbName;
        if (debug) {
            QueryBuilder.LOG_SQL = true;
            QueryBuilder.LOG_VALUES = true;
        }
    }

    private static DaoMaster getDaoMaster(Context context) {
        if (daoMaster == null) {
            DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(context, DB_NAME);
            daoMaster = new DaoMaster(helper.getWritableDatabase());
        }
        return daoMaster;
    }

    public static DaoSession getDaoSession(Context context) {
        if (daoSession == null) {
            if (daoMaster == null) {
                daoMaster = getDaoMaster(context);
            }
            daoSession = daoMaster.newSession();
        }
        return daoSession;
    }

}
