package me.gavin.service.base;

import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.model.Book;
import me.gavin.app.model.Chapter;
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

        Book loadBook(long bookId);

        Observable<List<Book>> loadAllBooks();
    }

    public interface SourceService {
        Observable<Book> search(String query);

        Observable<Book> detail(String id);

        Observable<List<Chapter>> directory(String id);

        Observable<String> chapter(Book book, String cid);
    }
}
