package me.gavin.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

import io.reactivex.Observable;
import io.reactivex.schedulers.Schedulers;
import me.gavin.app.Config;
import me.gavin.app.RxTransformer;
import me.gavin.app.StreamHelper;
import me.gavin.app.Utils;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.source.Source;
import me.gavin.app.core.source.SourceServicess;
import me.gavin.db.dao.SourceDao;
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;

/**
 * SourceManager
 *
 * @author gavin.xiong 2017/4/28
 */
public class SourceManager extends BaseManager implements DataLayer.SourceService {

    @Override
    public Observable<List<Book>> search(String query) {
        return Observable.just(Source.FLAG_CHECKED)
                .map(flag -> " WHERE " + SourceDao.Properties.Flag.columnName + " & " + flag + " = " + flag)
                .map(getDaoSession().getSourceDao()::queryRaw)
                .flatMap(Observable::fromIterable)
                .map(src -> SourceServicess.getSource(src.id))
                .toList()
                .toObservable()
                .flatMap(srcss -> {
                    List<Observable<List<Book>>> observables = new ArrayList<>();
                    for (SourceServicess source : srcss) {
                        observables.add(search(source, query)
                                .subscribeOn(Schedulers.io()));
                    }
                    return Observable.merge(observables);
                });
    }

    @Override
    public Observable<List<Book>> search(SourceServicess source, String query) {
        return getApi().get(source.queryUrl(query), Config.cacheControlQuery)
                .map(ResponseBody::bytes)
                .map(this::read)
                .map(Jsoup::parse)
                .map(document -> document.select(source.querySelector()))
                .flatMap(Observable::fromIterable)
                .compose(source.queryFilter())
                .compose(RxTransformer.log())
                .map(source::query2Book)
                .toList()
                .toObservable()
                .filter(books -> !books.isEmpty());
    }

    @Override
    public Observable<Book> detail(Book book) {
        SourceServicess source = SourceServicess.getSource(book.src);
        return Observable.just(source.detailsUrl(book.id))
                .flatMap(url -> getApi().get(url, Config.cacheControlDetail))
                .map(ResponseBody::bytes)
                .map(this::read)
                .map(Jsoup::parse)
                .compose(RxTransformer.log())
                .map(doc -> source.bookInfo(book, doc))
                .compose(source.detailChapters())
                .toList()
                .toObservable()
                .map(chapters -> {
                    book.chapters.clear();
                    book.chapters.addAll(chapters);
                    return book;
                });
    }

    @Override
    public Observable<List<Chapter>> directory(Book book) {
        SourceServicess source = SourceServicess.getSource(book.getSrc());
        return getApi().get(source.directoryUrl(book.id), Config.cacheControlDirectory)
                .map(ResponseBody::bytes)
                .map(this::read)
                .map(Jsoup::parse)
                .map(document -> document.select(source.directorySelector()))
                .compose(RxTransformer.log())
                .compose(source.directoryFilter())
                .map(element -> source.directory2Chapter(element, book.id))
                .toList()
                .toObservable()
                .compose(RxTransformer.log())
                .map(chapters -> {
                    for (int i = 0; i < chapters.size(); i++) {
                        chapters.get(i).bookId = book.id;
                        chapters.get(i).index = i;
                    }
                    book.count = chapters.size();
                    getDaoSession().getBookDao().update(book);
                    return chapters;
                });
    }

    @Override
    public Observable<String> chapter(SourceServicess source, Book book, int index) {
        return directory(book)
                .map(chapters -> chapters.get(index))
                .flatMap(chapter -> getApi().get(source.chapterUrl(chapter), Config.cacheControlChapter))
                .map(ResponseBody::bytes)
                .map(this::read)
                .compose(source.chapter2Text())
                .map(Element::text)
                .map(s -> s.replaceAll("\\\\n", "\n"))
                .compose(RxTransformer.log());
    }

    @Override
    public Observable<Page> curr(Book book) {
        if (book.type == Book.TYPE_LOCAL) {
            return Observable.just(book.getOffset())
                    .map(offset -> Utils.nextLocal(new Page(book), book.getOffset()));
        }
        return chapter(SourceServicess.getSource(book.src), book, book.index)
                .map(s -> {
                    Page page = new Page(book);
                    page.index = book.index;
                    page.chapter = s;
                    return Utils.nextOnline(page, book.getOffset());
                });
    }

    @Override
    public Observable<Page> last(Page target, Page page) {
        if (target.book.type == Book.TYPE_LOCAL) {
            return Observable.just(target.start)
                    .map(offset -> Utils.lastLocal(page, offset));
        }
        return Observable.just(0)
                .flatMap(arg0 -> {
                    if (target.start > 0) {
                        page.chapter = target.chapter;
                        page.index = target.index;
                        page.end = target.start;
                        return Observable.just(page);
                    } else {
                        return chapter(SourceServicess.getSource(target.book.src), target.book, target.index - 1)
                                .map(s -> {
                                    page.chapter = s;
                                    page.index = target.index - 1;
                                    page.end = s.length();
                                    return page;
                                });
                    }
                })
                .map(page1 -> Utils.lastOnline(page, page.end));
    }

    @Override
    public Observable<Page> next(Page target, Page page) {
        if (target.book.type == Book.TYPE_LOCAL) {
            return Observable.just(target.end)
                    .map(offset -> Utils.nextLocal(page, offset));
        }
        return Observable.just(0)
                .flatMap(arg0 -> {
                    if (target.end < target.chapter.length()) {
                        page.chapter = target.chapter;
                        page.index = target.index;
                        page.start = target.end;
                        return Observable.just(page);
                    } else {
                        return chapter(SourceServicess.getSource(target.book.src), target.book, target.index + 1)
                                .map(s -> {
                                    page.chapter = s;
                                    page.index = target.index + 1;
                                    page.start = 0;
                                    return page;
                                });
                    }
                })
                .map(page1 -> Utils.nextOnline(page, page.start));
    }

    private String read(byte[] bytes) throws IOException {
        try (GZIPInputStream gis = new GZIPInputStream(new ByteArrayInputStream(bytes));
             BufferedSource buffer = Okio.buffer(new GzipSource(Okio.source(new ByteArrayInputStream(bytes))))) {
            String encoding = StreamHelper.getCharsetByJUniversalCharDet(gis);
            return buffer.readString(Charset.forName(encoding));
        }
    }
}
