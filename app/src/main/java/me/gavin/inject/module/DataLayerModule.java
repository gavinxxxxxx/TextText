package me.gavin.inject.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
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
    public SourceManager provideSearchManager() {
        return new SourceManager();
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
            SourceManager searchManager,
            SettingManager settingManager) {
        return new DataLayer(shelfManager, searchManager, settingManager);
    }
}
