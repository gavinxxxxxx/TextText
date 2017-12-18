package me.gavin.app;

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

}
