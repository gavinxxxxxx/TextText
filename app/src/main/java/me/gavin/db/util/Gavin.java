package me.gavin.db.util;

import org.greenrobot.greendao.annotation.Entity;
import org.greenrobot.greendao.annotation.Generated;
import org.greenrobot.greendao.annotation.Id;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2017/12/14
 */
@Entity
public class Gavin {

    @Id(autoincrement = true)
    private long id;

    @Generated(hash = 296488948)
    public Gavin(long id) {
        this.id = id;
    }

    @Generated(hash = 1168959116)
    public Gavin() {
    }

    public long getId() {
        return this.id;
    }

    public void setId(long id) {
        this.id = id;
    }

}
