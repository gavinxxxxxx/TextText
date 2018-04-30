package me.gavin.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import io.reactivex.Observable;
import me.gavin.app.RxTransformer;
import me.gavin.app.model.Book;
import me.gavin.app.model.Chapter;
import me.gavin.base.App;
import me.gavin.db.dao.ChapterDao;
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;
import me.gavin.util.CacheHelper;
import okhttp3.ResponseBody;

/**
 * SourceManager
 *
 * @author gavin.xiong 2017/4/28
 */
public class SourceManager extends BaseManager implements DataLayer.SourceService {

    @Override
    public Observable<Book> search(String query) {
        return getApi().yanmoxuanQuery(query)
                .map(ResponseBody::byteStream)
                .map(this::unGZIP)
                .map(Jsoup::parse)
                .map(document -> document.select("section[class=container] section[class=lastest] li"))
                .flatMap(Observable::fromIterable)
                .filter(element -> !"rowheader".equals(element.className()))
                .filter(element -> !"pagination".equals(element.className()))
                .compose(RxTransformer.log())
                .map(element -> {
                    Book book = new Book();
                    book.setName(element.selectFirst("span[class=n2]").text());
                    book.setAuthor(element.selectFirst("span[class=a2]").text());
                    String uri = "https:" + element.selectFirst("span[class=n2] a").attr("href");
                    book.setUri(uri);
                    book.setId(uri.substring(uri.lastIndexOf("/text_") + 6, uri.length() - 5));
                    book.setType(Book.TYPE_ONLINE);
                    book.setSrc("衍墨轩");
                    book.setCharset("utf-8");
                    return book;
                });
    }

    @Override
    public Observable<Book> detail(String id) {
        return getApi().yanmoxuanDetail(id)
                .map(ResponseBody::string)
                .map(s -> new Book());
    }

    @Override
    public Observable<List<Chapter>> directory(String id) {
        return getApi().yanmoxuanDirectory(id)
                .map(ResponseBody::byteStream)
                .map(this::unGZIP)
                .map(Jsoup::parse)
                .map(document -> document.select("section[class=container] article[class=info] ul[class=mulu] li[class=col3] a"))
                .flatMap(Observable::fromIterable)
                .map(element -> {
                    String uri = element.attr("href");
                    String cid = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
                    return new Chapter(cid, element.text());
                })
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

    public Observable<String> getFromNet(String bid, String cid) {
        return getApi().yanmoxuanChapter(bid, cid)
                .map(ResponseBody::byteStream)
                .map(this::unGZIP)
                .map(s -> s.replaceAll("<br/?>", "\\\\n"))
                .map(Jsoup::parse)
                .map(document -> document.selectFirst("article[class=chaptercontent] div[class=content]"))
                .map(Element::text)
                .map(s -> s.replaceAll("\\\\n", "\n"))
                .map(s -> {
                    GZIP(s, null);
                    return s;
                });
    }

    public Observable<String> getFromCache(Book book, int index) {
        String path = CacheHelper.getFilesDir(App.get(), "book") + "/test.x";
        return Observable.just(path)
//        return Observable.just(".Books/%s(%s)/%s/##%s##")
//                .map(format -> String.format(format, book.getName(), book.getAuthor(), book.getSrc(), index))
                .map(FileInputStream::new)
                .map(this::unGZIP);
    }

    @Override
    public Observable<String> chapter(Book book, int index) {
        return Observable.just("/%s(%s)/%s/%s")
                .map(format -> String.format(format, book.getName(), book.getAuthor(), book.getSrc(), index))
                .map(sf -> CacheHelper.getFilesDir(App.get(), ".Books") + sf)
                .map(File::new)
                .flatMap(file -> {
                    if (!file.exists() || file.isDirectory()) {
                        file.delete();
                        file.getParentFile().mkdirs();
                        file.createNewFile();
                        Chapter chapter = getDaoSession().getChapterDao()
                                .queryBuilder()
                                .where(ChapterDao.Properties.BookId.eq(book.getId()), ChapterDao.Properties.Index.eq(index))
                                .unique();
                        return getApi().yanmoxuanChapter(book.getId(), chapter.getId())
                                .map(ResponseBody::byteStream)
                                .map(this::unGZIP)
                                .map(s -> s.replaceAll("<br/?>", "\\\\n"))
                                .map(Jsoup::parse)
                                .map(document -> document.selectFirst("article[class=chaptercontent] div[class=content]"))
                                .map(Element::text)
                                .map(s -> s.replaceAll("\\\\n", "\n"))
                                .map(s -> GZIP(s, file));
                    }
                    return Observable.just(file)
                            .map(FileInputStream::new)
                            .map(this::unGZIP);
                })
                .compose(RxTransformer.log());
    }

    private String GZIP(String s, File file) throws IOException {
        file.mkdirs();
        String path = CacheHelper.getFilesDir(App.get(), "book") + "/test.x";
        try (FileOutputStream fos = new FileOutputStream(file);
             GZIPOutputStream gos = new GZIPOutputStream(fos)) {
            gos.write(s.getBytes("utf-8"));
            gos.finish();
        }
        return s;
    }

    private String unGZIP(InputStream in) throws IOException {
        try (GZIPInputStream gzip = new GZIPInputStream(in);
             Reader reader = new InputStreamReader(gzip);
             Writer writer = new StringWriter()) {
            char[] buffer = new char[4096];
            int temp;
            while ((temp = reader.read(buffer)) > 0)
                writer.write(buffer, 0, temp);
            return writer.toString();
        }
    }
}
