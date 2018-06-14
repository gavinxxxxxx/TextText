package me.gavin.app.core.model;

/**
 * 章节
 *
 * @author gavin.xiong 2018/1/1
 */
public class Chapter {

    public String id;
    public String bookId;
    public String title;
    public String url;
    public int index;
    public long offset;
    public boolean selected;
    public String text;

    public Chapter() {
    }

    public Chapter(long offset, String title) {
        this.offset = offset;
        this.title = title;
        this.selected = false;
    }

    public Chapter(String bookId, String id, String title) {
        this.bookId = bookId;
        this.id = id;
        this.title = title;
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "id='" + id + '\'' +
                ", title='" + title + '\'' +
                '}';
    }
}
