package me.gavin.app.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.RxTransformer;
import me.gavin.app.explorer.ExplorerActivity;
import me.gavin.app.model.Book;
import me.gavin.app.read.ReadActivity;
import me.gavin.base.BindingActivity;
import me.gavin.base.recycler.BindingAdapter;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityShelfBinding;

/**
 * 书架
 *
 * @author gavin.xiong 2018/1/4
 */
public class ShelfActivity extends BindingActivity<ActivityShelfBinding> {

    private final List<Book> mList = new ArrayList<>();
    private BindingAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shelf;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mBinding.fab.setOnClickListener(v ->
                startActivityForResult(new Intent(this, ExplorerActivity.class), 0));

        getDataLayer().getShelfService().loadAllBooks()
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    mList.clear();
                })
                .subscribe(books -> {
                    mList.addAll(books);
                    mAdapter = new BindingAdapter<>(this, mList, R.layout.item_shelf_book);
                    mAdapter.setOnItemClickListener(i ->
                            startActivity(new Intent(this, ReadActivity.class)
                                    .putExtra("bookId", mList.get(i).get_Id())));
                    mBinding.recycler.setAdapter(mAdapter);
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null && data.getData() != null) {
            Observable.just(data.getData())
                    .map(Book::fromUri)
                    .flatMap(book -> getDataLayer().getShelfService().insertBook(book))
                    .subscribe(id -> {
                        startActivity(new Intent(this, ReadActivity.class)
                                .putExtra("bookId", id));
                    }, Throwable::printStackTrace);
        }
    }
}
