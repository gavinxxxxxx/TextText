package me.gavin.app.core.source;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 书源
 *
 * @author gavin.xiong 2018/4/28.
 */
@Entity
public class Source {

    public static final int FLAG_NONE = 0; // 正常状态
    public static final int FLAG_ENABLE = 1; // 可用
    public static final int FLAG_CHECKED = 1 << 1; // 选中

    @Id
    public String id; // daocaorenshuwu
    public String name; // 稻草人书屋
    public String attr; // 备注
    public String url; // http://www.daocaorenshuwu.com
    public int flag; // {0:可用 1：已选}

    @Generated(hash = 126208573)
    public Source(String id, String name, String attr, String url, int flag) {
        this.id = id;
        this.name = name;
        this.attr = attr;
        this.url = url;
        this.flag = flag;
    }

    @Generated(hash = 615387317)
    public Source() {
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

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getFlag() {
        return this.flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }

    public boolean isChecked() {
        return (flag & FLAG_CHECKED) == FLAG_CHECKED;
    }

    public void setChecked(boolean checked) {
        flag = checked ? flag | FLAG_CHECKED : flag ^ flag & FLAG_CHECKED;
    }

    @Override
    public String toString() {
        return "Source{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", attr='" + attr + '\'' +
                ", url='" + url + '\'' +
                ", flag=" + flag +
                '}';
    }
}
