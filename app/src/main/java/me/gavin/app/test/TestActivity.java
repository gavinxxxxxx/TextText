package me.gavin.app.test;

import android.graphics.drawable.Animatable;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.gavin.base.BindingActivity;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityTestBinding;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/22.
 */
public class TestActivity extends BindingActivity<ActivityTestBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_test;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mBinding.imageView.setElevation(10);
        mBinding.imageView.setImageDrawable(getDrawable(R.drawable.anim_loading));
        mBinding.imageView.setOnClickListener(v -> {
            if (mBinding.imageView.getDrawable() instanceof Animatable) {
                if (((Animatable) mBinding.imageView.getDrawable()).isRunning()) {
                    ((Animatable) mBinding.imageView.getDrawable()).stop();
                } else {
                    ((Animatable) mBinding.imageView.getDrawable()).start();
                }
            }
        });
    }
}
