package me.gavin.text;

/**
 * ViewModel
 *
 * @author gavin.xiong 2017/12/14
 */
public final class ViewModel {

    public String regex = "[\\s　]*\\n[\\s　]*";

    public float textSize = 40f; // 文字大小
    public float textHeight = textSize * 1.3271484f;
    public int textColor = 0xFF000000;

    public float topPadding = 0f;
    public float bottomPadding = topPadding;
    public float leftPadding = 50f;
    public float rightPadding = leftPadding;

    public float segmentSpacing = 50f; // 段间距
    public float lineSpacing = 10f; // 行间距
    public float indent = textSize * 1.5f; // 首行缩进

}
