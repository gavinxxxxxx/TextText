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

    public long pageOffset;
    public int pageLimit;
    public int pagePreCount;

    public float textSize = 40f; // 文字大小
    public float textHeight = textSize * 1.3271484f; // 文字高度
    public float textAscent = textSize * -0.9277344f;
    public float textDescent = textSize * 0.24414062f;
    public int textColor = 0xFF000000; // 文字颜色

    public float topPadding = 50f;
    public float bottomPadding = topPadding;
    public float leftPadding = 50f;
    public float rightPadding = leftPadding;

    public float segmentSpacing = textSize * 0.5f; // 段间距
    public float lineSpacing = textSize * 0.2f; // 行间距
    public float indent = textSize * 2f; // 首行缩进
    public float wordSpacingMax = textSize * 0.5f; // 单词最大间距

    public final Paint textPaint, debugPaint;

    public ViewModel() {
        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textAscent = fontMetrics.ascent;
        textDescent = fontMetrics.descent;
        textHeight = fontMetrics.bottom - fontMetrics.top;

        int lineCount = textPaint.breakText(Config.TEXT_A, true,
                DisplayUtil.getScreenWidth() - leftPadding - rightPadding, null);
        pagePreCount = lineCount * (int) Math.ceil((DisplayUtil.getScreenHeight() - topPadding - bottomPadding) / textHeight);

        debugPaint = new Paint();
        debugPaint.setColor(0x22222222);
    }

}
