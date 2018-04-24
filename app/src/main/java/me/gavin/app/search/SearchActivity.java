package me.gavin.app.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.gavin.app.RxTransformer;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivitySearchBinding;
import me.gavin.util.L;

/**
 * 搜索
 *
 * @author gavin.xiong 2018/4/24.
 */
public class SearchActivity extends BindingActivity<ActivitySearchBinding> {

    @Override
    protected int getLayoutId() {
        return R.layout.activity_search;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        String query = getIntent().getStringExtra(BundleKey.QUERY);
        mBinding.includeToolbar.toolbar.setTitle(query);
        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24dp);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v -> finish());

        doSearch(query);
    }

    private void doSearch(String query) {
        getDataLayer().getSearchService()
                .search(query)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(L::e, L::e);
    }
}
