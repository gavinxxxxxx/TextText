package me.gavin.app;

import me.gavin.base.App;

/**
 * ViewModel
 *
 * @author gavin.xiong 2017/12/14
 */
public class ViewModel {

    public long pageOffset;
    public int pageLimit;
    public long pageEnd;

    public boolean isLast;

    public String mText;
    public String[] mTextSp;

    public ViewModel(long pageOffset, boolean isLast) {
        this.isLast = isLast;
        if (isLast) {
            this.pageEnd = pageOffset;
            this.mText = AssetsUtils.readText(App.get(), "zx.txt", pageEnd - Config.pagePreCount, Config.pagePreCount);
            this.mTextSp = mText == null ? null : mText.split(Config.REGEX_SEGMENT);
        } else {
            this.pageOffset = pageOffset;
            this.mText = AssetsUtils.readText(App.get(), "zx.txt", pageOffset, Config.pagePreCount);
            this.mTextSp = mText == null ? null : mText.split(Config.REGEX_SEGMENT);
        }
    }
}
