package me.gavin.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.Config;
import me.gavin.app.RxTransformer;
import me.gavin.app.Utils;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.source.Source;
import me.gavin.app.core.source.SourceServicess;
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
    public Observable<Book> search(List<SourceServicess> sources, String query) {
        List<Observable<Book>> observables = new ArrayList<>();
        for (SourceServicess source : sources) {
            observables.add(search(source, query));
        }
        return Observable.merge(observables);
//        return Observable.fromIterable(sources)
//                .flatMap(source -> search(source, query));
    }

    public Observable<String> o1() {
        return Observable.just("A", "B","C", "D","E", "F");
    }

    @Override
    public Observable<Book> search(SourceServicess source, String query) {
        return getApi().get(source.queryUrl(query), Config.cacheControlQuery)
                .map(ResponseBody::byteStream)
                .map(this::read)
                .map(Jsoup::parse)
                .map(document -> document.select(source.querySelector()))
                .flatMap(Observable::fromIterable)
                .compose(source.queryFilter())
                .compose(RxTransformer.log())
                .map(source::query2Book);
    }

    @Override
    public Observable<Book> detail(SourceServicess source, String id) {
        return null;
    }

    @Override
    public Observable<List<Chapter>> directory(Book book) {
        SourceServicess source = SourceServicess.getSource(book.getSrc());
        return getApi().get(source.directoryUrl(book.id), Config.cacheControlDirectory)
                .map(ResponseBody::byteStream)
                .map(this::read)
                .map(Jsoup::parse)
                .map(document -> document.select(source.directorySelector()))
                .compose(source.directoryFilter())
                .map(element -> source.directory2Chapter(element, book.id))
                .toList()
                .toObservable()
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
                .map(ResponseBody::byteStream)
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
        return chapter(SourceServicess.getSource(book.src), book, book.getIndex())
                .map(s -> {
                    Page page = new Page(book);
                    page.index = book.getIndex();
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

    private String read(InputStream in) throws IOException {
        try (BufferedSource bufferedSource = Okio.buffer(new GzipSource(Okio.source(in)))) {
            return bufferedSource.readUtf8();
        }
    }
}
