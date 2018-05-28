package me.gavin.service;

import org.greenrobot.greendao.AbstractDao;
import org.greenrobot.greendao.query.QueryBuilder;

import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.source.SourceModel;
import me.gavin.base.App;
import me.gavin.db.dao.BookDao;
import me.gavin.db.dao.DaoSession;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;
import okio.BufferedSource;
import okio.Okio;

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
    public Observable<Long> insertOrUpdate(Book book) {
        return Observable.just(getDaoSession())
                .map(DaoSession::getBookDao)
                .map(AbstractDao::queryBuilder)
                .map(qb -> qb.where(BookDao.Properties.Name.eq(book.name)))
                .map(qb -> qb.where(BookDao.Properties.Author.eq(book.author)))
                .map(QueryBuilder::buildDelete)
                .map(dq -> {
                    dq.executeDeleteWithoutDetachingEntities();
                    return getDaoSession();
                })
                .map(DaoSession::getBookDao)
                .map(bookDao -> bookDao.insert(book));
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
        return Observable.just(getDaoSession())
                .map(DaoSession::getBookDao)
                .map(AbstractDao::queryBuilder)
                .map(qb -> qb.orderDesc(BookDao.Properties.Time))
                .map(QueryBuilder::list)
                .flatMap(Observable::fromIterable)
                .flatMap(book -> {
                    if (book.type == Book.TYPE_LOCAL) {
                        return Observable.just(book);
                    }
                    return Observable.just(book.src)
                            .map(s -> "src/" + s + ".json")
                            .map(App.get().getAssets()::open)
                            .map(Okio::source)
                            .map(Okio::buffer)
                            .map(BufferedSource::readUtf8)
                            .map(json -> ApplicationComponent
                                    .Instance
                                    .get()
                                    .getGson()
                                    .fromJson(json, SourceModel.class))
                            .map(src -> {
                                book.source = src;
                                return book;
                            });
                })
                .toList()
                .toObservable();

//        return Observable.just(getDaoSession()
//                .getBookDao()
//                .queryBuilder()
//                .orderDesc(BookDao.Properties.Time)
//                // .orderAsc(BookDao.Properties.Name) 多重排序
//                .list());
    }

    @Override
    public void removeBook(Book book) {
        getDaoSession().getBookDao().delete(book);
    }
}
