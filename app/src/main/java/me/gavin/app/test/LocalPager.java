package me.gavin.app.test;

import java.io.IOException;

import me.gavin.app.Config;
import me.gavin.app.StreamHelper;
import me.gavin.app.Utils;
import me.gavin.app.model.Book;
import me.gavin.app.model.Page;

/**
 * 本地
 *
 * @author gavin.xiong 2018/4/27
 */
public class LocalPager extends Pager {

    public LocalPager(Book book) {
        super(book);
    }

    @Override
    public void offset(Long offset) {
        try {
            mPages[1] = fromBook(offset == null ? mBook.getOffset() : offset, false);
            mPages[0] = fromBook(mPages[1].start, true);
            mPages[2] = fromBook(mPages[1].end, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void lastPage() {
        try {
            mPages[2] = mPages[1];
            mPages[1] = mPages[0];
            mPages[0] = mPages[1].isFirst ? null : fromBook(mPages[1].start, true);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void nextPage() {
        try {
            mPages[0] = mPages[1];
            mPages[1] = mPages[2];
            mPages[2] = mPages[1].isLast ? null : fromBook(mPages[1].end, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Page fromBook(long offset, boolean isReverse) throws IOException {
        Page page = new Page();
        page.book = mBook;
        page.isReverse = isReverse;
        if (!isReverse) { // 正向
            page.start = offset;
            page.isFirst = page.start <= 0;
            page.mText = StreamHelper.getText(mBook.open(), mBook.getCharset(), page.start, (int) Math.min(Config.pagePreCount, mBook.getLength() - page.start));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.align = true;
            if (page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)) {
                page.indent = true;
            } else {
                int preCount = (int) (page.start >= Config.segmentPreCount ? Config.segmentPreCount : page.start);
                String fix = StreamHelper.getText(mBook.open(), mBook.getCharset(), page.start - preCount, preCount);
                page.indent = fix.matches(Config.REGEX_SEGMENT_PREFIX);
            }
        } else { // 反向
            page.end = offset;
            page.isLast = page.end >= mBook.getLength();
            page.mText = StreamHelper.getText(mBook.open(), mBook.getCharset(), Math.max(page.end - Config.pagePreCount, 0), (int) Math.min(Config.pagePreCount, page.end));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.indent = true;
            String fix = StreamHelper.getText(mBook.open(), mBook.getCharset(), page.end, Config.segmentPreCount);
            page.align = !page.mText.matches(Config.REGEX_SEGMENT_PREFIX) && !fix.matches(Config.REGEX_SEGMENT_SUFFIX);
        }
        page.prepare();
        return page;
    }
}
