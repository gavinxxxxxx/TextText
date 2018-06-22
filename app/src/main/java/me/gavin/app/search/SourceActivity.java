package me.gavin.app.search;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import me.gavin.app.core.source.Source;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.R;
import me.gavin.databinding.ActivitySourceBinding;
import me.gavin.util.L;

/**
 * 书源 - 新增 & 编辑
 *
 * @author gavin.xiong 2018/4/26.
 */
public class SourceActivity extends BindingActivity<ActivitySourceBinding> {

    private Source mSource;
    private boolean mIsEdit;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_source;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mSource = (Source) getIntent().getSerializableExtra(BundleKey.SOURCE);
        mIsEdit = mSource != null;

        mBinding.includeToolbar.toolbar.setTitle(mIsEdit ? "编辑书源" : "新增书源");
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());
        mBinding.includeToolbar.toolbar.inflateMenu(R.menu.action_done);
        mBinding.includeToolbar.toolbar.setOnMenuItemClickListener(item -> {
            done();
            return true;
        });

        checkData();
        mSource.data.query.fields.add(new Source.Field());
        mBinding.rvQuery.setAdapter(new SourceFieldAdapter(this, mSource.data.query.fields));
        mBinding.rvQuery.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
        mSource.data.detail.fields.add(new Source.Field());
        mBinding.rvDetail.setAdapter(new SourceFieldAdapter(this, mSource.data.detail.fields));
        mBinding.rvDetail.setLayoutManager(new LinearLayoutManager(this) {
            @Override
            public boolean canScrollVertically() {
                return false;
            }
        });
    }

    private void checkData() {
        if (mSource == null) mSource = new Source();
        if (mSource.data() == null) mSource.data = new Source.Data();
        if (mSource.data.query == null) mSource.data.query = new Source.Action();
        if (mSource.data.query.fields == null) mSource.data.query.fields = new ArrayList<>();
        if (mSource.data.detail == null) mSource.data.detail = new Source.Action();
        if (mSource.data.detail.fields == null) mSource.data.detail.fields = new ArrayList<>();
        if (mSource.data.catalog == null) mSource.data.catalog = new Source.Action();
        if (mSource.data.catalog.fields == null) mSource.data.catalog.fields = new ArrayList<>();
        if (mSource.data.chapter == null) mSource.data.chapter = new Source.Action();
        mBinding.setItem(mSource);
    }

    private void done() {
        removeEmpty(mSource.data.query.fields);
        removeEmpty(mSource.data.detail.fields);
        removeEmpty(mSource.data.catalog.fields);
        L.e(ApplicationComponent.Instance.get().getGson().toJson(mSource));
    }

    private void removeEmpty(List<Source.Field> fields) {
        List<Source.Field> temp = new LinkedList<>();
        for (Source.Field t : fields) {
            if (TextUtils.isEmpty(t.type)
                    && TextUtils.isEmpty(t.select)
                    && TextUtils.isEmpty(t.attr)
                    && TextUtils.isEmpty(t.feature))
                temp.add(t);
        }
        fields.removeAll(temp);
    }
}
