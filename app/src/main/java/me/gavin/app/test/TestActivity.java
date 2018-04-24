package me.gavin.app.test;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

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

//        String sss = "零漪而叁斯舞留期捌酒";
        String sss = "0123456789";

        List<IText> list = new LinkedList<>();
        for (int i = 0; i < 10; i++) {
            int count = (int) (Math.random() * 60) + 1;
            String s = "";
            for (int j = 0; j < count; j++) {
                s += sss.charAt(j % 10);
            }
            list.add(new IText(0, s));
        }
//        mBinding.iText.append(list);
    }

    private final Page[] mPages = new Page[3];

    private void set() {
        getDataLayer().getShelfService().loadAllBooks()
                .flatMap(Observable::fromIterable)
                .take(1)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(book -> {
                    Config.applySizeChange(mBinding.text.getWidth(), mBinding.text.getHeight());
                    mPages[1] = Page.fromBook(book, book.getOffset(), false);
                    mPages[0] = Page.fromBook(book, mPages[1].pageStart, true);
                    mPages[2] = Page.fromBook(book, mPages[1].pageEnd, false);
                    Flipper flipper = new CoverFlipper();
                    flipper.set(mPages[0], mPages[1], mPages[2]);
                    flipper.attach(mBinding.text);
                    flipper.setOnPageChangeCallback(isReverse -> {
                        L.e("OnPageChange - " + isReverse);
                        try {
                            if (!isReverse) {
                                mPages[0] = mPages[1];
                                mPages[1] = mPages[2];
                                mPages[2] = mPages[1].isLast ? null : Page.fromBook(book, mPages[1].pageEnd, false);
                            } else {
                                mPages[2] = mPages[1];
                                mPages[1] = mPages[0];
                                mPages[0] = mPages[1].isFirst ? null : Page.fromBook(book, mPages[1].pageStart, true);
                            }
                            flipper.set(mPages[0], mPages[1], mPages[2]);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                }, L::e);
    }
}
