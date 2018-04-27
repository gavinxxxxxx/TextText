package me.gavin.app.model;

import java.util.List;

/**
 * 这里是萌萌哒注释菌
 *
 * @author gavin.xiong 2018/4/28.
 */
public class Book2 {

    private String id;
    private String name;
    private String author;
    private String cover;
    private String source;
    private List<Chapter> directory;

    public String getChapterPath() {
        return String.format(".Books/%s(%s)/%s/%s", name, author, "srcName", "cid");
    }
}
