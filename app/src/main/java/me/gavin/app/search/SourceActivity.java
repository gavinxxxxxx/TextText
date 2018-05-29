package me.gavin.app.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.gavin.app.core.source.Source;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivitySourceBinding;

/**
 * 书源 - 新增 & 编辑
 *
 * @author gavin.xiong 2018/4/26.
 */
public class SourceActivity extends BindingActivity<ActivitySourceBinding> {

    private Source mSource;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_source;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mSource = (Source) getIntent().getSerializableExtra(BundleKey.SOURCE);

        mBinding.includeToolbar.toolbar.setTitle(mSource == null ? "新增书源" : "编辑书源");
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());
        mBinding.includeToolbar.toolbar.inflateMenu(R.menu.action_done);
        mBinding.includeToolbar.toolbar.setOnMenuItemClickListener(item -> {

            return true;
        });
    }
}
