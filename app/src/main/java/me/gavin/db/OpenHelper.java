package me.gavin.db;

import android.content.Context;

import org.greenrobot.greendao.database.Database;

import me.gavin.db.dao.DaoMaster;

/**
 * 数据库助手类
 *
 * @author gavin.xiong 2018/5/16
 */
public class OpenHelper extends DaoMaster.DevOpenHelper {

    public OpenHelper(Context context, String name) {
        super(context, name);
    }

    @Override
    public void onCreate(Database db) {
        super.onCreate(db);
    }

    @Override
    public void onUpgrade(Database db, int oldVersion, int newVersion) {
        super.onUpgrade(db, oldVersion, newVersion);
    }
}
