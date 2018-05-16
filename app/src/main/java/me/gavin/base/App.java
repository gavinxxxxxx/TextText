package me.gavin.base;

import android.app.Application;

import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import me.gavin.app.core.source.Source;
import me.gavin.db.dao.SourceDao;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.inject.component.DaggerApplicationComponent;
import me.gavin.inject.module.ApplicationModule;
import me.gavin.util.CrashHandler;
import okio.Okio;

/**
 * 自定义 Application
 *
 * @author gavin.xiong 2017/4/25
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        CrashHandler.get().init();
        initDagger();
        checkSources();
    }

    private void initDagger() {
        ApplicationComponent.Instance.init(DaggerApplicationComponent
                .builder()
                .applicationModule(new ApplicationModule(this))
                .build());
    }

    public static Application get() {
        return ApplicationComponent.Instance.get().getApplication();
    }

    /**
     * 插入书源
     */
    private void checkSources() {
        ApplicationComponent component = ApplicationComponent.Instance.get();
        SourceDao sourceDao = component.getDaoSession().getSourceDao();
        if (sourceDao.count() == 0) {
            try (InputStream is = getAssets().open("source.json")) {
                String json = Okio.buffer(Okio.source(is)).readUtf8();
                List<Source> sources = component.getGson()
                        .fromJson(json, new TypeToken<List<Source>>() {}.getType());
                sourceDao.insertInTx(sources);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
