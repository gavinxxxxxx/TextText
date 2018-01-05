package me.gavin.service;

import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.model.Book;
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;

/**
 * SettingManager
 *
 * @author gavin.xiong 2017/4/28
 */
public class ShelfManager extends BaseManager implements DataLayer.ShelfService {

    @Override
    public Observable<List<Book>> getBooks() {
        return Observable.just(getDaoSession().getBookDao().loadAll());
    }
}
