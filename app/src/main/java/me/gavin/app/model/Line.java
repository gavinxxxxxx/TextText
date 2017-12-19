package me.gavin.app.model;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/12/19
 */
public class Line {

    public String text;
    public float y;

    public boolean lineIndent;
    public boolean lineAlign;

    public Line(String text, float y, boolean lineIndent, boolean lineAlign) {
        this.text = text;
        this.y = y;
        this.lineIndent = lineIndent;
        this.lineAlign = lineAlign;
    }
}
