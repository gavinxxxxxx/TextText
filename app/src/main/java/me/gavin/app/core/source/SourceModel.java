package me.gavin.app.core.source;

import java.util.List;

/**
 * 自定义书源
 *
 * @author gavin.xiong 2018/5/26.
 */
public class SourceModel {

    public String id; // daocaorenshuwu
    public String name; // 稻草人书屋
    public String attr; // 备注
    public String host; // http://www.daocaorenshuwu.com
    public String json;
    public int flag; // {0:可用 1：已选}

    public Query query;
    public Detail detail;
    public Directory directory;
    public Chapter chapter;

    public static class Query {
        public String url;
        public String select;
        public List<Field> fields;
    }

    public static class Detail {
        public String url;
        public String select;
        public List<Field> fields;
    }

    public static class Directory {
        public String url;
        public String select;
        public int skip;
        public List<Field> fields;
    }

    public static class Chapter {
        public String url;
        public String select;
    }

    public static class Field {
        public String type;
        public String select;
        public String attr;
        public String feature;
    }
}
