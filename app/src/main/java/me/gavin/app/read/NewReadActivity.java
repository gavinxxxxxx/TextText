package me.gavin.app.read;

import android.os.Bundle;
import android.support.annotation.Nullable;

import io.reactivex.Observable;
import me.gavin.app.RxTransformer;
import me.gavin.app.Utils;
import me.gavin.app.model.Book;
import me.gavin.app.model.Page;
import me.gavin.app.test.CoverFlipper;
import me.gavin.app.test.Pager2;
import me.gavin.base.BindingActivity;
import me.gavin.base.BundleKey;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityReadNewBinding;
import me.gavin.util.L;

public class NewReadActivity extends BindingActivity<ActivityReadNewBinding> implements Pager2 {

    private Book mBook;

    @Override
    protected int getLayoutId() {
        return R.layout.activity_read_new;
    }

    @Override
    protected void afterCreate(@Nullable Bundle savedInstanceState) {
        long bookId = getIntent().getLongExtra(BundleKey.BOOK_ID, -1);
        mBook = getDataLayer().getShelfService().loadBook(bookId);
        if (mBook == null) {
            return;
        }

        mBinding.text.setPager(this);
        mBinding.text.setFlipper(new CoverFlipper());
    }

    @Override
    public void onFilped(Page page) {
        mBook.setIndex(page.index);
        mBook.setOffset(page.start);
        getDataLayer().getShelfService().updateBook(mBook);
    }

    @Override
    public void curr() {
//        Observable.just(mBook.getOffset())
//                .map(offset -> {
//                    Page page = new Page();
//                    page.book = mBook;
//                    return Utils.nextLocal(page, mBook.getOffset());
//                })
//                .compose(RxTransformer.applySchedulers())
//                .doOnSubscribe(mCompositeDisposable::add)
//                .subscribe(page -> {
//                    mBinding.text.add(page, true);
//                }, L::e);

        getDataLayer().getSourceService().chapter(mBook, mBook.getIndex())
                .map(s -> {
                    Page page = new Page();
                    page.book = mBook;
                    page.index = mBook.getIndex();
                    page.chapter = s;
                    return Utils.nextOnline(page, mBook.getOffset());
                })
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(mCompositeDisposable::add)
                .subscribe(page -> {
                    mBinding.text.add(page, true);
                }, L::e);
    }

    @Override
    public void last() {
        Page page = new Page();
        page.book = mBook;
//        Observable.just(mBinding.text.header().start)
//                .filter(offset -> !lasting)
//                .map(offset -> Utils.lastLocal(page, offset))
//                .compose(RxTransformer.applySchedulers())
//                .doOnSubscribe(disposable -> {
//                    lasting = true;
//                    mCompositeDisposable.add(disposable);
//                    mBinding.text.add(page, true);
//                })
//                .doOnComplete(() -> lasting = false)
//                .doOnError(throwable -> lasting = false)
//                .subscribe(mBinding.text::update, L::e);

        Observable.just(mBinding.text.header())
                .flatMap(header -> {
                    if (header.start > 0) {
                        page.chapter = header.chapter;
                        page.index = header.index;
                        page.end = header.start;
                        return Observable.just(page);
                    } else {
                        return getDataLayer().getSourceService()
                                .chapter(mBook, header.index - 1)
                                .map(s -> {
                                    page.chapter = s;
                                    page.index = header.index - 1;
                                    page.end = s.length();
                                    return page;
                                });
                    }
                })
                .map(page1 -> Utils.lastOnline(page, page.end))
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    lasting = true;
                    mCompositeDisposable.add(disposable);
                    mBinding.text.add(page, true);
                })
                .doOnComplete(() -> lasting = false)
                .doOnError(throwable -> lasting = false)
                .subscribe(mBinding.text::update, Throwable::printStackTrace);

//        page.chapter = mBinding.text.header().chapter;
//        Observable.just(mBinding.text.header().start)
////                .filter(offset -> !lasting)
//                .map(offset -> Utils.lastOnline(page, offset))
//                .compose(RxTransformer.applySchedulers())
//                .doOnSubscribe(disposable -> {
//                    lasting = true;
//                    mCompositeDisposable.add(disposable);
//                    mBinding.text.add(page, true);
//                })
//                .doOnComplete(() -> lasting = false)
//                .doOnError(throwable -> lasting = false)
//                .subscribe(mBinding.text::update, L::e);

    }

    @Override
    public void next() {
        Page page = new Page();
        page.book = mBook;
//        Observable.just(mBinding.text.footer().end)
//                .filter(offset -> !nexting)
//                .map(offset -> Utils.nextLocal(page, offset))
//                .compose(RxTransformer.applySchedulers())
//                .doOnSubscribe(disposable -> {
//                    mCompositeDisposable.add(disposable);
//                    mBinding.text.add(page, false);
//                })
//                .doOnComplete(() -> nexting = false)
//                .doOnError(throwable -> nexting = false)
//                .subscribe(mBinding.text::update, L::e);

        Observable.just(mBinding.text.footer())
                .flatMap(footer -> {
                    if (footer.end < footer.chapter.length()) {
                        page.chapter = footer.chapter;
                        page.index = footer.index;
                        page.start = footer.end;
                        return Observable.just(page);
                    } else {
                        return getDataLayer().getSourceService()
                                .chapter(mBook, footer.index + 1)
                                .map(s -> {
                                    page.chapter = s;
                                    page.index = footer.index + 1;
                                    page.start = 0;
                                    return page;
                                });
                    }
                })
                .map(page1 -> Utils.nextOnline(page1, page.start))
                .compose(RxTransformer.applySchedulers())
                .doOnSubscribe(disposable -> {
                    nexting = true;
                    mCompositeDisposable.add(disposable);
                    mBinding.text.add(page, false);
                })
                .doOnComplete(() -> nexting = false)
                .doOnError(throwable -> nexting = false)
                .subscribe(mBinding.text::update, L::e);

//        page.chapter = mBinding.text.footer().chapter;
//        Observable.just(mBinding.text.footer().end)
////                .filter(offset -> !nexting)
//                .map(offset -> Utils.nextOnline(page, offset))
//                .compose(RxTransformer.applySchedulers())
//                .doOnSubscribe(disposable -> {
//                    nexting = true;
//                    mCompositeDisposable.add(disposable);
//                    mBinding.text.add(page, false);
//                })
//                .doOnComplete(() -> nexting = false)
//                .doOnError(throwable -> nexting = false)
//                .subscribe(mBinding.text::update, L::e);
    }

    private boolean lasting, nexting;

}
