package me.gavin.app.test;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import me.gavin.app.Config;
import me.gavin.app.RxTransformer;
import me.gavin.app.model.Page;
import me.gavin.base.BindingActivity;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityTestBinding;
import me.gavin.util.L;

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

    private void set() {
        getDataLayer().getShelfService().loadAllBooks()
                .flatMap(Observable::fromIterable)
                .take(1)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(book -> {
                    Config.applySizeChange(mBinding.text.getWidth(), mBinding.text.getHeight());
                    Page curr = Page.fromBook(book, book.getOffset(), false);
                    Page last = Page.fromBook(book, curr.pageStart, true);
                    Page next = Page.fromBook(book, curr.pageEnd, false);
                    Flipper flipper = new CoverFlipper();
                    flipper.set(last, curr, next);
                    flipper.attach(mBinding.text);
                }, L::e);
    }
}
