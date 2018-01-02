package me.gavin.app.explorer;

import android.databinding.DataBindingUtil;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import java.io.File;

import io.reactivex.Observable;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityExplorerBinding;

/**
 * 文件浏览
 *
 * @author gavin.xiong 2018/1/2
 */
public class ExplorerActivity extends AppCompatActivity {

    private ActivityExplorerBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏深色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_explorer);

        afterCreate(savedInstanceState);
    }

    private void afterCreate(Bundle savedInstanceState) {
        Observable.just(Environment.getExternalStorageDirectory())
                .map(File::listFiles)
                .flatMap(Observable::fromArray)
                .filter(file -> !file.getName().startsWith("."))
                .map(FileItem::new)
                .sorted((o1, o2) -> o1.isDir() != o2.isDir()
                        ? (o1.isDir() ? Integer.MIN_VALUE : Integer.MAX_VALUE)
                        : o1.getName().toLowerCase().compareTo(o2.getName().toLowerCase()))
                .toList()
                .subscribe(list -> {
                    ExplorerAdapter adapter = new ExplorerAdapter(this, list);
                    mBinding.recycler.setAdapter(adapter);
                }, Throwable::printStackTrace);
    }
}
