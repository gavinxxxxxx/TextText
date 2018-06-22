package me.gavin.app.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.gavin.app.core.source.Source;
import me.gavin.base.BindingActivity;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.R;
import me.gavin.databinding.ActivitySourceListBinding;

/**
 * 书源管理
 *
 * @author gavin.xiong 2018/5/16
 */
public class SourceListActivity extends BindingActivity<ActivitySourceListBinding> {

    private final List<Source> mList = new ArrayList<>();
    private BindingAdapter<Source> mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_source_list;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mBinding.includeToolbar.toolbar.setTitle("书源管理");
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());
        mBinding.includeToolbar.toolbar.inflateMenu(R.menu.activity_source);
        mBinding.includeToolbar.toolbar.setOnMenuItemClickListener(item -> {
            startActivity(new Intent(this, SourceActivity.class));
            return true;
        });

        mAdapter = new BindingAdapter<>(this, mList, R.layout.item_source);
        mBinding.recycler.setAdapter(mAdapter);

        sources();
    }

    @Override
    protected void onPause() {
        super.onPause();
        ApplicationComponent
                .Instance
                .get()
                .getDaoSession()
                .getSourceDao()
                .updateInTx(mList);
    }

    private void sources() {
        List<Source> list = ApplicationComponent
                .Instance
                .get()
                .getDaoSession()
                .getSourceDao()
                .loadAll();
        mList.clear();
        mList.addAll(list);
        mAdapter.notifyDataSetChanged();
    }
}
