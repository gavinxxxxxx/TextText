package me.gavin.app.model;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Page
 *
 * @author gavin.xiong 2017/12/14
 */
public class Page {

    public final Book book;
    public String chapter;

    public int index;
    public long start;
    public int limit;
    public long end;

    public boolean isFirst;
    public boolean isLast;

    public boolean isReverse; // 反向

    public boolean indent; // 页面第一行是否缩进 - 第一行可能不是自然段起始位置
    public boolean align; // 页面最后一行是否分散对齐 - 只反向有用

    public String mText; // 页面预加载文字 - 直接读取
    public String[] mTextSp; // 页面段落数组

    public final List<Line> lineList; // 页面文字分行
    public final List<Word> wordList; // 页面按字词拆分

    public final int type;
    public boolean ready;

    public Page(Book book) {
        this.book = book;
        this.type = book.getType();
        lineList = new ArrayList<>();
        wordList = new LinkedList<>();
    }

}
