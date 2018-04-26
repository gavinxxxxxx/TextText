package me.gavin.app.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.gavin.app.RxTransformer;
import me.gavin.app.model.Book;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivitySearchBinding;
import me.gavin.util.L;

/**
 * 搜索
 *
 * @author gavin.xiong 2018/4/24.
 */
public class SearchActivity extends BindingActivity<ActivitySearchBinding> {

    private final List<Book> mList = new ArrayList<>();
    private BindingAdapter<Book> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        String query = getIntent().getStringExtra(BundleKey.QUERY);
        mBinding.includeToolbar.toolbar.setTitle(query);
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());

        mAdapter = new BindingAdapter<>(this, mList, R.layout.item_search);
        mAdapter.setOnItemClickListener(i -> {
            doDetail(mList.get(i).getId());
            doDirectory(mList.get(i).getId());
        });
        mBinding.recycler.setAdapter(mAdapter);

        doSearch(query);
    }

    private void doSearch(String query) {
        getDataLayer().getSourceService()
                .search(query)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(book -> {
                    mList.add(book);
                    mAdapter.notifyDataSetChanged();
                }, L::e);
    }

    private void doDetail(String id) {
        getDataLayer().getSourceService()
                .detail(id)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(L::e, L::e);
    }

    private void doDirectory(String id) {
        getDataLayer().getSourceService()
                .directory(id)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(L::e, L::e);
    }
}
