package me.gavin.service.base;

import com.google.gson.Gson;

import javax.inject.Inject;

import me.gavin.db.dao.DaoSession;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.net.ClientAPI;

/**
 * BaseManager
 *
 * @author gavin.xiong 2017/4/28
 */
public abstract class BaseManager {

    @Inject
    ClientAPI mApi;
    @Inject
    Gson mGson;
    @Inject
    DaoSession mDaoSession;

    public BaseManager() {
        ApplicationComponent.Instance.get().inject(this);
    }

    public ClientAPI getApi() {
        return mApi;
    }

    public DaoSession getDaoSession() {
        return mDaoSession;
    }

    public Gson getGson() {
        return mGson;
    }
}
