package me.gavin.service;

import org.jsoup.Jsoup;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
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
import me.gavin.service.base.BaseManager;
import me.gavin.service.base.DataLayer;

/**
 * SourceManager
 *
 * @author gavin.xiong 2017/4/28
 */
public class SourceManager extends BaseManager implements DataLayer.SourceService {

    @Override
    public Observable<Book> search(String query) {
        return getApi().yanmoxuanQuery(query)
                .map(responseBody -> unGZIP(responseBody.byteStream()))
                .compose(RxTransformer.log())
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
                    book.setUri("https:" + element.selectFirst("span[class=n2] a").attr("href"));
                    return book;
                });
    }

    @Override
    public Observable<Book> detail(String id) {
        return null;
    }

    @Override
    public Observable<List<Chapter>> directory(String id) {
        return null;
    }

    @Override
    public Observable<String> chapter(String id, String chapter) {
        return null;
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
