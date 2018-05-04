package me.gavin.app.core.model;

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

    public boolean indent; // 正向首行缩进 - 由上往下排 反向是否缩进由内容直接决定
    public boolean align; // 反向尾行分散对齐 - 由上往下排 正向是否分散对齐由内容决定

    public String mText; // 页面预加载文字 - 直接读取
    public String[] mTextSp; // 页面段落数组

    public final List<Line> lineList; // 页面文字分行
    public final List<Word> wordList; // 页面按字词拆分

    public boolean ready;

    public Page(Book book) {
        this.book = book;
        lineList = new ArrayList<>();
        wordList = new LinkedList<>();
    }
}
