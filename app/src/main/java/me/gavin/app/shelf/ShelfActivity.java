package me.gavin.app.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
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

    private List<Book> mList;
    private BindingAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shelf;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mList = new ArrayList<>();
        Observable.just("/storage/emulated/0/gavin/book/")
                .map(File::new)
                .map(File::listFiles)
                .flatMap(Observable::fromArray)
                .filter(file -> !file.getName().contains("从零开始"))
                .map(File::getPath)
                .map(Book::fromSDCard)
                .toList()
                .subscribe(books -> {
                    mList.addAll(books);
                    mAdapter = new BindingAdapter<>(this, mList, R.layout.item_shelf_book);
                    mAdapter.setOnItemClickListener(i ->
                            startActivity(new Intent(this, ReadActivity.class)
                                    .setData(mList.get(i).getUri())));
                    mBinding.recycler.setAdapter(mAdapter);
                });
    }
}
