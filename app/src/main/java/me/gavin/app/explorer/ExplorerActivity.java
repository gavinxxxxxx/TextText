package me.gavin.app.explorer;

import android.os.Bundle;
import android.os.Environment;

import java.io.File;

import io.reactivex.Observable;
import me.gavin.base.BindingActivity;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityExplorerBinding;

/**
 * 文件浏览
 *
 * @author gavin.xiong 2018/1/2
 */
public class ExplorerActivity extends BindingActivity<ActivityExplorerBinding> {

    private int count = 0, count2 = 0;
    private int count3 = 0, count4 = 0;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_explorer;
    }

    protected void afterCreate(Bundle savedInstanceState) {
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());

        Observable.just(Environment.getExternalStorageDirectory())
                .map(File::listFiles)
                .flatMap(Observable::fromArray)
                .filter(file -> !file.getName().startsWith("."))
                .filter(file -> file.isDirectory() || file.getName().endsWith(".txt"))
                .map(file -> {
                    if (file.isDirectory()) {
                        count++;
                    } else {
                        count2++;
                    }
                    return file;
                })
                .map(file -> {
                    FileItem fileItem = new FileItem(file);
                    if (fileItem.isDir()) {
                        count3 = 0;
                        count4 = 0;
                        Observable.fromArray(file.listFiles())
                                .filter(subFile -> !subFile.getName().startsWith("."))
                                .filter(subFile -> subFile.isDirectory() || subFile.getName().endsWith(".txt"))
                                .map(subFile -> {
                                    if (subFile.isDirectory()) {
                                        count3++;
                                    } else {
                                        count4++;
                                    }
                                    return subFile;
                                })
                                .toList()
                                .subscribe(list -> {
                                    fileItem.setSub(getString(R.string.label_explorer_sub, count3, count4));
                                });
                    } else {
                        fileItem.setSub("file");
                    }
                    return fileItem;
                })
                .sorted((o1, o2) -> o1.isDir() != o2.isDir()
                        ? (o1.isDir() ? Integer.MIN_VALUE : Integer.MAX_VALUE)
                        : o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()))
                .toList()
                .subscribe(list -> {
                    mBinding.includeToolbar.toolbar.setSubtitle(getString(R.string.label_explorer_sub, count, count2));

                    ExplorerAdapter adapter = new ExplorerAdapter(this, list);
                    mBinding.recycler.setAdapter(adapter);
                }, Throwable::printStackTrace);
    }
}
