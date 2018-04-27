package me.gavin.app.search;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import me.gavin.app.RxTransformer;
import me.gavin.app.model.Book;
import me.gavin.app.model.Chapter;
import me.gavin.app.model.Page;
import me.gavin.app.model.Word;
import me.gavin.app.read.NewReadActivity222;
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
        mBook = (Book) getIntent().getSerializableExtra(BundleKey.BOOK);
        detail(mBook.getId());
        directory(mBook.getId());
    }

    private void detail(String id) {
        getDataLayer().getSourceService()
                .detail(id)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(L::e, L::e);
    }

    private void directory(String id) {
        getDataLayer().getSourceService()
                .directory(id)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(chapters -> {
                    BindingAdapter<Chapter> adapter = new BindingAdapter<>(
                            this, chapters, R.layout.item_chapter);
                    adapter.setOnItemClickListener(i ->
                            chapter(mBook.getId(), chapters.get(i).id));
                    mBinding.recycler.setAdapter(adapter);
                }, L::e);
    }

    public void chapter(String id, String cid) {
        getDataLayer().getSourceService()
                .chapter(id, cid)
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(s -> {
                    L.e(s);
//                    Page page = Page.fromChapter(s, 0, false);
//                    L.e(page);
//                    L.e(page.mText);
//                    for (Word word : page.wordList) {
//                        L.e(word);
//                    }
                    startActivity(new Intent(this, NewReadActivity222.class)
                            .putExtra(BundleKey.BOOK_ID, s));
                }, L::e);
    }
}