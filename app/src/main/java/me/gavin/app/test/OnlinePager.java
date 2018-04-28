package me.gavin.app.test;

import java.io.IOException;

import me.gavin.app.Config;
import me.gavin.app.Utils;
import me.gavin.app.model.Page;

/**
 * 在线 - 分页
 *
 * @author gavin.xiong 2018/4/27.
 */
public class OnlinePager extends Pager {

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
            page.mText = page.book.getText().substring((int) page.start, Math.min((int) page.start + Config.pagePreCount, page.book.getText().length()));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.align = true;
            if (page.isFirst || page.mText.matches(Config.REGEX_SEGMENT_SUFFIX)) {
                page.indent = true;
            } else {
                int preCount = (int) (page.start >= Config.segmentPreCount ? Config.segmentPreCount : page.start);
                String fix = page.book.getText().substring((int) page.start - preCount, (int)page.start);
                page.indent = fix.matches(Config.REGEX_SEGMENT_PREFIX);
            }
        } else { // 反向
            page.end = offset;
            page.isLast = page.end >= page.book.getText().length();
            int si = Math.max((int) page.end - Config.pagePreCount, 0);
            page.mText = page.book.getText().substring(si, si + Math.min(Config.pagePreCount, (int) page.end));
            page.mTextSp = Utils.trim(page.mText).split(Config.REGEX_SEGMENT);
            page.indent = true;
            String fix = page.book.getText().substring((int) page.end, (int)page.end + Config.segmentPreCount);
            page.align = !page.mText.matches(Config.REGEX_SEGMENT_PREFIX) && !fix.matches(Config.REGEX_SEGMENT_SUFFIX);
        }
        page.prepare();
        return page;
    }
}
