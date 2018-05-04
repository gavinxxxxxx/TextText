package me.gavin.app.read;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import me.gavin.app.core.model.Chapter;
import me.gavin.base.BindingFragment;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.FragmentChapterBinding;
import me.gavin.util.L;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/1/6
 */
public class ChapterFragment extends BindingFragment<FragmentChapterBinding> {

    private final List<Chapter> mList = new ArrayList<>();
    private BindingAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_chapter;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mAdapter = new BindingAdapter<>(getActivity(), mList, R.layout.item_chapter);
        mBinding.recycler.setAdapter(mAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();
        L.e("onResume");
    }

    @Override
    public void onSupportVisible() {
        super.onSupportVisible();
        L.e("onSupportVisible");
    }

    @Override
    public void onSupportInvisible() {
        super.onSupportInvisible();
        L.e("onSupportInvisible");
    }
}
