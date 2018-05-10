package me.gavin.app.read;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.graphics.ColorUtils;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.Config;
import me.gavin.app.RxTransformer;
import me.gavin.app.StreamHelper;
import me.gavin.app.core.flipper.SimpleFlipper;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.pager.Pager;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityReadNewBinding;

public class NewReadActivity extends BindingActivity<ActivityReadNewBinding> implements Pager {

    private Book mBook;
    private final List<Chapter> mChapterList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_new;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        getWindow().setStatusBarColor(0);
        getWindow().getDecorView().setSystemUiVisibility(0);
        long bookId = getIntent().getLongExtra(BundleKey.BOOK_ID, -1);
        mBook = getDataLayer().getShelfService().loadBook(bookId);
        if (mBook == null) {
            return;
        }

        mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        float[] hsl = new float[3];
        ColorUtils.colorToHSL(Config.bgColor, hsl);
        hsl[1] = Math.max(0, hsl[1] - 0.025f);
        hsl[2] = Math.max(0, hsl[2] - 0.025f);
        mBinding.includeContent.cover.setBackgroundColor(ColorUtils.HSLToColor(hsl));
        mBinding.includeContent.toolbar.setTitle(mBook.name);
        Drawable drawable = getDrawable(R.drawable.ic_arrow_back_24dp);
        drawable.setTint(Config.textColor);
        mBinding.includeContent.toolbar.setNavigationIcon(drawable);
        mBinding.includeContent.toolbar.setNavigationOnClickListener(v -> finish());
        mBinding.includeContent.toolbar.setTitleTextColor(Config.textColor);
        mBinding.includeContent.toolbar.setBackgroundColor(Config.bgColor);

        mBinding.includeContent.text.setPager(this);
        // mBinding.text.setFlipper(new CoverFlipper());
        mBinding.includeContent.text.setFlipper(new SimpleFlipper());
        mBinding.includeContent.text.setOnSystemUiVisibilityChangeListener(visibility -> {
            mBinding.includeContent.cover.setVisibility(visibility);
            mBinding.includeContent.toolbar.setVisibility(visibility);
        });

        initChapter();
    }

    @Override
    public void curr() {
        getDataLayer().getSourceService()
                .curr(mBook)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(page -> {
                    mBinding.includeContent.text.set(page);
                }, Throwable::printStackTrace);
    }

    @Override
    public void last(Page target, Page page) {
        getDataLayer().getSourceService()
                .last(target, page)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(mBinding.includeContent.text::update, Throwable::printStackTrace);
    }

    @Override
    public void next(Page target, Page page) {
        getDataLayer().getSourceService()
                .next(target, page)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(mBinding.includeContent.text::update, Throwable::printStackTrace);
    }

    private boolean lasting, nexting;

    @Override
    protected void onPause() {
        super.onPause();
        if (mBook != null && mBinding.includeContent.text != null && mBinding.includeContent.text.curr() != null) {
            mBook.setIndex(mBinding.includeContent.text.curr().index);
            mBook.setOffset(mBinding.includeContent.text.curr().start);
            mBook.setTime(System.currentTimeMillis());
            getDataLayer().getShelfService().updateBook(mBook);
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (mBinding.drawer.isDrawerOpen(Gravity.START)
                || mBinding.drawer.isDrawerOpen(Gravity.END)) {
            mBinding.drawer.closeDrawers();
        } else {
            super.onBackPressedSupport();
        }
    }

    private void initChapter() {
        directory()
                .compose(RxTransformer.applySchedulers())
                .subscribe(chapters -> {
                    mChapterList.addAll(chapters);
                    if (mChapterList.isEmpty()) {
                        Snackbar.make(mBinding.rvChapter, "暂无章节信息", Snackbar.LENGTH_LONG).show();
                    } else {
                        Chapter curr = mChapterList.get(0);
                        for (Chapter t : mChapterList) {
                            if (t.offset > mBook.getOffset())
                                break;
                            curr = t;
                        }
                        curr.selected = true;

                        BindingAdapter adapter = new BindingAdapter<>(this, mChapterList, R.layout.item_chapter);
                        adapter.setOnItemClickListener(i -> {
                            mBinding.drawer.closeDrawers();
                            for (Chapter t : mChapterList) {
                                t.selected = false;
                            }
                            mChapterList.get(i).selected = true;
                            adapter.notifyDataSetChanged();
                            mBook.index = i;
                            mBook.offset = mBook.type == Book.TYPE_ONLINE ? 0 : mChapterList.get(i).offset;
                            curr();
                        });
                        mBinding.rvChapter.setAdapter(adapter);

                        mBinding.rvChapter.scrollToPosition(mChapterList.indexOf(curr));
                        mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                }, Throwable::printStackTrace);
    }

    private Observable<List<Chapter>> directory() {
        if (mBook.type == Book.TYPE_LOCAL) {
            return Observable.just(mBook)
                    .map(Book::open)
                    .map(is -> StreamHelper.getChapters(is, mBook.getCharset()));
        } else if (mBook.type == Book.TYPE_ONLINE) {
            return getDataLayer().getSourceService()
                    .directory(mBook);
        }
        return Observable.just(mChapterList);
    }
}
