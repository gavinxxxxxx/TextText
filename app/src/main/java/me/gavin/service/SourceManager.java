package me.gavin.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.Config;
import me.gavin.app.RxTransformer;
import me.gavin.app.Utils;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.source.Source;
import me.gavin.base.App;
import me.gavin.db.dao.ChapterDao;
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;
import me.gavin.util.CacheHelper;
import okhttp3.ResponseBody;
import okio.BufferedSink;
import okio.BufferedSource;
import okio.GzipSink;
import okio.GzipSource;
import okio.Okio;

/**
 * SourceManager
 *
 * @author gavin.xiong 2017/4/28
 */
public class SourceManager extends BaseManager implements DataLayer.SourceService {

    @Override
    public Observable<Book> search(Source source, String query) {
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
    public Observable<Book> detail(Source source, String id) {
        return null;
    }

    @Override
    public Observable<List<Chapter>> directory(Source source, String id) {
        return getApi().get(source.directoryUrl(id), Config.cacheControlDirectory)
                .map(ResponseBody::byteStream)
                .map(this::read)
                .map(Jsoup::parse)
                .map(document -> document.select(source.directorySelector()))
                .compose(source.directoryFilter())
                .map(source::directory2Chapter)
                .toList()
                .toObservable()
                .map(chapters -> {
                    getDaoSession().getChapterDao()
                            .queryBuilder()
                            .where(ChapterDao.Properties.BookId.eq(id))
                            .buildDelete()
                            .executeDeleteWithoutDetachingEntities();
                    for (int i = 0; i < chapters.size(); i++) {
                        chapters.get(i).setBookId(id);
                        chapters.get(i).setIndex(i);
                    }
                    getDaoSession().getChapterDao().saveInTx(chapters);
                    return chapters;
                });
    }

    @Override
    public Observable<String> chapter(Source source, Book book, int index) {
        return Observable.just("/%s(%s)/%s/%s")
                .map(format -> String.format(format, book.getName(), book.getAuthor(), book.getSrc(), index))
                .map(sf -> CacheHelper.getFilesDir(App.get(), ".book") + sf)
                .map(File::new)
                .flatMap(file -> {
                    if (!file.exists() || file.isDirectory()) {
                        Chapter chapter = getDaoSession().getChapterDao()
                                .queryBuilder()
                                .where(ChapterDao.Properties.BookId.eq(book.getId()), ChapterDao.Properties.Index.eq(index))
                                .unique();
                        return getApi().get(source.chapterUrl(chapter), Config.cacheControlChapter)
                                .map(ResponseBody::byteStream)
                                .map(this::read)
                                .compose(source.chapter2Text())
                                .map(Element::text)
                                .map(s -> s.replaceAll("\\\\n", "\n"))
                                .map(s -> write(s, file));
                    }
                    return Observable.just(file)
                            .map(FileInputStream::new)
                            .map(this::read);
                })
                .compose(RxTransformer.log());
    }

    @Override
    public Observable<Page> curr(Book book) {
        if (book.type == Book.TYPE_LOCAL) {
            return Observable.just(book.getOffset())
                    .map(offset -> Utils.nextLocal(new Page(book), book.getOffset()));
        }
        return chapter(Source.getSource(book.src), book, book.getIndex())
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
                        return chapter(Source.getSource(target.book.src), target.book, target.index - 1)
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
                        return chapter(Source.getSource(target.book.src), target.book, target.index + 1)
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

    private String write(String s, File file) throws IOException {
        file.delete();
        file.getParentFile().mkdirs();
        file.createNewFile();
        try (BufferedSink bufferedSink = Okio.buffer(new GzipSink(Okio.sink(file)))) {
            bufferedSink.writeUtf8(s);
            return s;
        }
    }

    private String read(InputStream in) throws IOException {
        try (BufferedSource bufferedSource = Okio.buffer(new GzipSource(Okio.source(in)))) {
            return bufferedSource.readUtf8();
        }
    }
}
