package me.gavin.app.read;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import me.gavin.app.model.Book;
import me.gavin.app.model.Page;
import me.gavin.base.BindingActivity;
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityReadBinding;
import me.gavin.text.databinding.ItemTextBinding;
import me.gavin.util.SPUtil;

public class ReadActivity extends BindingActivity<ActivityReadBinding> {

    private List<Page> mPageList = new ArrayList<>();

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        Uri uri = getIntent().getData();
        if (uri == null) {
            return;
        }

        openBook(uri.getPath());

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
        LinearLayoutManager layoutManager = (LinearLayoutManager) mBinding.recycler.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        SPUtil.putLong("offset", mPageList.get(position).pageStart);
    }

    private void openBook(String path) {
        Book book = Book.fromSDCard(path);
        mPageList.clear();
        mPageList.add(Page.fromBook(book, SPUtil.getLong("offset", 220000), false));
        mBinding.recycler.setAdapter(new Adapter(this, mPageList));
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

    public static class Adapter extends RecyclerAdapter<Page, ItemTextBinding> {

        Adapter(Context context, List<Page> list) {
            super(context, list, R.layout.item_text);
        }

        @Override
        protected void onBind(RecyclerHolder<ItemTextBinding> holder, Page t, int position) {
            holder.binding.text.set(t);
        }
    }
}
