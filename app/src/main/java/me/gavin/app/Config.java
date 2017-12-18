package me.gavin.app;

import android.graphics.Paint;
import android.text.TextPaint;

import me.gavin.util.DisplayUtil;
import me.gavin.util.SPUtil;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/12/18
 */
public final class Config {

    public static final String REGEX_SEGMENT = "[\\s　]*\\n[\\s　]*"; // 分段
    public static final String REGEX_GROUP = "\\w+"; // 连续的字母或数字 - 不可分割
    public static final String REGEX_PUNCTUATION = "[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]"; // 标点符号
    public static final String REGEX_PUNCTUATION_LEFT = "[({'\\[<（｛【‘“]"; // 标点符号
    public static final String REGEX_PUNCTUATION_RIGHT = "[)}'\\]>）｝】’”]"; // 标点符号
    public static final String REGEX_PUNCTUATION_N = "[`~!@#$^&*)=|}:;,\\].>/?~！@#￥……&*）——|}】‘；：”'。，、？]"; // 标点符号
    public static final String REGEX_WORD = "[A-Za-z0-9\\-]"; // 单词
    public static final String REGEX_WORD2 = "[A-Za-z0-9\\-]+"; // 单词
    public static final String REGEX_WORD3 = "([A-Za-z0-9\\-]+|[^A-Za-z0-9\\-])"; // 单词
    public static final String TEXT_A = "abcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyzabcdefghijklmnopqrstuvwxyz"; // 示范
    public static final String TEXT_B = "iiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiiii"; // 示范


    public static int pagePreCount; // 预加载字符数

    public static float textSize; // 文字大小
    public static float textHeight; // 文字高度
    public static float textAscent;
    public static float textDescent;
    public static float textLeading;
    public static int textColor; // 文字颜色

    public static float topPadding;
    public static float bottomPadding;
    public static float leftPadding;
    public static float rightPadding;

    public static float segmentSpacing; // 段间距
    public static float lineSpacing; // 行间距
    public static float indent; // 首行缩进
    public static float wordSpacingMax; // 单词最大间距

    public static final Paint textPaint, debugPaint;

    static {
        textSize = SPUtil.getFloat("textSize", 41f);
        // textHeight = textSize * 1.3271484f;
        // textAscent = textSize * -0.9277344f;
        // textDescent = textSize * 0.24414062f;
        textColor = SPUtil.getInt("textColor", 0xFF000000);

        topPadding = SPUtil.getFloat("topPadding", 50f);
        bottomPadding = topPadding;
        leftPadding = SPUtil.getFloat("leftPadding", 50f);
        rightPadding = leftPadding;

        segmentSpacing = textSize * SPUtil.getFloat("segmentSpacing", 0.5f);
        lineSpacing = textSize * SPUtil.getFloat("lineSpacing", 0.2f);
        indent = textSize * SPUtil.getFloat("indent", 2f);
        wordSpacingMax = textSize * 0.5f;

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        debugPaint = new Paint();
        debugPaint.setColor(0x22222222);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textAscent = fontMetrics.ascent;
        textDescent = fontMetrics.descent;
        textLeading = fontMetrics.leading;
        textHeight = fontMetrics.bottom - fontMetrics.top;

        int lineCount = textPaint.breakText(Config.TEXT_A, true,
                DisplayUtil.getScreenWidth() - leftPadding - rightPadding, null);
        pagePreCount = lineCount * (int) Math.ceil((DisplayUtil.getScreenHeight() - topPadding - bottomPadding) / textHeight);

        L.e(textAscent);
        L.e(textDescent);
        L.e(textLeading);
        L.e(textHeight);
    }

}
