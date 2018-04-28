package me.gavin.app.model;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 章节
 *
 * @author gavin.xiong 2018/1/1
 */
@Entity
public class Chapter {

    @Id(autoincrement = true)
    public Long _id;
    public String id;
    public String bookId;
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

    @Generated(hash = 837958929)
    public Chapter(Long _id, String id, String bookId, String title, int index,
            long offset, boolean selected, String text) {
        this._id = _id;
        this.id = id;
        this.bookId = bookId;
        this.title = title;
        this.index = index;
        this.offset = offset;
        this.selected = selected;
        this.text = text;
    }

    @Generated(hash = 393170288)
    public Chapter() {
    }

    @Override
    public String toString() {
        return "Chapter{" +
                "offset=" + offset +
                ", title='" + title + '\'' +
                '}';
    }

    public Long get_id() {
        return this._id;
    }

    public void set_id(Long _id) {
        this._id = _id;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return this.title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getIndex() {
        return this.index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public long getOffset() {
        return this.offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public boolean getSelected() {
        return this.selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }

    public String getText() {
        return this.text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getBookId() {
        return this.bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }
}
