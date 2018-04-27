package me.gavin.service;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
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
                    book.setSrc("衍墨轩");
                    String uri = "https:" + element.selectFirst("span[class=n2] a").attr("href");
                    book.setUri(uri);
                    book.setId(uri.substring(uri.lastIndexOf("/text_") + 6, uri.length() - 5));
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
                .toObservable();
    }

    @Override
    public Observable<String> chapter(String id, String cid) {
//        return getApi().yanmoxuanChapter(id, cid)
//                .map(ResponseBody::byteStream)
//                .map(this::saveStream);

//        return getApi().yanmoxuanChapter(id, cid)
//                .map(ResponseBody::byteStream)
//                .map(this::unGZIP)
//                .compose(RxTransformer.log())
//                .map(s -> s.replaceAll("<br/?>", "~~~\n\\n\n\\\n\\\\n\\\\\n~~~"))
//                .map(Jsoup::parse)
//                .compose(RxTransformer.log())
//                .map(document -> document.selectFirst("article[class=chaptercontent] div[class=content]"))
//                .compose(RxTransformer.log())
//                .map(Element::text)
//                .compose(RxTransformer.log())
//                .map(this::saveString);

        return getApi().yanmoxuanChapter(id, cid)
                .map(ResponseBody::byteStream)
                .map(this::unGZIP)
                .map(s -> s.replaceAll("<br/?>", "\\\\n"))
                .map(Jsoup::parse)
                .map(document -> document.selectFirst("article[class=chaptercontent] div[class=content]"))
                .map(Element::text)
                .map(s -> s.replaceAll("\\\\n", "\n"));
    }

    private String saveStream(InputStream in) throws IOException {
        String path = CacheHelper.getFilesDir(App.get(), "book") + "/test.t";
        try (FileOutputStream fos = new FileOutputStream(path)) {
            byte[] buffer = new byte[8 * 1024];
            int bytesRead;
            while ((bytesRead = in.read(buffer)) != -1) {
                fos.write(buffer, 0, bytesRead);
            }
            return path;
        }
    }

    private String saveString(String string) throws IOException {
        String path = CacheHelper.getFilesDir(App.get(), "book") + "/test.s";
        try (FileWriter writer = new FileWriter(path)) {
            writer.write(string);
            return path;
        }
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

    private static byte[] gzip(byte[] bytes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             GZIPOutputStream zos = new GZIPOutputStream(bos)) {
            zos.write(bytes);
            zos.finish();
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }

    private static byte[] unGzip(byte[] bytes) {
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream();
             ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
             GZIPInputStream zis = new GZIPInputStream(bis)) {
            byte[] buffer = new byte[4096];
            int temp;
            while ((temp = zis.read(buffer)) > 0)
                bos.write(buffer, 0, temp);
            return bos.toByteArray();
        } catch (IOException e) {
            return null;
        }
    }
}
