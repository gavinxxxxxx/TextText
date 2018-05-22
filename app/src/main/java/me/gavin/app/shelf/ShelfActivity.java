package me.gavin.app.shelf;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.SearchView;
import android.view.Gravity;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.RxTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.app.explorer.ExplorerActivity;
import me.gavin.app.read.NewReadActivity;
import me.gavin.app.search.SearchActivity;
import me.gavin.app.test.TestActivity;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityShelfBinding;

/**
 * 书架
 *
 * @author gavin.xiong 2018/1/4
 */
public class ShelfActivity extends BindingActivity<ActivityShelfBinding> {

    private final List<Book> mList = new ArrayList<>();
    private ShelfAdapter mAdapter;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_shelf;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        mBinding.fab.setOnClickListener(v ->
                startActivityForResult(new Intent(this, ExplorerActivity.class), 0));

        mBinding.includeToolbar.toolbar.setNavigationIcon(R.drawable.vt_menu);
        mBinding.includeToolbar.toolbar.setNavigationOnClickListener(v ->
                startActivity(new Intent(this, TestActivity.class)));
        mBinding.includeToolbar.toolbar.inflateMenu(R.menu.main);
        SearchView searchView = (SearchView) mBinding
                .includeToolbar
                .toolbar
                .getMenu()
                .findItem(R.id.actionSearch)
                .getActionView();
        searchView.setQueryHint("输入点什么");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                startActivity(new Intent(ShelfActivity.this, SearchActivity.class)
                        .putExtra(BundleKey.QUERY, query));
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        getDataLayer().getShelfService().loadAllBooks()
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    mCompositeDisposable.add(disposable);
                    mList.clear();
                })
                .subscribe(books -> {
                    mList.addAll(books);
                    mAdapter = new ShelfAdapter(this, mList);
                    mAdapter.setOnItemClickListener(book ->
                            startActivity(new Intent(this, NewReadActivity.class)
                                    .putExtra(BundleKey.BOOK_ID, book.get_id())));
                    mAdapter.setOnItemLongClickListener((v, book) -> {
                        PopupMenu popupMenu = new PopupMenu(this, v);
                        popupMenu.setGravity(Gravity.CENTER);
                        popupMenu.inflate(R.menu.item_shelf);
                        popupMenu.show();
                        popupMenu.setOnMenuItemClickListener(item -> {
                            if (item.getItemId() == R.id.actionDel) {
                                getDataLayer().getShelfService().removeBook(book);
                                mAdapter.notifyItemRemoved(mList.indexOf(book));
                                mList.remove(book);
                            }
                            return true;
                        });
                    });
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
                        startActivity(new Intent(this, NewReadActivity.class)
                                .putExtra(BundleKey.BOOK_ID, id));
                    }, Throwable::printStackTrace);
        }
    }
}
