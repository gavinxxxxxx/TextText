package me.gavin.app.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.util.DiffUtil;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import me.gavin.app.RxTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivitySearchBinding;

/**
 * 搜索
 *
 * @author gavin.xiong 2018/4/24.
 */
public class SearchActivity extends BindingActivity<ActivitySearchBinding> {

    private final List<Book> mList = new ArrayList<>();
    private SearchAdapter mAdapter;

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
        mBinding.includeToolbar.toolbar.inflateMenu(R.menu.activity_search);
        mBinding.includeToolbar.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_source) {
                startActivity(new Intent(this, SourceActivity.class));
            }
            return true;
        });

        mAdapter = new SearchAdapter(this, mList);
        mAdapter.setOnItemClickListener(i -> startActivity(
                new Intent(this, DetailActivity.class)
                        .putExtra(BundleKey.BOOK, mList.get(i))));
        mBinding.recycler.setAdapter(mAdapter);

        mBinding.refresh.setOnRefreshListener(() -> doSearch(query));
        doSearch(query);
    }

    private void doSearch(String query) {
        getDataLayer().getSourceService()
                .search(query)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    mBinding.refresh.setRefreshing(true);
                    mList.clear();
                    mAdapter.notifyDataSetChanged();
                })
                .doOnComplete(() -> mBinding.refresh.setRefreshing(false))
                .doOnError(throwable -> mBinding.refresh.setRefreshing(false))
                .map(this::distinctAndSoft)
                .subscribe(books -> {
                    DiffUtil.calculateDiff(new DiffCallback(mList, books))
                            .dispatchUpdatesTo(new DiffUpdateCallback(mBinding.recycler));
                    mList.clear();
                    mList.addAll(books);
                }, t -> {
                    t.printStackTrace();
                    Snackbar.make(mBinding.recycler, t.getMessage(), Snackbar.LENGTH_LONG).show();
                });
    }

    @NonNull
    private List<Book> distinctAndSoft(List<Book> insertList) {
        final List<Book> newList = new ArrayList<>();
        newList.addAll(mList);
        for (Book nb : insertList) {
            boolean c = false;
            for (Book ob : mList) {
                if (ob.name.equals(nb.name) && ob.author.equals(nb.author)) {
                    c = true;
                    nb.srcCount = ob.srcCount + 1;
                    nb.ids = ob.ids.concat("," + nb.id);
                    nb.srcs = ob.srcs.concat("," + nb.src);
                    nb.srcNames = ob.srcNames.concat("," + nb.srcName);
                    newList.remove(ob);
                    newList.add(nb);
                    break;
                }
            }
            if (!c) {
                nb.srcCount = 1;
                nb.ids = nb.id;
                nb.srcs = nb.src;
                nb.srcNames = nb.srcName;
                newList.add(nb);
            }
        }
        Collections.sort(newList, (o1, o2) -> o2.srcCount - o1.srcCount);
        return newList;
    }
}
