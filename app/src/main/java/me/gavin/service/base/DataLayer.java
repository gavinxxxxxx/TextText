package me.gavin.service.base;

import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.model.Book;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;

/**
 * DataLayer
 *
 * @author gavin.xiong 2017/4/28
 */
public class DataLayer {

    private ShelfService mShelfService;
    private SearchService mSearchService;
    private SettingService mSettingService;

    public DataLayer(ShelfService shelfService, SearchService searchService, SettingService settingService) {
        mShelfService = shelfService;
        mSearchService = searchService;
        mSettingService = settingService;
    }

    public ShelfService getShelfService() {
        return mShelfService;
    }

    public SearchService getSearchService() {
        return mSearchService;
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

    public interface SearchService {
        Observable<RequestBody> search(String query);
    }

}
