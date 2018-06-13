package me.gavin.app.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.MenuItem;

import java.util.Arrays;
import java.util.List;

import me.gavin.app.RxTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.read.NewReadActivity;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityDetailBinding;
import me.gavin.util.L;

/**
 * 详情
 *
 * @author gavin.xiong 2018/4/26.
 */
public class DetailActivity extends BindingActivity<ActivityDetailBinding> {

    private Book mBook;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mBook = (Book) getIntent().getSerializableExtra(BundleKey.BOOK);

        mBinding.includeToolbar.toolbar.setTitle("书籍详情");
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());
        initMenu();

        mBinding.recycler.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mBinding.fab.setOnClickListener(v -> aaa(0));

        mBinding.setItem(mBook);

        detail();
    }

    private void initMenu() {
        mBinding.includeToolbar.toolbar.inflateMenu(R.menu.activity_details);
        MenuItem source = mBinding.includeToolbar.toolbar.getMenu().findItem(R.id.action_source);
        source.setTitle(mBook.srcName);
        List<String> srcs = Arrays.asList(mBook.srcs.split(","));
        List<String> srcNames = Arrays.asList(mBook.srcNames.split(","));
        List<String> ids = Arrays.asList(mBook.ids.split(","));
        for (String s : srcNames) {
            source.getSubMenu().add(s);
        }
        mBinding.includeToolbar.toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() != R.id.action_source) {
                source.setTitle(item.getTitle());
                int index = srcNames.indexOf(source.getTitle().toString());
                mBook.src = srcs.get(index);
                mBook.srcName = srcNames.get(index);
                mBook.id = ids.get(index);
                getDataLayer().getSourceService().resetSource(mBook);
                detail();
            }
            return true;
        });
    }

    private void detail() {
        getDataLayer().getSourceService()
                .detail(mBook)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(book -> {
                    mBinding.setItem(mBook);
                    BindingAdapter<Chapter> adapter = new BindingAdapter<>(this, book.chapters, R.layout.item_chapter);
                    adapter.setOnItemClickListener(chapter -> aaa(book.chapters.indexOf(chapter)));
                    mBinding.recycler.setAdapter(adapter);
                    mBinding.fab.show();
                }, Throwable::printStackTrace);
    }

    private void aaa(int index) {
        mBook.index = index;
        mBook.offset = 0;
        getDataLayer().getShelfService()
                .insertOrUpdate(mBook)
                .subscribe(bookId -> {
                    startActivity(new Intent(this, NewReadActivity.class)
                            .putExtra(BundleKey.BOOK_ID, bookId));
                }, L::e);
    }

    public void chapter(int i) {
        mBook.setIndex(i);
        getDataLayer().getSourceService()
                .chapter(mBook, mBook.index)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(s -> {
                    L.e(s);
//                    mBook.setText(s);
//                    mBook.setOffset(0);
//                    getDataLayer().getShelfService().updateBook(mBook);
//                    startActivity(new Intent(this, NewReadActivity.class)
//                            .putExtra(BundleKey.BOOK_ID, mBook.get_id()));
                }, Throwable::printStackTrace);
    }
}
