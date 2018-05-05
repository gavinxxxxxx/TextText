package me.gavin.service;

import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.core.model.Book;
import me.gavin.db.dao.BookDao;
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
    public Book loadBook(long bookId) {
        return getDaoSession().getBookDao().load(bookId);
    }

    @Override
    public void updateBook(Book book) {
        getDaoSession().getBookDao().update(book);
    }

    @Override
    public Observable<List<Book>> loadAllBooks() {
        return Observable.just(getDaoSession()
                .getBookDao()
                .queryBuilder()
                .orderDesc(BookDao.Properties.Time)
                // .orderAsc(BookDao.Properties.Name) 多重排序
                .list());
    }

    @Override
    public void removeBook(Book book) {
        getDaoSession().getBookDao().delete(book);
    }
}
