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
    public Observable<Long> insertBook(Book book) {
        return Observable.just(getDaoSession().getBookDao().insert(book));
    }

    @Override
    public Observable<Book> loadBook(long bookId) {
        return Observable.just(getDaoSession().getBookDao().load(bookId));
    }

    @Override
    public void updateBook(Book book) {
        getDaoSession().getBookDao().update(book);
    }

    @Override
    public Observable<List<Book>> loadAllBooks() {
        return Observable.just(getDaoSession().getBookDao().loadAll());
    }
}
