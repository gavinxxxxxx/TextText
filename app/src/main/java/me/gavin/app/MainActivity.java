package me.gavin.app;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
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
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityMainBinding;
import me.gavin.text.databinding.ItemTextBinding;
import me.gavin.util.DisplayUtil;
import me.gavin.util.L;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    private List<Page> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏深色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        Book book = Book.fromSDCard("/gavin/book/zx.8.txt");
        list.add(Page.fromBook(book, 630000, false));
//        list.add(Page.fromBook(book, 1630000, false));
        mBinding.recycler.setAdapter(new Adapter(this, list));
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(mBinding.recycler);

        mBinding.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            int pagingPreCount = 1;

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

    private boolean pagingLoading = false;

    /**
     * 执行加载更多
     */
    public void performPagingLoad() {
        if (!list.get(list.size() - 1).isLast && !pagingLoading) {
            pagingLoading = true;
            getData(false);
        }
    }

    private boolean pagingLoading2 = false;

    /**
     * 执行加载更多
     */
    public void performPagingLoad2() {
        if (!list.get(0).isFirst && !pagingLoading2) {
            pagingLoading2 = true;
            getData(true);
        }
    }

    public void getData(boolean isLast) {
        Observable.just(isLast ? 0 : list.size() - 1)
                .map(list::get)
                .map(page -> {
                    long offset = page.pageStart + (isLast ? 0 : page.pageLimit);
                    return Page.fromBook(page.book, offset, isLast);
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnComplete(() -> {
                    if (isLast) {
                        pagingLoading2 = false;
                    } else {
                        pagingLoading = false;
                    }
                })
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe(page -> {
                    int index = isLast ? 0 : list.size();
                    list.add(index, page);
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
