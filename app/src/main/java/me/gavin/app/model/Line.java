package me.gavin.app.model;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/12/19
 */
public class Line {

    public String src;
    public String suffix;
    public String text;
    public float y;

    public boolean lineIndent;
    public boolean lineAlign;

    public Line(String src, String suffix, float y, boolean lineIndent, boolean lineAlign) {
        this.src = src;
        this.suffix = suffix;
        this.text = src + suffix;
        this.y = y;
        this.lineIndent = lineIndent;
        this.lineAlign = lineAlign;
    }
}
