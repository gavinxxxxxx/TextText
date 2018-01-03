package me.gavin.app.explorer;

import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observable;
import me.gavin.app.RxTransformer;
import me.gavin.base.BindingActivity;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityExplorerBinding;

/**
 * 文件浏览
 *
 * @author gavin.xiong 2018/1/2
 */
public class ExplorerActivity extends BindingActivity<ActivityExplorerBinding> {

    private List<FileItem> mList = new ArrayList<>();
    private BindingAdapter mAdapter;

    private File mRoot;

    private Map<String, int[]> mMap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_explorer;
    }

    protected void afterCreate(Bundle savedInstanceState) {
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());

        mList = new ArrayList<>();
        mAdapter = new BindingAdapter<>(this, mList, R.layout.item_explorer);
        mAdapter.setOnItemClickListener(i -> {
            FileItem item = mList.get(i);
            if (item.isDir()) {
                putP();
                mRoot = item.getFile();
                listFiles();
            } else {
                Snackbar.make(mBinding.recycler, item.getFile().getPath(), Snackbar.LENGTH_LONG).show();
            }
        });
        mBinding.recycler.setAdapter(mAdapter);

        mRoot = Environment.getExternalStorageDirectory();
        mMap = new HashMap<>();

        Observable.just(mRoot)
                .compose(FileItem.fromFile())
                .subscribe(fileItem -> {
                    mBinding.includeToolbar.toolbar.setSubtitle(fileItem.getSub());
                    listFiles();
                }, Throwable::printStackTrace);
    }

    private void putP() {
        LinearLayoutManager layoutManager = (LinearLayoutManager) mBinding.recycler.getLayoutManager();
        int position = layoutManager.findFirstVisibleItemPosition();
        View child = layoutManager.findViewByPosition(position);
        if (child != null) {
            int offset = child.getTop();
            mMap.put(mRoot.getPath(), new int[]{position, offset});
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (!mRoot.equals(Environment.getExternalStorageDirectory())) {
            putP();
            mRoot = mRoot.getParentFile();
            listFiles();
        } else {
            super.onBackPressedSupport();
        }
    }

    private void listFiles() {
        Observable.fromArray(mRoot.listFiles())
                .compose(RxTransformer.fileFilter())
                .compose(FileItem.fromFile())
                .toSortedList((o1, o2) -> o1.isDir() != o2.isDir()
                        ? (o1.isDir() ? Integer.MIN_VALUE : Integer.MAX_VALUE)
                        : o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()))
                .subscribe(list -> {
                    mList.clear();
                    mList.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    if (mMap.containsKey(mRoot.getPath())) {
                        mBinding.recycler.post(() -> {
                            mBinding.recycler.scrollToPosition(mMap.get(mRoot.getPath())[0]);
//                        mBinding.recycler.scrollBy(0, mMap.get(mRoot.getPath())[1]);
                        });
                    }
                }, Throwable::printStackTrace);
    }
}
