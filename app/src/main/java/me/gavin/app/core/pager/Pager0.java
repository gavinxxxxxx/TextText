package me.gavin.app.core.pager;

import java.io.IOException;

import me.gavin.app.core.model.Page;
import me.gavin.app.core.TextView;

/**
 * 分页器
 *
 * @author gavin.xiong 2018/4/27
 */
public abstract class Pager0 {

    TextView mView;

    public abstract void offset(Long offset);

    public abstract void offset(boolean reserve);

    protected abstract Page offset(long offset, boolean reserve) throws IOException;
}
