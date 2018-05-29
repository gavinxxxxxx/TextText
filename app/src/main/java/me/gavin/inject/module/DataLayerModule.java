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
    ShelfManager provideShelfManager() {
        return new ShelfManager();
    }

    @Singleton
    @Provides
    SourceManager provideSourceManager() {
        return new SourceManager();
    }

    @Singleton
    @Provides
    SettingManager provideSettingManager() {
        return new SettingManager();
    }

    @Singleton
    @Provides
    DataLayer provideDataLayer(
            ShelfManager shelfManager,
            SourceManager sourceManager,
            SettingManager settingManager) {
        return new DataLayer(shelfManager, sourceManager, settingManager);
    }
}
