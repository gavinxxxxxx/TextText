//package me.gavin.app.core.source;
//
//import org.jsoup.Jsoup;
//import org.jsoup.nodes.Document;
//import org.jsoup.nodes.Element;
//import org.jsoup.select.Elements;
//
//import io.reactivex.Observable;
//import io.reactivex.ObservableTransformer;
//import me.gavin.app.core.model.Book;
//import me.gavin.app.core.model.Chapter;
//
///**
// * 书源 - 无弹窗小说网
// *
// * @author gavin.xiong 2018/5/24.
// * @link {http://www.22ff.com/}
// */
//public final class _22ff extends SourceServicess {
//
//    public static _22ff get() {
//        return Hold.INSTANCE;
//    }
//
//    public static class Hold {
//        static _22ff INSTANCE = new _22ff();
//    }
//
//    @Override
//    public String queryUrl(String query) {
//        return String.format("http://www.22ff.com/s_%s", query);
//    }
//
//    @Override
//    public String querySelector() {
//        return "body > div:nth-child(3) > div.neirong > ul";
//    }
//
//    @Override
//    public ObservableTransformer<Element, Element> queryFilter() {
//        return upstream -> upstream
//                .filter(element -> "neirong1".equals(element.child(0).className()));
//    }
//
//    @Override
//    public Book query2Book(Element element) {
//        Book book = new Book();
//        book.setName(element.selectFirst("li.neirong1 > a:nth-child(1)").text());
//        book.setAuthor(element.selectFirst("li.neirong4 > a").text());
//        String uri = "http://www.22ff.com" + element.selectFirst("li.neirong1 > a:nth-child(1)").attr("href"); // /xs/139419/
//        book.setId(uri.substring(uri.lastIndexOf("/xs/") + 4, uri.length() - 1));
//        book.setType(Book.TYPE_ONLINE);
//        book.setSrc("22ff");
//        book.setSrcName("无弹窗小说网");
//        return book;
//    }
//
//    @Override
//    public String detailsUrl(String id) {
//        return String.format("http://www.22ff.com/xs/%s/", id);
//    }
//
//    @Override
//    public Document bookInfo(Book book, Document doc) {
//        book.cover = doc.selectFirst("body > div:nth-child(3) > table > tbody > tr:nth-child(1) > td:nth-child(1) > img").attr("src");
//        // book.state =
//        book.category = doc.selectFirst("body > div:nth-child(3) > table > tbody > tr:nth-child(1) > td:nth-child(5) > a").text();
//        book.updateTime = doc.selectFirst("body > div:nth-child(3) > table > tbody > tr:nth-child(1) > td:nth-child(7)").text();
//        book.updateChapter = doc.selectFirst("body > div:nth-child(3) > table > tbody > tr:nth-child(2) > td > a").text();
//        book.intro = doc.selectFirst("body > div:nth-child(3) > table > tbody > tr:nth-child(4) > td").text();
//        return doc;
//    }
//
//    @Override
//    public ObservableTransformer<Document, Chapter> detailChapters() {
//        return upstream -> upstream
//                .map(doc -> doc.select("body > div.main > div.neirong > div.clc > a"))
//                .flatMap(Observable::fromIterable)
//                .take(10)
//                .map(Element::text)
//                .map(title -> new Chapter(null, null, title));
//    }
//
//    @Override
//    public String directoryUrl(String id) {
//        return String.format("http://www.22ff.com/xs/%s/", id);
//    }
//
//    @Override
//    public String directorySelector() {
//        return "body > div.main > div.neirong > div.clc > a";
//    }
//
//    @Override
//    public ObservableTransformer<Elements, Element> directoryFilter() {
//        return upstream -> upstream.flatMap(Observable::fromIterable);
//    }
//
//    @Override
//    public Chapter directory2Chapter(Element element, String bookId) {
//        String uri = element.attr("href");
//        String cid = uri.replaceFirst("/xs/" + bookId + "/", "")
//                .replaceFirst("/", "");
//        return new Chapter(bookId, cid, element.text());
//    }
//
//    @Override
//    public String chapterUrl(Chapter chapter) {
//        return String.format("http://www.22ff.com/xs/%s/%s/", chapter.bookId, chapter.id);
//    }
//
//    @Override
//    public String chapterSelector() {
//        return "#chapter_content";
//    }
//
//    @Override
//    public ObservableTransformer<String, Element> chapter2Text() {
//        return upstream -> upstream
//                .map(s -> s.replaceAll("<br/?>", "\\\\n"))
//                .map(s -> s.replaceAll("<(p)>(?s)(.*?)</\\1>", "\\\\n$2\\\\n"))
//                .map(Jsoup::parse);
//    }
//}
