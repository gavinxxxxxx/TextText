package me.gavin.app;

import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextPaint;
import android.view.ViewConfiguration;

import me.gavin.base.App;
import me.gavin.util.DisplayUtil;
import me.gavin.util.SPUtil;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/12/18
 */
public final class Config {

    public static final String REGEX_CHAPTER = "\\s第[0-9零一二三四五六七八九十百千万]+(章|节|集|卷|部|篇|回|话)[^\\n]{0,30}"; // 章节
    public static final String REGEX_CHAPTER2 = "\\s{1}第(.{1,5})(章|节|集|卷|部|篇)"; // 章节 // 二进制?
    public static final String REGEX_SEGMENT = "\\s*\\n\\s*"; // 分段
    public static final String REGEX_SEGMENT_PREFIX = "[\\s\\S]+\\s*\\n\\s*"; // 分段 - 段前
    public static final String REGEX_SEGMENT_SUFFIX = "\\s*\\n\\s*[\\s\\S]+"; // 分段 - 段后
    public static final String REGEX_WORD = "[A-Za-z0-9\\-]+"; // 单词
    public static final String REGEX_CHARACTER = "([A-Za-z0-9\\-]+|[^A-Za-z0-9\\-])"; // 文字
    public static final String REGEX_PUNCTUATION = "[`~!@#$^&*()=|{}':;',\\[\\].<>/?~！@#￥……&*（）——|{}【】‘；：”“'。，、？]"; // 标点符号
    public static final String REGEX_PUNCTUATION_LEFT = "[({'\\[<（｛【‘“]"; // 标点符号
    public static final String REGEX_PUNCTUATION_RIGHT = "[)}'\\]>）｝】’”]"; // 标点符号
    public static final String REGEX_PUNCTUATION_N = "[`~!@#$^&*)=|}:;,\\].>/?~！@#￥……&*）——|}】‘；：”'。，、？]"; // 标点符号

    public static int width;
    public static int height;

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

    public static final Paint textPaint, bgPaint, debugPaint;

    public static final int pageCount; // 页面数量
    public static final int pageElevation; // 页高度
    public static final int touchSlop; // 滑动临界值
    public static final int flingVelocity; // 抛投临界值
    public static final float flipAnimDuration; // 翻页动画时长比例

    static {
        textSize = SPUtil.getInt("textSize", 40);
        textColor = SPUtil.getInt("textColor", 0xFFA9B7C6);

        topPadding = SPUtil.getInt("topPadding", 80);
        bottomPadding = SPUtil.getInt("bottomPadding", 50);
        leftPadding = SPUtil.getInt("leftPadding", 50);
        rightPadding = SPUtil.getInt("rightPadding", leftPadding);

        segmentSpacing = SPUtil.getInt("segmentSpacing", textSize);
        lineSpacing = SPUtil.getInt("lineSpacing", textSize / 2);
        indent = SPUtil.getFloat("indent", textSize * 2f);
        wordSpacingMax = textSize * 0.5f;

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);

        bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        bgPaint.setColor(0xFF252525);

        debugPaint = new Paint();
        debugPaint.setColor(0x22222222);

        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textAscent = fontMetrics.ascent; // textAscent = textSize * -0.9277344f;
        textDescent = fontMetrics.descent; // textDescent = textSize * 0.24414062f;
        textLeading = fontMetrics.leading;
        textTop = (int) Math.ceil(fontMetrics.top); // -1.5 -> -1
        textBottom = (int) Math.floor(fontMetrics.bottom); // 1.5 -> 1
        textHeight = textBottom - textTop; // textHeight = textSize * 1.3271484f;

        pageCount = 3;
        pageElevation = DisplayUtil.dp2px(10);
        touchSlop = ViewConfiguration.get(App.get()).getScaledTouchSlop();
        flingVelocity = ViewConfiguration.get(App.get()).getScaledMinimumFlingVelocity() * 2;
        flipAnimDuration = 0.4f;
    }

    /**
     * 尺寸变化时重新计算预加载数量
     */
    public static void applySizeChange(int w, int h) {
        width = w;
        height = h;
        segmentPreCount = (int) Math.ceil((width - leftPadding - rightPadding) / getLetterMinWidth());
        pagePreCount = segmentPreCount * (int) Math.ceil((height - topPadding - bottomPadding) / textHeight);
    }

    /**
     * 获取字母最小宽度
     */
    private static float getLetterMinWidth() {
        String[] ss = {"f", "i", "j", "l", "r", "1"};
        float min = Float.MAX_VALUE;
        for (String s : ss)
            min = Math.min(min, textPaint.measureText(s));
        return min;
    }
}
