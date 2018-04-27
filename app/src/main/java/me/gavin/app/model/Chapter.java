package me.gavin.app.model;

/**
 * 章节
 *
 * @author gavin.xiong 2018/1/1
 */
public class Chapter {

    public String id;
    public String title;
    public int index;
    public long offset;
    public boolean selected;
    public String text;

    public Chapter(long offset, String title) {
        this.offset = offset;
        this.title = title;
        this.selected = false;
    }

    public Chapter(String id, String title) {
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "offset=" + offset +
                ", title='" + title + '\'' +
                '}';
    }
}
