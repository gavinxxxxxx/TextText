package me.gavin.service.base;

import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.model.Book;
import okhttp3.ResponseBody;

/**
 * DataLayer
 *
 * @author gavin.xiong 2017/4/28
 */
public class DataLayer {

    private ShelfService mShelfService;
    private SettingService mSettingService;

    public DataLayer(ShelfService shelfService, SettingService settingService) {
        mShelfService = shelfService;
        mSettingService = settingService;
    }

    public ShelfService getShelfService() {
        return mShelfService;
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

}
