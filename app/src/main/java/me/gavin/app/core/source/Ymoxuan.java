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
// * 书源 - 衍墨轩
// * -
// * 目录 & 章节 url 中含不明数字 - 改变可能导致部分新章节缺失 - book/0/abc | book/1/abc
// *
// * @author gavin.xiong 2018/4/28.
// * @link {https://www.ymoxuan.com/}
// * @link {https://www.ymoxuan.com/book/0/27334/index.html}
// * @link {https://www.ymoxuan.com/book/1/27334/index.html}
// */
//public final class Ymoxuan extends SourceServicess {
//
//    public static Ymoxuan get() {
//        return Hold.INSTANCE;
//    }
//
//    public static class Hold {
//        static Ymoxuan INSTANCE = new Ymoxuan();
//    }
//
//    @Override
//    public String queryUrl(String query) {
//        return String.format("https://www.ymoxuan.com/search.htm?keyword=%s", query);
//    }
//
//    @Override
//    public String querySelector() {
//        return "section[class=container] section[class=lastest] li";
//    }
//
//    @Override
//    public ObservableTransformer<Element, Element> queryFilter() {
//        return upstream -> upstream
//                .filter(element -> !"rowheader".equals(element.className()))
//                .filter(element -> !"pagination".equals(element.className()));
//    }
//
//    @Override
//    public Book query2Book(Element element) {
//        Book book = new Book();
//        book.setName(element.selectFirst("span[class=n2]").text());
//        book.setAuthor(element.selectFirst("span[class=a2]").text());
//        String uri = "https:" + element.selectFirst("span[class=n2] a").attr("href");
//        book.setId(uri.substring(uri.lastIndexOf("/text_") + 6, uri.length() - 5));
//        book.setType(Book.TYPE_ONLINE);
//        book.setSrc("ymoxuan");
//        book.setSrcName("衍墨轩");
//        return book;
//    }
//
//    @Override
//    public String detailsUrl(String id) {
//        return String.format("https://www.ymoxuan.com/text_%s.html", id);
//    }
//
//    @Override
//    public Document bookInfo(Book book, Document doc) {
//        book.cover = doc.selectFirst("body > section > div.left > article.info > div.cover > img").attr("src");
//        book.state = doc.selectFirst("body > section > div.left > article.info > p.detail.pt20 > i:nth-child(3)").text();
//        book.category = doc.selectFirst("body > section > div.left > article.info > p.detail.pt20 > i:nth-child(2) > a").text();
//        book.updateTime = doc.selectFirst("body > section > div.left > article.info > p:nth-child(4) > i").text();
//        book.updateChapter = doc.selectFirst("body > section > div.left > article.info > p:nth-child(5) > i > a").text();
//        book.intro = doc.selectFirst("body > section > div.left > article.info > p.desc").text();
//        return doc;
//    }
//
//    @Override
//    public ObservableTransformer<Document, Chapter> detailChapters() {
//        return upstream -> upstream
//                .map(doc -> doc.select("body > section > div.left > article.info > ul > li > a"))
//                .flatMap(Observable::fromIterable)
//                .take(10)
//                .map(Element::text)
//                .map(title -> new Chapter(null, null, title));
//    }
//
//    @Override
//    public String directoryUrl(String id) {
//        return String.format("https://www.ymoxuan.com/book/99/%s/index.html", id);
//    }
//
//    @Override
//    public String directorySelector() {
//        return "section[class=container] article[class=info] ul[class=mulu] li[class=col3] a";
//    }
//
//    @Override
//    public ObservableTransformer<Elements, Element> directoryFilter() {
//        return upstream -> upstream
//                .flatMap(elements -> Observable
//                        .fromIterable(elements)
//                        .skip(elements.size() >= 18 ? 9 : elements.size() / 2)); // 跳过最新章节
//    }
//
//    @Override
//    public Chapter directory2Chapter(Element element, String bookId) {
//        String uri = element.attr("href");
//        String cid = uri.substring(uri.lastIndexOf("/") + 1, uri.lastIndexOf("."));
//        return new Chapter(bookId, cid, element.text());
//    }
//
//    @Override
//    public String chapterUrl(Chapter chapter) {
//        return String.format("https://www.ymoxuan.com/book/99/%s/%s.html", chapter.bookId, chapter.id);
//    }
//
//    @Override
//    public String chapterSelector() {
//        return "article[class=chaptercontent] div[class=content]";
//    }
//
//    @Override
//    public ObservableTransformer<String, Element> chapter2Text() {
//        return upstream -> upstream
//                .map(s -> s.replaceAll("<br/?>", "\\\\n"))
//                .map(Jsoup::parse)
//                .map(document -> document.selectFirst("article[class=chaptercontent] div[class=content]"));
//    }
//}
