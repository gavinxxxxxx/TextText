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
 * @author gavin.xiong 2018/4/23
 */
public class ITextView extends View {

    private int hSpace; // 横向间隔
    private int vSpace; // 纵向间隔

    private final List<IText> mTexts; // 文本列表
    private final TextPaint mBPaint, mRPaint; // 红黑画笔
    private float mTextHeight; // 文本高度

    public ITextView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        mTexts = new LinkedList<>();

        mBPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mBPaint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP,
                14, getResources().getDisplayMetrics()));
        mBPaint.setColor(0xFF000000);
        mBPaint.setStyle(Paint.Style.FILL);
        mBPaint.setTextAlign(Paint.Align.CENTER);
        Paint.FontMetrics fm = mBPaint.getFontMetrics();
        mTextHeight = fm.bottom - fm.top;

        mRPaint = new TextPaint(mBPaint);
        mRPaint.setColor(0xFFF55C54);

        hSpace = DisplayUtil.dp2px(15);
        vSpace = DisplayUtil.dp2px(14);

        if (isInEditMode()) {
            for (int i = 0; i < 5; i++) {
                int count = (int) (Math.random() * 7) + 1;
                String s = "";
                for (int j = 0; j < count; j++) {
                    s += "哈";
                }
                mTexts.add(new IText(i % 3 - 1, s));
            }
        }
    }

    public void set(List<IText> texts) {
        mTexts.clear();
        mTexts.addAll(texts);
        requestLayout();
    }

    public ITextView append(IText iText) {
        mTexts.add(iText);
        requestLayout();
        return this;
    }

    public ITextView append(List<IText> texts) {
        mTexts.addAll(texts);
        requestLayout();
        return this;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        float width = 0, height = 0;
        float lineWidth = 0;

        for (int i = 0; i < mTexts.size(); i++) {
            IText text = mTexts.get(i);

            if (text == null || text.text == null || text.text.isEmpty()) {
                continue;
            }

            float childWidth = mBPaint.measureText(text.text);

            if (lineWidth + hSpace + childWidth <= widthSize - getPaddingLeft() - getPaddingRight()) { // 加上空格和内容放得下
                lineWidth += lineWidth == 0 ? childWidth : hSpace + childWidth;
            } else if (lineWidth == 0) { // 放不下且是本行第一个
                lineWidth = Math.min(childWidth, widthSize - getPaddingLeft() - getPaddingRight());
            } else { // 放不下且非第一个
                width = Math.max(width, lineWidth);
                height += mTextHeight + vSpace;
                lineWidth = childWidth;
            }

            // 如果是最后一行
            if (i == mTexts.size() - 1) {
                width = Math.max(width, lineWidth);
                height += mTextHeight;
            }
        }

        int mWidth = widthMode == MeasureSpec.EXACTLY ? widthSize : Math.min(Math.round(width + getPaddingLeft() + getPaddingRight()), widthSize);
        int mHeight = heightMode == MeasureSpec.EXACTLY ? heightSize : Math.round(height + getPaddingTop() + getPaddingBottom());
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        float left = getPaddingLeft();
        float top = getPaddingTop();

        float lineWidth = 0;

        for (int i = 0; i < mTexts.size(); i++) {
            IText text = mTexts.get(i);

            if (text == null || text.text == null || text.text.isEmpty()) {
                continue;
            }

            float childWidth = mBPaint.measureText(text.text);

            if (lineWidth + hSpace + childWidth <= getWidth() - getPaddingLeft() - getPaddingRight()) { // 加上空格和内容放得下
                lineWidth += lineWidth == 0 ? childWidth : hSpace + childWidth;
            } else if (lineWidth == 0) { // 放不下且是本行第一个
                lineWidth = Math.min(childWidth, getWidth() - getPaddingLeft() - getPaddingRight());
                childWidth = getWidth() - getPaddingLeft() - getPaddingRight();
            } else { // 放不下且非第一个
                left = getPaddingLeft();
                top += vSpace + mTextHeight;

                lineWidth = childWidth;
            }

            float baseY = (int) (top + mTextHeight / 2 - mBPaint.descent() / 2 - mBPaint.ascent() / 2);
            int count = mBPaint.breakText(text.text, true, childWidth, null);
            String s = count == text.text.length() ? text.text : text.text.substring(0, count - 1) + "…";
            float w = mBPaint.measureText(s);
            canvas.drawText(s, left + w / 2, baseY, text.type == IText.TYPE_RED ? mRPaint : mBPaint);

            if (text.type == IText.TYPE_STRIKETHROUGH) {
                canvas.drawRect(left, top + mTextHeight / 2 - 2.5f, left + w, top + mTextHeight / 2 + 2.5f, mRPaint);
            }

            left += hSpace + childWidth;
        }
    }
}
