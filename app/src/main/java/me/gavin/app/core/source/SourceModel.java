package me.gavin.app.core.source;

import java.util.List;

/**
 * 自定义书源
 *
 * @author gavin.xiong 2018/5/26.
 */
public class SourceModel {

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
    public int flag; // {0:可用 1：已选}

    public String queryUrl;
    public String querySelect;
    public List<Field> queryFields;

    public String detailUrl;
    public String detailSelect;
    public List<Field> detailFields;

    public String directoryUrl;
    public String directorySelect;
    public int directorySkip;
    public List<Field> directoryFields;

    public String chapterUrl;
    public String chapterSelect;

    public static class Field {
        public String type;
        public String select;
        public String attr;
        public String feature;
    }
}
