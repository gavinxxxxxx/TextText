package me.gavin.app.test;

import java.io.IOException;

import me.gavin.app.Config;
import me.gavin.app.StreamHelper;
import me.gavin.app.Utils;
import me.gavin.app.model.Page;

/**
 * 本地
 *
 * @author gavin.xiong 2018/4/27
 */
public class LocalPager extends Pager {

    @Override
    public void offset(Long offset) {
        try {
            mView.pages[1] = offset(offset == null ? mView.book.getOffset() : offset, false);
            mView.pages[0] = mView.pages[1].isFirst ? null : offset(mView.pages[1].start, true);
            mView.pages[2] = mView.pages[1].isLast ? null : offset(mView.pages[1].end, false);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void offset(boolean reserve) {
        try {
            if (reserve) {
                mView.pages[2] = mView.pages[1];
                mView.pages[1] = mView.pages[0];
                mView.pages[0] = mView.pages[1].isFirst ? null : offset(mView.pages[1].start, true);
            } else {
                mView.pages[0] = mView.pages[1];
                mView.pages[1] = mView.pages[2];
                mView.pages[2] = mView.pages[1].isLast ? null : offset(mView.pages[1].end, false);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected Page offset(long offset, boolean reserve) throws IOException {
        Page page = new Page();
        page.book = mView.book;
        page.isReverse = reserve;
        if (!reserve) { // 正向
            page.start = offset;
            page.isFirst = page.start <= 0;
            page.mText = StreamHelper.getText(mView.book.open(), mView.book.getCharset(), page.start,
                    (int) Math.min(Config.pagePreCount, mView.book.getLength() - page.start));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.align = true;
            if (page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)) {
                page.indent = true;
            } else {
                int preCount = (int) (page.start >= Config.segmentPreCount ? Config.segmentPreCount : page.start);
                String fix = StreamHelper.getText(mView.book.open(), mView.book.getCharset(),
                        page.start - preCount, preCount);
                page.indent = fix.matches(Config.REGEX_SEGMENT_PREFIX);
            }
        } else { // 反向
            page.end = offset;
            page.isLast = page.end >= mView.book.getLength();
            page.mText = StreamHelper.getText(mView.book.open(), mView.book.getCharset(),
                    Math.max(page.end - Config.pagePreCount, 0), (int) Math.min(Config.pagePreCount, page.end));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.indent = true;
            String fix = StreamHelper.getText(mView.book.open(), mView.book.getCharset(), page.end, Config.segmentPreCount);
            page.align = !page.mText.matches(Config.REGEX_SEGMENT_PREFIX) && !fix.matches(Config.REGEX_SEGMENT_SUFFIX);
        }
        page.prepare();
        return page;
    }

}
