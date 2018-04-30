//package me.gavin.app.read;
//
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.view.Gravity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import me.gavin.app.Config;
//import me.gavin.app.Utils;
//import me.gavin.app.model.Book;
//import me.gavin.app.model.Chapter;
//import me.gavin.app.model.Page;
//import me.gavin.app.test.CoverFlipper;
//import me.gavin.app.test.OnlinePager;
//import me.gavin.base.BindingActivity;
//import me.gavin.base.BundleKey;
//import me.gavin.text.R;
//import me.gavin.text.databinding.ActivityReadNewBinding;
//
//public class OnlineReadActivity extends BindingActivity<ActivityReadNewBinding> {
//
//    private Book mBook;
//
//    private final List<Chapter> mChapterList = new ArrayList<>();
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_read_new;
//    }
//
//    @Override
//    protected void afterCreate(@Nullable Bundle savedInstanceState) {
//        if (!getIntent().hasExtra(BundleKey.BOOK_ID)) {
//            return;
//        }
//
//        long bookId = getIntent().getLongExtra(BundleKey.BOOK_ID, 0);
//        mBook = getDataLayer().getShelfService().loadBook(bookId);
//
//        mBinding.text.setPager(new OnlinePager());
//        mBinding.text.setFlipper(new CoverFlipper());
//        mBinding.text.setOnFlipCallback(page -> {
//            mBook.setOffset(page.start);
//            getDataLayer().getShelfService().updateBook(mBook);
//        });
//        mBinding.text.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
//            if (r - l != or - ol || b - t != ob - ot) {
//                Config.applySizeChange(r - l, b - t);
//                mBinding.text.setBook(mBook);
//            }
//        });
//
//
////        initChapter();
//    }
//
//
//    private void offset(Page src, boolean reserve) {
//        Page page = new Page();
//        page.book = mBook;
//        page.isReverse = reserve;
//        if (!reserve) { // 正向
//            if (page.end < page.chapter.length()) { // 章节未结束
//                page.start = src.end;
//                page.isFirst = page.start <= 0;
//                page.mText = page.book.getText().substring((int) page.start, Math.min((int) page.start + Config.pagePreCount, page.book.getText().length()));
//                page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
//                page.align = true;
//                if (page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)) {
//                    page.indent = true;
//                } else {
//                    int preCount = (int) (page.start >= Config.segmentPreCount ? Config.segmentPreCount : page.start);
//                    String fix = page.book.getText().substring((int) page.start - preCount, (int)page.start);
//                    page.indent = fix.matches(Config.REGEX_SEGMENT_PREFIX);
//                }
//            } else { // 下一章
//                page.loading = true;
//                page.start = 0;
//                page.isFirst = false;
//            }
//        }
//    }
//
//
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        if (mBook != null) {
//            mBook.setTime(System.currentTimeMillis());
//            getDataLayer().getShelfService().updateBook(mBook);
//        }
//    }
//
//    @Override
//    public void onBackPressedSupport() {
//        if (mBinding.drawer.isDrawerOpen(Gravity.START)
//                || mBinding.drawer.isDrawerOpen(Gravity.END)) {
//            mBinding.drawer.closeDrawers();
//        } else {
//            super.onBackPressedSupport();
//        }
//    }
//}
