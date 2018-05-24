package me.gavin.app.core.source;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;

/**
 * 书源 - 稻草人书屋
 * -
 * 书籍id不统一 - abc | book/xyz
 *
 * @author gavin.xiong 2018/5/9
 * @link {http://www.daocaorenshuwu.com/}
 * @link {http://www.daocaorenshuwu.com/quanzhifashi/}
 * @link {http://www.daocaorenshuwu.com/book/conglingkaishi/}
 */
public final class Daocaorenshuwu extends SourceServicess {

    public static Daocaorenshuwu get() {
        return Hold.INSTANCE;
    }

    public static class Hold {
        static Daocaorenshuwu INSTANCE = new Daocaorenshuwu();
    }

    @Override
    public String queryUrl(String query) {
        return String.format("http://www.daocaorenshuwu.com/plus/search.php?q=%s", query);
    }

    @Override
    public String querySelector() {
        return "div[class=container] div[class=panel-body] tbody tr";
    }

    @Override
    public ObservableTransformer<Element, Element> queryFilter() {
        return upstream -> upstream;
    }

    @Override
    public Book query2Book(Element element) {
        Elements elements = element.select("td");
        Book book = new Book();
        book.setName(elements.get(0).child(0).attr("title"));
        book.setAuthor(elements.get(1).text());
        String uri = elements.get(0).child(0).attr("href"); // /book/bookId/
        book.setId(uri.substring(1, uri.length() - 1));
        book.setType(Book.TYPE_ONLINE);
        book.setSrc("daocaorenshuwu");
        book.setSrcName("稻草人书屋");
        return book;
    }

    @Override
    public String detailsUrl(String id) {
        return String.format("http://www.daocaorenshuwu.com/%s/", id);
    }

    @Override
    public Document bookInfo(Book book, Document doc) {
        book.cover = "http://www.daocaorenshuwu.com" + doc.selectFirst("body > div:nth-child(5) > div.col-big.fl > div.book-info > div > div > div.media > div.media-left > a > img").attr("src");
        book.state = doc.selectFirst("body > div:nth-child(5) > div.col-big.fl > div.book-info > div > div > div.media > div.media-body > div.row > div:nth-child(2)").text();
        book.category = doc.selectFirst("body > div:nth-child(5) > div.col-big.fl > div.book-info > div > div > div.media > div.media-body > div.row > div:nth-child(3)").text();
        book.updateTime = doc.selectFirst("body > div:nth-child(5) > div.col-big.fl > div.book-info > div > div > div.media > div.media-body > div.row > div:nth-child(8)").text();
        book.updateChapter = doc.selectFirst("body > div:nth-child(5) > div.col-big.fl > div.book-info > div > div > div.media > div.media-body > div.row > div:nth-child(7) > a").text();
        book.intro = doc.selectFirst("body > div:nth-child(5) > div.col-big.fl > div.book-info > div > div > div.row.mt10 > div.col-sm-11.col-xs-10 > div").text();
        return doc;
    }

    @Override
    public ObservableTransformer<Document, Chapter> detailChapters() {
        return upstream -> upstream
                .map(doc -> doc.select("body > div:nth-child(5) > div.col-big.fl > div.chapter > div:nth-child(1) > div.panel-body > div > div > a"))
                .flatMap(Observable::fromIterable)
                .take(10)
                .map(Element::text)
                .map(title -> new Chapter(null, null, title));
    }

    @Override
    public String directoryUrl(String id) {
        // String[] split = id.split("/");
        // String prefix = split.length > 1 ? split[0] : "";
        // String rid = split.length > 1 ? split[1] : split[0];
        return String.format("http://www.daocaorenshuwu.com/%s/", id);
    }

    @Override
    public String directorySelector() {
        return "div[id=all-chapter] div[class=panel-body] div[class=col-md-6 item] a";
    }

    @Override
    public ObservableTransformer<Elements, Element> directoryFilter() {
        return upstream -> upstream.flatMap(Observable::fromIterable);
    }

    @Override
    public Chapter directory2Chapter(Element element, String bookId) {
        String uri = element.attr("href");
        String cid = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
        return new Chapter(bookId, cid, element.attr("title"));
    }

    @Override
    public String chapterUrl(Chapter chapter) {
        return String.format("http://www.daocaorenshuwu.com/%s/%s.html", chapter.bookId, chapter.id);
    }

    @Override
    public String chapterSelector() {
        return "div[class=container] div[class=content]  div[class=cont-text]";
    }

    @Override
    public ObservableTransformer<String, Element> chapter2Text() {
        return upstream -> upstream.map(Jsoup::parse)
                .map(document -> document.selectFirst("div[class=container] div[class=content]  div[class=cont-text]"))
                .map(Element::toString)
                .map(s -> s.replaceAll("<br *?/?>", "\\\\n"))
                .map(s -> s.replaceAll("<(p|div)>(?s)(.*?)</\\1>", "\\\\n$2\\\\n"))
                .map(s -> s.replaceAll("<(\\w+) class=[^<]+?</\\1>", ""))
                .map(Jsoup::parse);
    }
}
