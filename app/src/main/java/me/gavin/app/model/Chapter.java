package me.gavin.app.model;

/**
 * 章节
 *
 * @author gavin.xiong 2018/1/1
 */
public class Chapter {

    public String id;
    public String title;
    public long offset;
    public boolean selected;

    public Chapter(long offset, String title) {
        this.offset = offset;
        this.title = title;
        this.selected = false;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "offset=" + offset +
                ", title='" + title + '\'' +
                '}';
    }
}
