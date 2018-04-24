package me.gavin.app.test;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.View;

import java.util.LinkedList;
import java.util.List;

import me.gavin.util.DisplayUtil;

/**
 * 自定义文本框
 *
 * @author gavin.xiong 2018/4/24
 */
public class ITextView extends View {

    private static final String END = "…";

    private boolean mSingleLine;

    private int hSpace; // 横向间隔
    private int vSpace; // 纵向间隔

    private final List<IText> mTexts; // 文本列表
    private final TextPaint mBPaint, mRPaint; // 红黑画笔
    private float mTextHeight; // 文本高度
    private float mBaseY;

    public ITextView(Context context) {
        this(context, null);
    }

    public ITextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        if (attrs != null) {
            int index = -1;
            for (int i = 0; i < attrs.getAttributeCount(); i++) {
                if ("singleLine".equals(attrs.getAttributeName(i))) {
                    index = i;
                    break;
                }
            }
            if (index >= 0) {
                mSingleLine = attrs.getAttributeBooleanValue(index, false);
            }
        }
        mTexts = new LinkedList<>();

        mBPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mBPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, getResources().getDisplayMetrics()));
        mBPaint.setColor(0xFF000000);
        mBPaint.setStyle(Paint.Style.FILL);
        Paint.FontMetrics fm = mBPaint.getFontMetrics();
        mTextHeight = fm.bottom - fm.top;
        mBaseY = mTextHeight / 2 - mBPaint.descent() / 2 - mBPaint.ascent() / 2;

        mRPaint = new TextPaint(mBPaint);
        mRPaint.setColor(0xFFF55C54);

        hSpace = DisplayUtil.dp2px(15);
        vSpace = DisplayUtil.dp2px(14);

        if (isInEditMode()) {
            for (int i = 0; i < 15; i++) {
                int count = (int) (Math.random() * 7) + 1;
                String s = "";
                for (int j = 0; j < count; j++) {
                    s += "哈";
                }
                mTexts.add(new IText(i % 3 - 1, s));
            }
        }
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);

        int cw = widthSize - getPaddingLeft() - getPaddingRight();

        float height = 0;
        float lineWidth = 0;

        for (int i = 0; i < mTexts.size(); i++) {
            IText text = mTexts.get(i);

            if (text == null || text.text == null || text.text.isEmpty()) {
                continue;
            }

            float childWidth = mBPaint.measureText(text.text);

            if (lineWidth + hSpace + childWidth <= cw) { // 加上空格和内容放得下
                text.x = lineWidth == 0 ? 0 : lineWidth + hSpace; // center
                text.y = height; // top
                lineWidth += lineWidth == 0 ? childWidth : hSpace + childWidth;
            } else if (lineWidth == 0) { // 放不下且是本行第一个
                text.x = 0; // center
                text.y = height; // top
                lineWidth = cw;
            } else { // 放不下且非第一个
                height += mTextHeight + vSpace;
                lineWidth = Math.min(cw, childWidth);
                text.x = 0; // center
                text.y = height; // top
            }

            if (i == mTexts.size() - 1) { // 如果是最后一行
                height += mTextHeight;
            }
        }

        if (mSingleLine) {
            setMeasuredDimension(widthSize, (int) Math.ceil(mTextHeight));
        } else {
            setMeasuredDimension(widthSize, (int) Math.ceil(height + getPaddingTop() + getPaddingBottom()));
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (!mSingleLine) { // 非单行模式
            for (IText text : mTexts) {
                canvas.drawText(text.text, getPaddingLeft() + text.x,
                        getPaddingTop() + text.y + mBaseY,
                        text.type == IText.TYPE_RED ? mRPaint : mBPaint);
                if (text.type == IText.TYPE_STRIKETHROUGH) {
                    float textW = mBPaint.measureText(text.text);
                    canvas.drawRect(text.x, text.y + mTextHeight / 2 - 3,
                            text.x + textW, text.y + mTextHeight / 2 + 3, mRPaint);
                }
            }
            // todo 非单行文本单行显示不全补省略
            return;
        }
        // 单行模式
        for (int i = 0; i < mTexts.size(); i++) {
            IText text = mTexts.get(i); // 第一个必定 y = 0
            if (i == mTexts.size() - 1 || mTexts.get(i + 1).y == 0) {
                canvas.drawText(text.text, getPaddingLeft() + text.x,
                        getPaddingTop() + text.y + mBaseY,
                        text.type == IText.TYPE_RED ? mRPaint : mBPaint);
                // todo 下一行单字且正好放下 在加上省略号后放不下了
                continue;
            }

            // 即将换行
            float endW = mBPaint.measureText(END); // 省略号宽度
            float remainW = getWidth() - getPaddingLeft() - getPaddingRight() - text.x; // 剩余宽度
            float thisW = mBPaint.measureText(text.text); // 当前文本宽度
            if (thisW + hSpace + endW >= remainW) { // 占满空间 - 末尾省略号
                if (thisW + endW <= remainW) { // 整个文本才占满空间
                    canvas.drawText(text.text + END, getPaddingLeft() + text.x,
                            getPaddingTop() + text.y + mBaseY,
                            text.type == IText.TYPE_RED ? mRPaint : mBPaint);
                } else { // 文本已放不下
                    int count = mBPaint.breakText(text.text, true, remainW - endW, null);
                    canvas.drawText(text.text.substring(0, count) + END,
                            getPaddingLeft() + text.x, getPaddingTop() + text.y + mBaseY,
                            text.type == IText.TYPE_RED ? mRPaint : mBPaint);
                }
            } else { // 还有下一个的空间
                float nextRemainW = remainW - thisW - hSpace; // 放完这个文本和间隔后剩余空间
                IText next = mTexts.get(i + 1);
                int count = mBPaint.breakText(next.text, true,  // 剩余空间出去省略号能放多少个下文本
                        nextRemainW - endW, null);
                if (count <= 0) { // 一个都放不了
                    canvas.drawText(text.text + END, getPaddingLeft() + text.x,
                            getPaddingTop() + text.y + mBaseY,
                            text.type == IText.TYPE_RED ? mRPaint : mBPaint);
                } else { // 能放最少一个
                    canvas.drawText(text.text, getPaddingLeft() + text.x,
                            getPaddingTop() + text.y + mBaseY,
                            text.type == IText.TYPE_RED ? mRPaint : mBPaint);
                    float x = text.x + thisW + hSpace;
                    canvas.drawText(next.text.substring(0, count) + END, getPaddingLeft() + x,
                            getPaddingTop() + text.y + mBaseY,
                            next.type == IText.TYPE_RED ? mRPaint : mBPaint);
                }
            }
            break;
        }
    }
}