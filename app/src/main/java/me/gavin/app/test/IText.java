package me.gavin.app.test;

/**
 * 自定义文本框 model
 *
 * @author gavin.xiong 2018/4/23
 */
public class IText {

    public static final int TYPE_NORMAL = 0; // 正常文字
    public static final int TYPE_RED = 1; // 红色文字
    public static final int TYPE_STRIKETHROUGH = -1; // 删除线

    public int type;
    public String text;

    public IText(int type, String text) {
        this.type = type;
        this.text = text;
    }
}
