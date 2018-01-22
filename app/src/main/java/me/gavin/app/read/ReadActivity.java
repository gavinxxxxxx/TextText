package me.gavin.app.read;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SnapHelper;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
import me.gavin.app.RxTransformer;
import me.gavin.app.StreamHelper;
import me.gavin.app.model.Book;
import me.gavin.app.model.Chapter;
import me.gavin.app.model.Page;
import me.gavin.base.BindingActivity;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityReadBinding;
import me.gavin.util.DisplayUtil;

public class ReadActivity extends BindingActivity<ActivityReadBinding> {

    private Book mBook;

    private List<Page> mPageList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        if (!getIntent().hasExtra("bookId")) {
            return;
        }

        // 状态栏浅色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        }

        Observable.just(getIntent())
                .map(intent -> intent.getLongExtra("bookId", 0))
                .flatMap(id -> getDataLayer().getShelfService().loadBook(id))
                .subscribe(book -> {
                    mBook = book;
                    offset(book.getOffset());
                }, Throwable::printStackTrace);

        mBinding.rvChapter.getLayoutParams().width = DisplayUtil.getScreenWidth() / 3 * 2;
        Observable.just(mBook)
                .map(Book::open)
                .map(is -> StreamHelper.getChapters(is, mBook.getCharset()))
                .compose(RxTransformer.applySchedulers())
                .subscribe(chapters -> {
                    if (chapters.isEmpty()) {
                        Snackbar.make(mBinding.rvChapter, "暂无章节信息", Snackbar.LENGTH_LONG).show();
                    } else {
                        Chapter curr = chapters.get(0);
                        for (Chapter t : chapters) {
                            if (t.offset > mBook.getOffset())
                                break;
                            curr = t;
                        }
                        curr.selected = true;

                        BindingAdapter adapter = new BindingAdapter<>(this, chapters, R.layout.item_chapter);
                        adapter.setOnItemClickListener(i -> {
                            for (Chapter t : chapters) {
                                t.selected = false;
                            }
                            chapters.get(i).selected = true;
                            adapter.notifyDataSetChanged();
                            offset(chapters.get(i).offset);
                        });
                        mBinding.rvChapter.setAdapter(adapter);

                        mBinding.rvChapter.scrollToPosition(chapters.indexOf(curr));
                    }
                }, Throwable::printStackTrace);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(mBinding.recycler);

        mBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int pagingPreCount = 0;

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                //得到当前显示的最后一个item的view
                RecyclerView.LayoutManager layoutManager = recyclerView.getLayoutManager();
                View firstChild = layoutManager.getChildAt(0);
                View lastChild = layoutManager.getChildAt(layoutManager.getChildCount() - 1);
                if (lastChild != null) {
                    //通过这个lastChildView得到这个view当前的position值
                    int lastPosition = layoutManager.getPosition(lastChild);
                    //判断lastPosition是不是最后一个position
                    if (lastPosition > layoutManager.getItemCount() - 2 - pagingPreCount) {
                        performPagingLoad();
                    }
                }
                if (firstChild != null) {
                    //通过这个lastChildView得到这个view当前的position值
                    int firstPosition = layoutManager.getPosition(firstChild);
                    //判断lastPosition是不是最后一个position
                    if (firstPosition < 1 + pagingPreCount) {
                        performPagingLoad2();
                    }
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (mBook != null) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mBinding.recycler.getLayoutManager();
            int position = layoutManager.findFirstVisibleItemPosition();
            mBook.setOffset(mPageList.get(position).pageStart);
            mBook.setTime(System.currentTimeMillis());
            getDataLayer().getShelfService().updateBook(mBook);
        }
    }

    private void offset(long offset) {
        Observable.just(offset)
                .map(off -> Page.fromBook(mBook, off, false))
                .subscribe(page -> {
                    mBinding.drawer.closeDrawers();
                    mPageList.clear();
                    mPageList.add(page);
                    mBinding.recycler.setAdapter(new PageAdapter(this, mPageList));
                }, Throwable::printStackTrace);
    }

    private boolean pagingLoading = false;

    /**
     * 执行加载更多
     */
    public void performPagingLoad() {
        if (!mPageList.get(mPageList.size() - 1).isLast && !pagingLoading) {
            pagingLoading = true;
            getData(false);
        }
    }

    private boolean pagingLoading2 = false;

    /**
     * 执行加载更多
     */
    public void performPagingLoad2() {
        if (!mPageList.get(0).isFirst && !pagingLoading2) {
            pagingLoading2 = true;
            getData(true);
        }
    }

    public void getData(boolean isReverse) {
        Observable.just(isReverse ? 0 : mPageList.size() - 1)
                .map(mPageList::get)
                .map(page -> {
                    long offset = page.pageStart + (isReverse ? 0 : page.pageLimit);
                    return Page.fromBook(page.book, offset, isReverse);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (isReverse) {
                        pagingLoading2 = false;
                    } else {
                        pagingLoading = false;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(page -> {
                    int index = isReverse ? 0 : mPageList.size();
                    mPageList.add(index, page);
                    mBinding.recycler.getAdapter().notifyItemInserted(index);
                }, Throwable::printStackTrace);
    }
}
