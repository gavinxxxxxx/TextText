package me.gavin.inject.module;

import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.gavin.db.dao.DaoSession;
import me.gavin.db.DBHelper;

/**
 * DataLayerModule
 *
 * @author gavin.xiong 2017/4/28
 */
@Module
public class DatabaseModule {

    @Singleton
    @Provides
    DaoSession provideDaoSession(Application application) {
        return DBHelper.getDaoSession(application);
    }

}
