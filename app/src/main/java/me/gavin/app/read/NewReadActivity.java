package me.gavin.app.read;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.Config;
import me.gavin.app.RxTransformer;
import me.gavin.app.StreamHelper;
import me.gavin.app.model.Book;
import me.gavin.app.model.Chapter;
import me.gavin.app.model.Page;
import me.gavin.app.test.CoverFlipper;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityReadNewBinding;

public class NewReadActivity extends BindingActivity<ActivityReadNewBinding> {

    private Book mBook;

    private final Page[] mPages = new Page[3];
    private final List<Chapter> mChapterList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_new;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        if (!getIntent().hasExtra(BundleKey.BOOK_ID)) {
            return;
        }

        // 状态栏浅色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        long bookId = getIntent().getLongExtra("bookId", 0);
        mBook = getDataLayer().getShelfService().loadBook(bookId);

        mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

        new CoverFlipper(mBook).attach(mBinding.text);
//        mBinding.text.getFlipper().setOnPageChangeCallback(page -> {
//            // 更新进度
//            mBook.setOffset(mPages[1].start);
//            // 章节定位
//            if (!mChapterList.isEmpty()) {
//                Chapter curr = mChapterList.get(0);
//                for (Chapter t : mChapterList) {
//                    t.selected = false;
//                    if (t.offset <= mBook.getOffset()) {
//                        curr = t;
//                    }
//                }
//                curr.selected = true;
//                mBinding.rvChapter.getAdapter().notifyDataSetChanged();
//                mBinding.rvChapter.scrollToPosition(mChapterList.indexOf(curr));
//            }
//        });
        init();
        mBinding.text.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
            if (r - l != or - ol || b - t != ob - ot) {
                Config.applySizeChange(r - l, b - t);
                mBinding.text.getFlipper().offset(null);
            }
        });
    }

    private void init() {
        Observable.just(mBook)
                .map(Book::open)
                .map(is -> StreamHelper.getChapters(is, mBook.getCharset()))
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
                            for (Chapter t : mChapterList) {
                                t.selected = false;
                            }
                            mChapterList.get(i).selected = true;
                            adapter.notifyDataSetChanged();
                            // TODO: 2018/4/27  offset(mChapterList.get(i).offset);
                        });
                        mBinding.rvChapter.setAdapter(adapter);

                        mBinding.rvChapter.scrollToPosition(mChapterList.indexOf(curr));
                        mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                    }
                }, Throwable::printStackTrace);
    }

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
}
