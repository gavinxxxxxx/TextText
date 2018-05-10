package me.gavin.app.search;

import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.List;

import me.gavin.app.RxTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.core.source.Source;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityDetailBinding;
import me.gavin.util.L;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/26.
 */
public class DetailActivity extends BindingActivity<ActivityDetailBinding> {

    private Book mBook;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_detail;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        long bookId = getIntent().getLongExtra(BundleKey.BOOK_ID, 0);
        mBook = getDataLayer().getShelfService().loadBook(bookId);
        detail(mBook.getId());
        directory(mBook.getId());
    }

    private void detail(String id) {
//        getDataLayer().getSourceService()
//                .detail(Source.getSource(mBook.getSrc()), id)
//                .compose(RxTransformer.applySchedulers())
//                .doOnSubscribe(mCompositeDisposable::add)
//                .subscribe(L::e, L::e);
    }

    private List<Chapter> chapterList;

    private void directory(String id) {
        getDataLayer().getSourceService()
                .directory(mBook)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(chapters -> {
                    chapterList = chapters;
                    BindingAdapter<Chapter> adapter = new BindingAdapter<>(
                            this, chapters, R.layout.item_chapter);
                    adapter.setOnItemClickListener(this::chapter);
                    mBinding.recycler.setAdapter(adapter);
                }, L::e);
    }

    private boolean flag = false;

    public void chapter(int i) {
//        if (!flag) {
//            getDataLayer().getSourceService()
//                    .getFromNet(mBook.getId(), chapterList.get(i).getId())
//                    .compose(RxTransformer.applySchedulers())
//                    .doOnSubscribe(mCompositeDisposable::add)
//                    .subscribe(s -> {
//                        flag = true;
//                        L.e(s);
//                    }, L::e);
//        } else {
//            getDataLayer().getSourceService()
//                    .getFromCache(mBook, i)
//                    .compose(RxTransformer.applySchedulers())
//                    .doOnSubscribe(mCompositeDisposable::add)
//                    .subscribe(s -> {
//                        flag = false;
//                        L.e(s);
//                    }, L::e);
//        }

        mBook.setIndex(i);
        getDataLayer().getSourceService()
                .chapter(Source.getSource(mBook.getSrc()), mBook, mBook.getIndex())
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(s -> {
                    L.e(s);
//                    mBook.setText(s);
//                    mBook.setOffset(0);
//                    getDataLayer().getShelfService().updateBook(mBook);
//                    startActivity(new Intent(this, NewReadActivity.class)
//                            .putExtra(BundleKey.BOOK_ID, mBook.get_id()));
                }, L::e);
    }
}
