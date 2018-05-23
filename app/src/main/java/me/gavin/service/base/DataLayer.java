package me.gavin.service.base;

import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.source.SourceServicess;
import okhttp3.ResponseBody;

/**
 * DataLayer
 *
 * @author gavin.xiong 2017/4/28
 */
public class DataLayer {

    private ShelfService mShelfService;
    private SourceService mSourceService;
    private SettingService mSettingService;

    public DataLayer(ShelfService shelfService, SourceService sourceService, SettingService settingService) {
        mShelfService = shelfService;
        mSourceService = sourceService;
        mSettingService = settingService;
    }

    public ShelfService getShelfService() {
        return mShelfService;
    }

    public SourceService getSourceService() {
        return mSourceService;
    }

    public SettingService getSettingService() {
        return mSettingService;
    }

    public interface SettingService {
        Observable<ResponseBody> download(String url);
    }

    public interface ShelfService {
        Observable<Long> insertBook(Book book);

        void updateBook(Book book);

        Observable<Long> insertOrUpdate(Book book);

        Book loadBook(long bookId);

        Observable<List<Book>> loadAllBooks();

        void removeBook(Book book);
    }

    public interface SourceService {
        Observable<List<Book>> search(String query);

        Observable<List<Book>> search(SourceServicess source, String query);

        Observable<Book> detail(Book book);

        Observable<List<Chapter>> directory(Book book);

        Observable<String> chapter(SourceServicess source, Book book, int index);

        Observable<Page> curr(Book book);

        Observable<Page> last(Page target, Page page);

        Observable<Page> next(Page target, Page page);
    }
}
