package me.gavin.app.test;

import android.os.Bundle;
import android.support.annotation.Nullable;

import me.gavin.app.model.Page;
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
//        getDataLayer().getShelfService().loadAllBooks()
//                .flatMap(Observable::fromIterable)
//                .take(1)
//                .compose(RxTransformer.applySchedulers())
//                .doOnSubscribe(mCompositeDisposable::add)
//                .subscribe(book -> {
//                    Config.applySizeChange(mBinding.text.getWidth(), mBinding.text.getHeight());
//                    mPages[1] = Page.fromBook(book, book.getOffset(), false);
//                    mPages[0] = mPages[1].last();
//                    mPages[2] = mPages[1].next();
//                    Flipper flipper = new CoverFlipper();
//                    flipper.set(mPages[0], mPages[1], mPages[2]);
//                    flipper.attach(mBinding.text);
//                    flipper.setOnPageChangeCallback(isReverse -> {
//                        L.e("OnPageChange - " + isReverse);
//                        try {
//                            if (!isReverse) {
//                                mPages[0] = mPages[1];
//                                mPages[1] = mPages[2];
//                                mPages[2] = mPages[1].next();
//                            } else {
//                                mPages[2] = mPages[1];
//                                mPages[1] = mPages[0];
//                                mPages[0] = mPages[1].last();
//                            }
//                            flipper.set(mPages[0], mPages[1], mPages[2]);
//                        } catch (IOException e) {
//                            e.printStackTrace();
//                        }
//                    });
//                }, L::e);
    }
}
