package me.gavin.db.service;

import me.gavin.app.model.Book;
import me.gavin.db.dao.BookDao;

/**
 * Book
 *
 * @author gavin.xiong 2017/12/19
 */
public class BookService extends BaseService<Book, Long> {

    public BookService(BookDao dao) {
        super(dao);
    }
}
