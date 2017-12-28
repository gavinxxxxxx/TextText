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


    public static int pagePreCount; // 页面预加载字符数
    public static int segmentPreCount; // 段落预加载字符数

    public static int textSize; // 文字大小
    public static float textAscent;
    public static float textDescent;
    public static float textLeading;
    public static int textTop;
    public static int textBottom;
    public static int textHeight; // 文字高度
    public static int textColor; // 文字颜色

    public static int topPadding;
    public static int bottomPadding;
    public static int leftPadding;
    public static int rightPadding;

    public static int segmentSpacing; // 段间距
    public static int lineSpacing; // 行间距
    public static float indent; // 首行缩进
    public static float wordSpacingMax; // 单词最大间距

    public static final Paint textPaint, debugPaint;

    static {
        textSize = SPUtil.getInt("textSize", 40);
        textColor = SPUtil.getInt("textColor", 0xFF000000);

        topPadding = SPUtil.getInt("topPadding", 80);
        bottomPadding = topPadding;
        leftPadding = SPUtil.getInt("leftPadding", 50);
        rightPadding = leftPadding;

        segmentSpacing = SPUtil.getInt("segmentSpacing", textSize / 2);
        lineSpacing = SPUtil.getInt("lineSpacing", textSize / 4);
        indent = SPUtil.getFloat("indent", textSize * 2f);
        wordSpacingMax = textSize * 0.5f;

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        debugPaint = new Paint();
        debugPaint.setColor(0x22222222);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textAscent = fontMetrics.ascent; // textAscent = textSize * -0.9277344f;
        textDescent = fontMetrics.descent; // textDescent = textSize * 0.24414062f;
        textLeading = fontMetrics.leading;
        textTop = (int) Math.ceil(fontMetrics.top); // -1.5 -> -1
        textBottom = (int) Math.floor(fontMetrics.bottom); // 1.5 -> 1
        textHeight = textBottom - textTop; // textHeight = textSize * 1.3271484f;

        int lineCount = textPaint.breakText(Config.TEXT_A, true,
                DisplayUtil.getScreenWidth() - leftPadding - rightPadding, null);
        pagePreCount = lineCount * (int) Math.ceil((DisplayUtil.getScreenHeight() - topPadding - bottomPadding) / textHeight);
        segmentPreCount = 20;
    }

}
