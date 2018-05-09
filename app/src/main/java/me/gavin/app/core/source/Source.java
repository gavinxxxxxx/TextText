package me.gavin.app.core.source;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import io.reactivex.ObservableTransformer;
import me.gavin.app.core.model.Book;
import me.gavin.app.core.model.Chapter;

/**
 * 书源
 *
 * @author gavin.xiong 2018/4/28.
 */
public abstract class Source {

    public String id; // daocaorenshuwu
    public String name; // 稻草人书屋
    public String url; // http://www.daocaorenshuwu.com

    /* ******************************************** 搜索 **************************************** */

    public abstract String queryUrl(String query);

    public abstract String querySelector();

    public abstract ObservableTransformer<Element, Element> queryFilter();

    public abstract Book query2Book(Element element);

    /* ******************************************** 详情 **************************************** */



    /* ******************************************** 目录 **************************************** */

    public abstract String directoryUrl(String id);

    public abstract String directorySelector();

    public abstract ObservableTransformer<Elements, Element> directoryFilter();

    public abstract Chapter directory2Chapter(Element element, String bookId);

    /* ******************************************** 章节 **************************************** */

    public abstract String chapterUrl(Chapter chapter);

    public abstract String chapterSelector();

    public abstract ObservableTransformer<String, Element> chapter2Text();

    /* ******************************************** 什么 **************************************** */

    public static Source getSource(String src) {
        switch (src) {
            case "ymoxuan":
                return new Ymoxuan();
            case "daocaorenshuwu":
                return new Daocaorenshuwu();
            default:
                return null;
        }
    }
}
