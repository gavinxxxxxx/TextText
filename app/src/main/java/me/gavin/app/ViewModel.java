package me.gavin.app;

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

    public float textSize = 30f; // 文字大小
    public float textHeight = textSize * 1.3271484f;
    public int textColor = 0xFF000000;

    public float topPadding = 0f;
    public float bottomPadding = topPadding;
    public float leftPadding = 50f;
    public float rightPadding = leftPadding;

    public float segmentSpacing = textSize * 1f; // 段间距
    public float lineSpacing = textSize * 0.0f; // 行间距
    public float indent = textSize * 2f; // 首行缩进
    public float wordSpacingMax = textSize * 2f; // 单词最大间距

}
