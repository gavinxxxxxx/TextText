package me.gavin.app.model;

import android.graphics.Canvas;

import me.gavin.app.Config;
import me.gavin.app.StreamHelper;

/**
 * ViewModel
 *
 * @author gavin.xiong 2017/12/14
 */
public class Page {

    public Book book;

    public long pageStart;
    public int pageLimit;
    public long pageEnd;

    public boolean isFirst;
    public boolean isLast;

    public boolean firstLineIndent; // 第一行缩进
    public boolean lastLineAlign; // 最后一行对齐 - 只反向有用

    public boolean isReverse;
    public boolean reverseFlag;

    public String mText;
    public String[] mTextSp;

    private Page() { }

    public static Page fromBook(Book book, long offset, boolean isReverse) {
        Page page = new Page();
        page.book = book;
        page.isReverse = isReverse;
        page.reverseFlag = isReverse;
        if (!isReverse) { // 正向
            page.pageStart = offset;
            page.isFirst = page.pageStart <= 0;
            page.mText = StreamHelper.getText(book.open(), page.pageStart, Config.pagePreCount);
            page.mTextSp = page.mText == null ? null : page.mText.split(Config.REGEX_SEGMENT);
            page.firstLineIndent = true;
            page.lastLineAlign = true;
        } else { // 反向
            page.pageEnd = offset;
            page.isLast = page.pageEnd >= book.getLength();
            page.mText = StreamHelper.getText(book.open(), page.pageEnd - Config.pagePreCount, Config.pagePreCount);
            page.mTextSp = page.mText == null ? null : page.mText.split(Config.REGEX_SEGMENT);
            page.firstLineIndent = true;
            page.lastLineAlign = true;
        }
        return page;
    }

    public void abcde() {
        int left, right;
        if (!isReverse) {
            if (this.pageStart <= 0) {
                this.isFirst = true;
                left = 0;
            }
        }
    }
}
