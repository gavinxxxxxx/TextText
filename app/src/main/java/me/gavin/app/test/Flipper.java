package me.gavin.app.test;

import android.graphics.Canvas;
import android.view.MotionEvent;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import me.gavin.app.Config;
import me.gavin.app.model.Line;
import me.gavin.app.model.Page;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/22.
 */
public abstract class Flipper {

    TextView mView;

    final Page[] mPages = new Page[Config.pageCount];

    public void attach(TextView view) {
        this.mView = view;
        view.setFlipper(this);
    }

    public void set(Page last, Page curr, Page next) {
        mPages[0] = last;
        mPages[1] = curr;
        mPages[2] = next;
        if (mView != null) {
            mView.invalidate();
        }
    }

    public abstract boolean onTouchEvent(MotionEvent event);

    public abstract void onDraw(Canvas canvas);

    /**
     * 显示文字
     *
     * @param canvas 画布
     * @param line   单行文字 & 当前行y坐标 & 缩进 & 分散对齐
     */
    void drawLine(Canvas canvas, Line line, float offsetX, float offsetY) {
        float indent = line.lineIndent ? Config.indent : 0;
        if (line.lineAlign || line.text.length() <= 1) { // 不需要分散对齐 | 只有一个字符
            canvas.drawText(line.text, Config.leftPadding + indent + offsetX, line.y + offsetY, Config.textPaint);
            return;
        }

        float textWidth = Config.textPaint.measureText(line.text);
        float lineWidth = Config.width - Config.leftPadding - Config.rightPadding - indent;
        float extraSpace = lineWidth - textWidth; // 剩余空间
        if (extraSpace <= 0) { // 没有多余空间 - 不需要分散对齐
            canvas.drawText(line.text, Config.leftPadding + indent + offsetX, line.y + offsetY, Config.textPaint);
            return;
        }

        Matcher matcher = Pattern.compile(Config.REGEX_CHARACTER).matcher(line.text);
        int workCount = 0;
        while (matcher.find()) {
            workCount++;
        }
        if (workCount > 1) { // 多个单词 - 词间距
            float workSpacing = extraSpace / (workCount - 1);
            float startX = Config.leftPadding + indent;
            float x;
            StringBuilder sb = new StringBuilder();
            int spacingCount = 0;
            matcher.reset();
            while (matcher.find()) {
                String word = matcher.group();
                x = startX + Config.textPaint.measureText(sb.toString()) + workSpacing * spacingCount;
                canvas.drawText(word, x + offsetX, line.y + offsetY, Config.textPaint);
                sb.append(word);
                spacingCount++;
            }
        } else { // 单个单词 - 字间距
            float workSpacing = extraSpace / (line.text.length() - 1);
            float startX = Config.leftPadding + indent;
            float x;
            for (int i = 0; i < line.text.length(); i++) {
                String word = String.valueOf(line.text.charAt(i));
                x = startX + Config.textPaint.measureText(line.text.substring(0, i)) + workSpacing * i;
                canvas.drawText(word, x + offsetX, line.y + offsetY, Config.textPaint);
            }
        }
    }
}
