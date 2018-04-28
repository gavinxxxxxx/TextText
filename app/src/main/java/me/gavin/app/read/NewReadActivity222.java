package me.gavin.app.read;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;

import me.gavin.app.model.Page;
import me.gavin.app.test.CoverFlipper;
import me.gavin.app.test.LocalPager;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityReadNewBinding;

public class NewReadActivity222 extends BindingActivity<ActivityReadNewBinding> {

    private String mChapter;

    private final Page[] mPages = new Page[3];

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_new;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mChapter = getIntent().getStringExtra(BundleKey.BOOK_ID);

        mBinding.drawer.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

//        new CoverFlipper().attach(mBinding.text);
//        new LocalPager().attach(mBinding.text);
//        mBinding.text.setBook();

//        mBinding.text.getFlipper().setOnPageChangeCallback(isReverse -> {
//            try {
//                if (!isReverse) {
//                    mPages[0] = mPages[1];
//                    mPages[1] = mPages[2];
//                    mPages[2] = mPages[1].next();
//                } else {
//                    mPages[2] = mPages[1];
//                    mPages[1] = mPages[0];
//                    mPages[0] = mPages[1].last();
//                }
//                mBinding.text.getFlipper().set(mPages[0], mPages[1], mPages[2]);
//
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        });
//        mBinding.text.addOnLayoutChangeListener((v, l, t, r, b, ol, ot, or, ob) -> {
//            if (r - l != or - ol || b - t != ob - ot) {
//                Config.applySizeChange(r - l, b - t);
//
//                try {
//                    mPages[1] = Page.fromChapter(mChapter, 0, false);
//                    mPages[0] = null;
//                    mPages[2] = null;
//                    mBinding.text.getFlipper().set(mPages[0], mPages[1], mPages[2]);
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//
//
//            }
//        });
    }

    @Override
    public void onBackPressedSupport() {
        if (mBinding.drawer.isDrawerOpen(Gravity.START)
                || mBinding.drawer.isDrawerOpen(Gravity.END)) {
            mBinding.drawer.closeDrawers();
        } else {
            super.onBackPressedSupport();
        }
    }
}
