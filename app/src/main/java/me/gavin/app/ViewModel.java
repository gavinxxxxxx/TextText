package me.gavin.app;

import android.graphics.Paint;
import android.text.TextPaint;

import me.gavin.util.DisplayUtil;

/**
 * ViewModel
 *
 * @author gavin.xiong 2017/12/14
 */
public final class ViewModel {

    public final String REGEX_SEGMENT = "[\\s　]*\\n[\\s　]*"; // 分段
    public final String REGEX_GROUP = "\\w+"; // 连续的字母或数字 - 不可分割
    public final String REGEX_PUNCTUATION = "[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]"; // 标点符号
    public final String REGEX_PUNCTUATION_LEFT = "[({'\\[<（｛【‘“]"; // 标点符号
    public final String REGEX_PUNCTUATION_RIGHT = "[)}'\\]>）｝】’”]"; // 标点符号
    public final String REGEX_PUNCTUATION_N = "[`~!@#$^&*)=|}:;,\\].>/?~！@#￥……&*）——|}】‘；：”'。，、？]"; // 标点符号
    public final String REGEX_WORD = "[A-Za-z0-9\\-]"; // 单词
    public final String REGEX_WORD2 = "[A-Za-z0-9\\-]+"; // 单词
    public final String REGEX_WORD3 = "([A-Za-z0-9\\-]+|[^A-Za-z0-9\\-])"; // 单词
    public static final String TEXT_A = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"; // 示范
    public static final String TEXT_B = "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"; // 示范

    public long pageOffset;
    public int pageLimit;
    public int pagePreCount;

    public float textSize = 40f; // 文字大小
    public float textHeight = textSize * 1.3271484f;
    public float textAscent = textSize * -0.9277344f;
    public float textDescent = textSize * 0.24414062f;
    public int textColor = 0xFF000000;

    public float topPadding = 80f;
    public float bottomPadding = topPadding;
    public float leftPadding = 50f;
    public float rightPadding = leftPadding;

    public float segmentSpacing = textSize * 1f; // 段间距
    public float lineSpacing = textSize * 0.0f; // 行间距
    public float indent = textSize * 2f; // 首行缩进
    public float wordSpacingMax = textSize * 0.5f; // 单词最大间距

    public final Paint mTextPaint, mDebugPaint;

    public ViewModel() {
        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(textSize);
        mTextPaint.setColor(textColor);
        Paint.FontMetrics fontMetrics = mTextPaint.getFontMetrics();
        textAscent = fontMetrics.ascent;
        textDescent = fontMetrics.descent;
        textHeight = fontMetrics.bottom - fontMetrics.top;

        int lineCount = mTextPaint.breakText(ViewModel.TEXT_A, true,
                DisplayUtil.getScreenWidth() - leftPadding - rightPadding, null);
        pagePreCount = lineCount * (int) Math.ceil((DisplayUtil.getScreenHeight() - topPadding - bottomPadding) / textHeight);

        mDebugPaint = new Paint();
        mDebugPaint.setColor(0x22222222);
    }

}
