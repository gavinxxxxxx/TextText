package me.gavin.inject.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.gavin.service.DaocaorenshuwuManager;
import me.gavin.service.SettingManager;
import me.gavin.service.ShelfManager;
import me.gavin.service.SourceManager;
import me.gavin.service.base.DataLayer;

/**
 * DataLayerModule
 *
 * @author gavin.xiong 2017/4/28
 */
@Module
public class DataLayerModule {

    @Singleton
    @Provides
    public ShelfManager provideShelfManager() {
        return new ShelfManager();
    }

    @Singleton
    @Provides
    public SourceManager provideSourceManager() {
        return new SourceManager();
    }

    @Singleton
    @Provides
    public DaocaorenshuwuManager provideDaocaorenshuwuManager() {
        return new DaocaorenshuwuManager();
    }

    @Singleton
    @Provides
    public SettingManager provideSettingManager() {
        return new SettingManager();
    }

    @Singleton
    @Provides
    public DataLayer provideDataLayer(
            ShelfManager shelfManager,
            DaocaorenshuwuManager sourceManager,
            SettingManager settingManager) {
        return new DataLayer(shelfManager, sourceManager, settingManager);
    }
}
