package me.gavin.app.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;

import java.util.ArrayList;
import java.util.List;

import me.gavin.app.core.source.Source;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivitySourceBinding;
import me.gavin.util.L;

/**
 * 书源 - 新增 & 编辑
 *
 * @author gavin.xiong 2018/4/26.
 */
public class SourceActivity extends BindingActivity<ActivitySourceBinding> {

    private Source mSource;

    private final List<Source.Field> queryFields = new ArrayList<>();
    private final List<Source.Field> detailFields = new ArrayList<>();

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
            L.e(ApplicationComponent.Instance.get().getGson().toJson(mSource));
            return true;
        });

        queryFields.add(new Source.Field());
        detailFields.add(new Source.Field());

        mBinding.rvQuery.setAdapter(new SourceFieldAdapter(this, queryFields));
        mBinding.rvQuery.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });

        mBinding.rvDetail.setAdapter(new SourceFieldAdapter(this, detailFields));
        mBinding.rvDetail.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }
}
