package me.gavin.app.core.source;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.Observable;
import io.reactivex.ObservableTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;

/**
 * 书源 - 稻草人书屋
 *
 * @author gavin.xiong 2018/5/9
 * @link {http://www.daocaorenshuwu.com/}
 */
public final class Daocaorenshuwu extends Source {

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
    public String directoryUrl(String id) {
        String[] split = id.split("/");
        String prefix = split.length > 1 ? split[0] : "";
        String rid = split.length > 1 ? split[1] : split[0];
        return String.format("http://www.daocaorenshuwu.com/%s/%s/", prefix, rid);
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
    public Chapter directory2Chapter(Element element) {
        String uri = element.attr("href");
        String cid = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
        return new Chapter(cid, element.attr("title"));
    }

    @Override
    public String chapterUrl(Chapter chapter) {
        String[] split = chapter.bookId.split("/");
        String prefix = split.length > 1 ? split[0] : "";
        String rid = split.length > 1 ? split[1] : split[0];
        return String.format("http://www.daocaorenshuwu.com/%s/%s/%s.html", prefix, rid, chapter.id);
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
