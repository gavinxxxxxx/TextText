//package me.gavin.app.read;
//
//import android.os.Build;
//import android.os.Bundle;
//import android.support.annotation.Nullable;
//import android.support.design.widget.Snackbar;
//import android.support.v7.widget.LinearLayoutManager;
//import android.support.v7.widget.RecyclerView;
//import android.view.Gravity;
//import android.view.View;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import io.reactivex.Observable;
//import io.reactivex.android.schedulers.AndroidSchedulers;
//import io.reactivex.schedulers.Schedulers;
//import me.gavin.app.Config;
//import me.gavin.app.PagerLayoutManager;
//import me.gavin.app.RxTransformer;
//import me.gavin.app.StreamHelper;
//import me.gavin.app.Utils;
//import me.gavin.app.core.model.Book;
//import me.gavin.app.core.model.Chapter;
//import me.gavin.app.core.model.Page;
//import me.gavin.base.BindingActivity;
//import me.gavin.base.BundleKey;
//import me.gavin.base.recycler.BindingAdapter;
//import me.gavin.R;
//import me.gavin.databinding.ActivityReadBinding;
//
//public class ReadActivity extends BindingActivity<ActivityReadBinding> {
//
//    private Book mBook;
//
//    private final List<Page> mPageList = new ArrayList<>();
//    private final List<Chapter> mChapterList = new ArrayList<>();
//
//    @Override
//    protected int getLayoutId() {
//        return R.layout.activity_read;
//    }
//
//    @Override
//    protected void afterCreate(@Nullable Bundle savedInstanceState) {
//        if (!getIntent().hasExtra(BundleKey.BOOK_ID)) {
//            return;
//        }
//
//        // 状态栏浅色图标
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
//        }
//
////        new PagerSnapHelper().attachToRecyclerView(mBinding.recycler);
//
//        long bookId = getIntent().getLongExtra(BundleKey.BOOK_ID, 0);
//        mBook = getDataLayer().getShelfService().loadBook(bookId);
//
//        mBinding.recycler.post(() -> {
//            Config.applySizeChange(mBinding.recycler.getWidth(), mBinding.recycler.getHeight());
//            init();
//        });
//    }
//
//    private void init() {
//        offset(mBook.getOffset());
//        Observable.just(mBook)
//                .map(Book::open)
//                .map(is -> StreamHelper.getChapters(is, mBook.getCharset()))
//                .compose(RxTransformer.applySchedulers())
//                .subscribe(chapters -> {
//                    mChapterList.addAll(chapters);
//                    if (mChapterList.isEmpty()) {
//                        Snackbar.make(mBinding.rvChapter, "暂无章节信息", Snackbar.LENGTH_LONG).show();
//                    } else {
//                        Chapter curr = mChapterList.get(0);
//                        for (Chapter t : mChapterList) {
//                            if (t.offset > mBook.getOffset())
//                                break;
//                            curr = t;
//                        }
//                        curr.selected = true;
//
//                        BindingAdapter adapter = new BindingAdapter<>(this, mChapterList, R.layout.item_chapter);
//                        adapter.setOnItemClickListener(i -> {
//                            for (Chapter t : mChapterList) {
//                                t.selected = false;
//                            }
//                            mChapterList.get(i).selected = true;
//                            adapter.notifyDataSetChanged();
//                            offset(mChapterList.get(i).offset);
//                        });
//                        mBinding.rvChapter.setAdapter(adapter);
//
//                        mBinding.rvChapter.scrollToPosition(mChapterList.indexOf(curr));
//                    }
//                }, Throwable::printStackTrace);
//
//
//        // 章节进度定位
//        mBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            @Override
//            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
//                if (newState == RecyclerView.SCROLL_STATE_IDLE && mBinding.rvChapter.getAdapter() != null) {
//                    LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
//                    int position = layoutManager.findFirstVisibleItemPosition();
//                    long offset = mPageList.get(position).start;
//
//                    mBook.setOffset(offset);
//
//                    Chapter curr = mChapterList.get(0);
//                    for (Chapter t : mChapterList) {
//                        t.selected = false;
//                        if (t.offset <= offset) {
//                            curr = t;
//                        }
//                    }
//                    curr.selected = true;
//                    mBinding.rvChapter.getAdapter().notifyDataSetChanged();
//                    mBinding.rvChapter.scrollToPosition(mChapterList.indexOf(curr));
//                }
//            }
//        });
//
//        // 翻页数据预加载
//        mBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
//            int pagingPreCount = 1;
//
//            @Override
//            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
//                //得到当前显示的最后一个item的view
//                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
//                View firstChild = layoutManager.getChildAt(0);
//                View lastChild = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
//                if (lastChild != null) {
//                    //通过这个lastChildView得到这个view当前的position值
//                    int lastPosition = layoutManager.getPosition(lastChild);
////                    L.e("lastPosition - " + lastPosition);
//                    //判断lastPosition是不是最后一个position
//                    if (lastPosition > layoutManager.getItemCount() - 2 - pagingPreCount) {
//                        performPagingLoad();
//                    }
//                }
//                if (firstChild != null) {
//                    //通过这个lastChildView得到这个view当前的position值
//                    int firstPosition = layoutManager.getPosition(firstChild);
////                    L.e("firstPosition - " + firstPosition);
//                    //判断lastPosition是 不是最后一个position
//                    if (firstPosition < 1 + pagingPreCount) {
//                        performPagingLoad2();
//                    }
//                }
//            }
//        });
//    }
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
//
//    private void offset(long offset) {
//        Observable.just(offset)
//                .map(off -> Utils.nextLocal(new Page(mBook), off))
//                .subscribe(page -> {
//                    mBinding.drawer.closeDrawers();
//                    mPageList.clear();
//                    mPageList.add(page);
//                    mBinding.recycler.setAdapter(new PageAdapter(this, mPageList));
//                    mBinding.recycler.setLayoutManager(new PagerLayoutManager());
//                }, Throwable::printStackTrace);
//    }
//
//    private boolean pagingLoading = false;
//
//    /**
//     * 执行加载更多
//     */
//    public void performPagingLoad() {
//        if (!mPageList.get(mPageList.size() - 1).isLast && !pagingLoading) {
//            pagingLoading = true;
//            getData(false);
//        }
//    }
//
//    private boolean pagingLoading2 = false;
//
//    /**
//     * 执行加载更多
//     */
//    public void performPagingLoad2() {
//        if (!mPageList.get(0).isFirst && !pagingLoading2) {
//            pagingLoading2 = true;
//            getData(true);
//        }
//    }
//
//    public void getData(boolean isReverse) {
//        Observable.just(isReverse ? 0 : mPageList.size() - 1)
//                .map(mPageList::get)
//                .map(page -> {
//                    long offset = page.start + (isReverse ? 0 : page.limit);
//                    return isReverse ? Utils.lastLocal(new Page(mBook), offset) : Utils.nextLocal(new Page(mBook), offset);
//                })
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .doOnComplete(() -> {
//                    if (isReverse) {
//                        pagingLoading2 = false;
//                    } else {
//                        pagingLoading = false;
//                    }
//                })
//                .subscribeOn(AndroidSchedulers.mainThread())
//                .subscribe(page -> {
//                    int index = isReverse ? 0 : mPageList.size();
//                    mPageList.add(index, page);
//                    mBinding.recycler.getAdapter().notifyItemInserted(index);
//                }, Throwable::printStackTrace);
//    }
//}
