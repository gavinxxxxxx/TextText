package me.gavin.app.explorer;

import android.os.Bundle;
import android.os.Environment;

import java.io.File;

import io.reactivex.Observable;
import me.gavin.app.RxTransformer;
import me.gavin.base.BindingActivity;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityExplorerBinding;

/**
 * 文件浏览
 *
 * @author gavin.xiong 2018/1/2
 */
public class ExplorerActivity extends BindingActivity<ActivityExplorerBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_explorer;
    }

    protected void afterCreate(Bundle savedInstanceState) {
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());

        Observable.just(Environment.getExternalStorageDirectory())
                .compose(FileItem.fromFile())
                .subscribe(fileItem -> {
                    mBinding.includeToolbar.toolbar.setSubtitle(fileItem.getSub());
                });

        Observable.just(Environment.getExternalStorageDirectory())
                .map(File::listFiles)
                .flatMap(Observable::fromArray)
                .compose(RxTransformer.fileFilter())
                .compose(FileItem.fromFile())
                .toSortedList((o1, o2) -> o1.isDir() != o2.isDir()
                        ? (o1.isDir() ? Integer.MIN_VALUE : Integer.MAX_VALUE)
                        : o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()))
                .subscribe(list -> {
                    ExplorerAdapter adapter = new ExplorerAdapter(this, list);
                    mBinding.recycler.setAdapter(adapter);
                }, Throwable::printStackTrace);
    }
}
