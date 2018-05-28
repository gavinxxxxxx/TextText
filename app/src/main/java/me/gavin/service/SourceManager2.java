package me.gavin.service;

import android.text.TextUtils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;

import java.lang.reflect.Field;
import java.util.List;

import io.reactivex.Observable;
import me.gavin.app.Config;
import me.gavin.app.RxTransformer;
import me.gavin.app.Utils;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;
import me.gavin.app.core.model.Page;
import me.gavin.app.core.source.SourceModel;
import me.gavin.app.core.source.SourceServicess;
import me.gavin.base.App;
import me.gavin.inject.component.ApplicationComponent;
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;
import okhttp3.ResponseBody;
import okio.BufferedSource;
import okio.Okio;

/**
 * SourceManager
 *
 * @author gavin.xiong 2017/4/28
 */
public class SourceManager2 extends BaseManager implements DataLayer.SourceService {

    private static SourceModel src;

    static {
//        Observable.just("src/22ff.json")
                // Observable.just("src/ymoxuan.json")
                 Observable.just("src/daocaorenshuwu.json")
                .map(App.get().getAssets()::open)
                .map(Okio::source)
                .map(Okio::buffer)
                .map(BufferedSource::readUtf8)
                .subscribe(json -> {
                    src = ApplicationComponent.Instance
                            .get()
                            .getGson()
                            .fromJson(json, SourceModel.class);
                });
    }

    @Override
    public Observable<List<Book>> search(String query) {
        return Observable.just(src.queryUrl)
                .map(s -> s.replace("{query}", query))
                .flatMap(url -> getApi().get(url, Config.cacheControlQuery))
                .map(ResponseBody::source)
                .map(BufferedSource::readUtf8)
                .map(Jsoup::parse)
                .map(document -> document.select(src.querySelect))
                .flatMap(Observable::fromIterable)
                .map(element -> {
                    Book book = new Book();
                    book.type = Book.TYPE_ONLINE;
                    book.src = src.id;
                    book.srcName = src.name;
                    for (SourceModel.Field field : src.queryFields) {
                        String regex = TextUtils.isEmpty(field.feature) ? null : field.feature
                                .replace("{bookId}", "(\\S+)")
                                .replace("{ext}", "(\\S+)");
                        String value = getValue(element, field, regex);
                        setValue(book, field.type, value);
                    }
                    return book;
                })
                .filter(book -> !TextUtils.isEmpty(book.id))
                .toList()
                .toObservable();
    }

    @Override
    public Observable<List<Book>> search(SourceServicess source, String query) {
        return null;
    }

    @Override
    public Observable<Book> detail(Book book) {
        return Observable.just(src.detailUrl)
                .map(s -> {
                    s = s.replace("{bookId}", book.id);
                    if (!TextUtils.isEmpty(book.ext))
                        s = s.replace("{ext}", book.ext);
                    return s;
                })
                .flatMap(url -> getApi().get(url, Config.cacheControlDetail))
                .map(ResponseBody::source)
                .map(BufferedSource::readUtf8)
                .map(Jsoup::parse)
                .map(document -> document.selectFirst(src.detailSelect))
                .map(element -> {
                    for (SourceModel.Field field : src.detailFields) {
                        String regex = TextUtils.isEmpty(field.feature) ? null : field.feature
                                .replace("{ext}", "(\\S+)");
                        String value = getValue(element, field, regex);
                        setValue(book, field.type, value);
                    }
                    return book;
                });
    }

    @Override
    public Observable<List<Chapter>> directory(Book book) {
        return Observable.just(src.directoryUrl)
                .map(s -> {
                    s = s.replace("{bookId}", book.id);
                    if (!TextUtils.isEmpty(book.ext))
                        s = s.replace("{ext}", book.ext);
                    return s;
                })
                .flatMap(url -> getApi().get(url, Config.cacheControlDirectory))
                .map(ResponseBody::source)
                .map(BufferedSource::readUtf8)
                .map(Jsoup::parse)
                .map(document -> document.select(src.directorySelect))
                .flatMap(Observable::fromIterable)
                .map(element -> {
                    Chapter chapter = new Chapter();
                    for (SourceModel.Field field : src.directoryFields) {
                        String regex = TextUtils.isEmpty(field.feature) ? null : field.feature
                                .replace("{chapterId}", "(\\S+)")
                                .replace("{ext}", "(\\S+)");
                        String value = getValue(element, field, regex);
                        setValue(chapter, field.type, value);
                    }
                    return chapter;
                })
                .filter(chapter -> !TextUtils.isEmpty(chapter.id))
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
                })
                .compose(RxTransformer.log());
    }

    @Override
    public Observable<String> chapter(SourceServicess source, Book book, int index) {
        return directory(book)
                .map(chapters -> chapters.get(index))
                .map(chapter -> chapter.id)
                .map(id -> {
                    String s = src.chapterUrl
                            .replace("{bookId}", book.id)
                            .replace("{chapterId}", id);
                    if (!TextUtils.isEmpty(book.ext))
                        s = s.replace("{ext}", book.ext);
                    return s;
                })
                .flatMap(url -> getApi().get(url, Config.cacheControlChapter))
                .map(ResponseBody::source)
                .map(BufferedSource::readUtf8)
                .map(Jsoup::parse)
                .map(document -> document.selectFirst(src.chapterSelect))
                .map(Node::outerHtml)
                .map(s -> s.replaceAll("<br/?>", "\\\\n"))
                .map(s -> s.replaceAll("<(p)>(?s)(.*?)</\\1>", "\\\\n$2\\\\n"))
                .map(Jsoup::parse)
                .map(Element::text)
                .map(s -> s.replaceAll("\\\\n", "\n"))
                .compose(RxTransformer.log());
    }

    private String getValue(Element element, SourceModel.Field field, String regex) {
        if (!TextUtils.isEmpty(field.select)) {
            element = element.selectFirst(field.select);
        }
        if (element == null) {
            return null;
        }
        String value = TextUtils.isEmpty(field.attr) ? element.text() : element.attr(field.attr);
        if (!TextUtils.isEmpty(regex)) {
            value = value.replaceFirst(regex, "$1");
        }
        return value;
    }

    private void setValue(Object target, String type, String value) {
        try {
            Field field = target.getClass().getField(type);
            field.setAccessible(true);
            field.set(target, value);
        } catch (Exception e) {
            e.printStackTrace();
        }
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
}
