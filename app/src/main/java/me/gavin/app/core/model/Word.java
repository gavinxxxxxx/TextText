package me.gavin.app.core.model;

import android.graphics.Canvas;

import me.gavin.app.Config;

/**
 * 字词
 *
 * @author gavin.xiong 2018/4/23
 */
public class Word {

    public String text;
    public float x;
    public float y;

    public Word(String text, float x, float y) {
        this.text = text;
        this.x = x;
        this.y = y;
    }

    public void draw(Canvas canvas, float offsetX, float offsetY) {
        canvas.drawText(text, x + offsetX, y + offsetY, Config.textPaint);
    }
}
