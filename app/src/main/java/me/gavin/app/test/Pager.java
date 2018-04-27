package me.gavin.app.test;

import me.gavin.app.Config;
import me.gavin.app.model.Book;
import me.gavin.app.model.Page;

/**
 * 分页器
 *
 * @author gavin.xiong 2018/4/27
 */
public abstract class Pager {

    Flipper mFlipper;

    Book mBook;

    final Page[] mPages = new Page[Config.pageCount];

    public Pager(Book book) {
        this.mBook = book;
    }

    public abstract void offset(Long offset);

    public abstract void lastPage();

    public abstract void nextPage();
}
