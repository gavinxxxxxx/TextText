package me.gavin.app.read;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.RxTransformer;
import me.gavin.app.StreamHelper;
import me.gavin.app.model.Book;
import me.gavin.app.model.Chapter;
import me.gavin.app.model.Page;
import me.gavin.app.test.CoverFlipper;
import me.gavin.app.test.Pager;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityReadNewBinding;
import me.gavin.util.L;

public class NewReadActivity extends BindingActivity<ActivityReadNewBinding> implements Pager {

    private Book mBook;
    private final List<Chapter> mChapterList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_new;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        long bookId = getIntent().getLongExtra(BundleKey.BOOK_ID, -1);
        mBook = getDataLayer().getShelfService().loadBook(bookId);
        if (mBook == null) {
            return;
        }

        // 状态栏浅色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        mBinding.text.setPager(this);
        mBinding.text.setFlipper(new CoverFlipper());

        initChapter();
    }

    @Override
    public void onFlipped(Page page) {
        mBook.setIndex(page.index);
        mBook.setOffset(page.start);
        getDataLayer().getShelfService().updateBook(mBook);
    }

    @Override
    public void curr() {
        getDataLayer().getSourceService()
                .curr(mBook)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(page -> {
                    mBinding.text.set(page);
                }, L::e);
    }

    @Override
    public void last() {
        Page page = new Page(mBook);
        getDataLayer().getSourceService()
                .last(mBinding.text.header(), page)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    lasting = true;
                    mCompositeDisposable.add(disposable);
                    mBinding.text.add(page, true);
                })
                .doOnComplete(() -> lasting = false)
                .doOnError(throwable -> lasting = false)
                .subscribe(mBinding.text::update, L::e);
    }

    @Override
    public void next() {
        Page page = new Page(mBook);
        getDataLayer().getSourceService()
                .next(mBinding.text.footer(), page)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    mBinding.text.add(page, false);
                })
                .doOnComplete(() -> nexting = false)
                .doOnError(throwable -> nexting = false)
                .subscribe(mBinding.text::update, L::e);
    }

    private boolean lasting, nexting;

    @Override
    protected void onPause() {
        super.onPause();
        if (mBook != null) {
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
                    .directory(mBook.id);
        }
        return Observable.just(mChapterList);
    }
}
