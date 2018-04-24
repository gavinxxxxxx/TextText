package me.gavin.service;

import io.reactivex.Observable;
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;
import okhttp3.RequestBody;

/**
 * SearchManager
 *
 * @author gavin.xiong 2017/4/28
 */
public class SearchManager extends BaseManager implements DataLayer.SearchService {

    @Override
    public Observable<RequestBody> search(String query) {
        return getApi().yanmoxuanQuery(query);
    }
}
