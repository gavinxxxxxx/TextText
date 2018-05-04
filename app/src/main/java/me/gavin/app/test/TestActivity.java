package me.gavin.app.test;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.gavin.app.core.model.Page;
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
        set();
    }

    private final Page[] mPages = new Page[3];

    private void set() {

    }
}
