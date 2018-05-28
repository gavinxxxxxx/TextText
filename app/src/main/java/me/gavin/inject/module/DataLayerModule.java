package me.gavin.inject.module;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import me.gavin.service.SettingManager;
import me.gavin.service.ShelfManager;
import me.gavin.service.SourceManager2;
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
    SourceManager2 provideSourceManager() {
//        return new SourceManager();
        return new SourceManager2();
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
            SourceManager2 sourceManager,
            SettingManager settingManager) {
        return new DataLayer(shelfManager, sourceManager, settingManager);
    }
}
