package me.gavin.app.explorer;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.widget.RecyclerView;
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
    private Map<String, Integer> mOffsetMap;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_explorer;
    }

    protected void afterCreate(Bundle savedInstanceState) {
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> onBackPressedSupport());

        mList = new ArrayList<>();
        mAdapter = new BindingAdapter<>(this, mList, R.layout.item_explorer);
        mAdapter.setOnItemClickListener(i -> {
            FileItem item = mList.get(i);
            if (item.isDir()) {
                putOffset(false);
                mRoot = item.getFile();
                listChild();
            } else {
                Intent intent = new Intent();
                intent.setData(Uri.fromFile(item.getFile()));
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        mBinding.recycler.setAdapter(mAdapter);

        mRoot = Environment.getExternalStorageDirectory();
        mOffsetMap = new HashMap<>();

        Observable.just(mRoot)
                .compose(FileItem.fromFile())
                .subscribe(fileItem -> {
                    mBinding.includeToolbar.toolbar.setSubtitle(fileItem.getSub());
                    listChild();
                }, Throwable::printStackTrace);
    }

    private void putOffset(boolean remove) {
        if (remove) {
            mOffsetMap.remove(mRoot.getPath());
        } else {
            RecyclerView.LayoutManager layoutManager = mBinding.recycler.getLayoutManager();
            View child = layoutManager.getChildAt(0);
            if (child != null) {
                int itemHeight = child.getHeight();
                int offset = itemHeight * layoutManager.getPosition(child) - child.getTop();
                mOffsetMap.put(mRoot.getPath(), offset);
            }
        }
    }

    @Override
    public void onBackPressedSupport() {
        if (!mRoot.equals(Environment.getExternalStorageDirectory())) {
            putOffset(true);
            mRoot = mRoot.getParentFile();
            listChild();
        } else {
            super.onBackPressedSupport();
        }
    }

    private void listChild() {
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
                    if (mOffsetMap.containsKey(mRoot.getPath())) {
                        mBinding.recycler.scrollToPosition(0);
                        mBinding.recycler.scrollBy(0, mOffsetMap.get(mRoot.getPath()));
                    }
                }, Throwable::printStackTrace);
    }
}
