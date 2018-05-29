package me.gavin.app.core.source;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;
import org.greenrobot.greendao.annotation.Transient;

import java.io.Serializable;
import java.util.List;

import me.gavin.inject.component.ApplicationComponent;

/**
 * 自定义书源
 *
 * @author gavin.xiong 2018/5/26.
 */
@Entity
public class Source implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final int FLAG_NONE = 0; // 正常状态
    public static final int FLAG_DISABLE = 1; // 不可用
    public static final int FLAG_CHECKED = 1 << 1; // 选中
    public static final int FLAG_TOP = 1 << 2; // 置顶
    public static final int FLAG_SYS = 1 << 3; // 内置

    @Id
    public String id; // daocaorenshuwu
    public String name; // 稻草人书屋
    public String attr; // 备注
    public String host; // http://www.daocaorenshuwu.com
    public String json;
    @Transient
    private Data data;
    public int flag; // {0:可用 1：已选}

    @Generated(hash = 885735672)
    public Source(String id, String name, String attr, String host, String json, int flag) {
        this.id = id;
        this.name = name;
        this.attr = attr;
        this.host = host;
        this.json = json;
        this.flag = flag;
    }

    @Generated(hash = 615387317)
    public Source() {
    }

    public static class Data implements Serializable {
        private static final long serialVersionUID = 1L;
        public Action query;
        public Action detail;
        public Action directory;
        public Action chapter;
    }

    public static class Action implements Serializable {
        private static final long serialVersionUID = 1L;
        public String url;
        public String select;
        public int skip;
        public List<Field> fields;
    }

    public static class Field implements Serializable {
        private static final long serialVersionUID = 1L;
        public String type;
        public String select;
        public String attr;
        public String feature;
    }

    public String getId() {
        return this.id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return this.name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAttr() {
        return this.attr;
    }

    public void setAttr(String attr) {
        this.attr = attr;
    }

    public String getHost() {
        return this.host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getJson() {
        return json();
    }

    public void setJson(String json) {
        this.json = json;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    // ---------------------------------------------------------------------------------

    public boolean isChecked() {
        return (flag & FLAG_CHECKED) == FLAG_CHECKED;
    }

    public void setChecked(boolean checked) {
        flag = checked ? flag | FLAG_CHECKED : flag ^ flag & FLAG_CHECKED;
    }

    private String json() {
        if (json == null) {
            json = ApplicationComponent.Instance.get().getGson().toJson(data);
        }
        return this.json;
    }

    public Data data() {
        if (data == null)
            data = ApplicationComponent.Instance.get().getGson().fromJson(json, Data.class);
        return data;
    }
}
