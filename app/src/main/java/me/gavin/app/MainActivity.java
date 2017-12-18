package me.gavin.app;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityMainBinding;
import me.gavin.text.databinding.ItemTextBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    private List<ViewModel> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏深色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        list.add(new ViewModel(50008, false));
        mBinding.recycler.setAdapter(new Adapter(this, list));
//        SnapHelper snapHelper = new PagerSnapHelper();
//        snapHelper.attachToRecyclerView(mBinding.recycler);

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

    private boolean pagingHaveMore = true;
    private boolean pagingLoading = false;

    /**
     * 执行加载更多
     */
    public void performPagingLoad() {
        if (pagingHaveMore && !pagingLoading) {
            pagingLoading = true;
            getData(false);
        }
    }

    private boolean pagingHaveMore2 = true;
    private boolean pagingLoading2 = false;

    /**
     * 执行加载更多
     */
    public void performPagingLoad2() {
        if (pagingHaveMore2 && !pagingLoading2) {
            pagingLoading2 = true;
            getData(true);
        }
    }

    public void getData(boolean isLast) {
        mBinding.recycler.post(() -> { // TODO: 2017/12/18
            ViewModel cur = list.get(isLast ? 0 : list.size() - 1);
            int index = isLast ? 0 : list.size();
            list.add(index, new ViewModel(cur.pageOffset + (isLast ? 0 : cur.pageLimit), isLast));
            mBinding.recycler.getAdapter().notifyItemInserted(index);
            if (isLast) {
                pagingLoading2 = false;
            } else {
                pagingLoading = false;
            }
        });
    }

    public static class Adapter extends RecyclerAdapter<ViewModel, ItemTextBinding> {

        Adapter(Context context, List<ViewModel> list) {
            super(context, list, R.layout.item_text);
        }

        @Override
        protected void onBind(RecyclerHolder<ItemTextBinding> holder, ViewModel t, int position) {
            holder.binding.text.set(t);
        }
    }
}
