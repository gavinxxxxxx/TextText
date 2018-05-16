package me.gavin.app.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;

import java.util.ArrayList;
import java.util.List;

import me.gavin.app.RxTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.source.SourceServicess;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivitySearchBinding;

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
            Book book = mList.get(i);
            book.setTime(System.currentTimeMillis());
            getDataLayer().getShelfService()
                    .insertBook(book)
                    .subscribe(aLong -> {
                        startActivity(new Intent(this, DetailActivity.class)
                                .putExtra(BundleKey.BOOK_ID, aLong));
                    });
        });
        mBinding.recycler.setAdapter(mAdapter);

        mBinding.refresh.setOnRefreshListener(() -> doSearch(query));
        doSearch(query);
    }

    private void doSearch(String query) {
        List<SourceServicess> sources = new ArrayList<>();
        sources.add(SourceServicess.getSource("ymoxuan"));
        sources.add(SourceServicess.getSource("daocaorenshuwu"));
        getDataLayer().getSourceService()
                .search(sources, query)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    mBinding.refresh.setRefreshing(true);
                })
                .doOnComplete(() -> mBinding.refresh.setRefreshing(false))
                .doOnError(throwable -> mBinding.refresh.setRefreshing(false))
                .subscribe(book -> {
                    mList.add(book);
                    mAdapter.notifyDataSetChanged();
                }, t -> Snackbar.make(mBinding.recycler, t.getMessage(), Snackbar.LENGTH_LONG).show());
    }
}
