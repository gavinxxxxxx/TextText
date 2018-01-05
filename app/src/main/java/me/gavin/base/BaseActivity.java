package me.gavin.base;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import javax.inject.Inject;

import io.reactivex.disposables.CompositeDisposable;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.service.base.DataLayer;
import me.yokeyword.fragmentation.SupportActivity;
import me.yokeyword.fragmentation.anim.DefaultVerticalAnimator;
import me.yokeyword.fragmentation.anim.FragmentAnimator;

/**
 * BaseActivity
 *
 * @author gavin.xiong 2016/12/30  2016/12/30
 */
public abstract class BaseActivity extends SupportActivity {

    @Inject
    protected CompositeDisposable mCompositeDisposable;
    @Inject
    protected DataLayer mDataLayer;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 状态栏深色图标
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        }
        setContentView();
        ApplicationComponent.Instance.get().inject(this);
        afterCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mCompositeDisposable.dispose();
    }

    @Override
    public FragmentAnimator onCreateFragmentAnimator() {
        return new DefaultVerticalAnimator();
    }

    public abstract void setContentView();

    protected abstract void afterCreate(@Nullable Bundle savedInstanceState);

    protected DataLayer getDataLayer() {
        return mDataLayer;
    }

}
