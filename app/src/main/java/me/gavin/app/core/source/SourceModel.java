package me.gavin.app.core.source;

import java.io.Serializable;
import java.util.List;

/**
 * 自定义书源
 *
 * @author gavin.xiong 2018/5/26.
 */
public class SourceModel implements Serializable {

    public static final int FLAG_NONE = 0; // 正常状态
    public static final int FLAG_ENABLE = 1; // 可用
    public static final int FLAG_CHECKED = 1 << 1; // 选中
    public static final int FLAG_TOP = 1 << 2; // 置顶
    public static final int FLAG_SYS = 1 << 3; // 内置

    public String id; // daocaorenshuwu
    public String name; // 稻草人书屋
    public String attr; // 备注
    public String host; // http://www.daocaorenshuwu.com
    public String json;
    public Data data;
    public int flag; // {0:可用 1：已选}

    public static class Data implements Serializable {
        public Action query;
        public Action detail;
        public Action directory;
        public Action chapter;
    }

    public static class Action implements Serializable {
        public String url;
        public String select;
        public int skip;
        public List<Field> fields;
    }

    public static class Field implements Serializable {
        public String type;
        public String select;
        public String attr;
        public String feature;
    }
}
