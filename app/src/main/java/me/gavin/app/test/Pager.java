package me.gavin.app.test;

import java.io.IOException;

import me.gavin.app.model.Page;

/**
 * 分页器
 *
 * @author gavin.xiong 2018/4/27
 */
public abstract class Pager {

    TextView mView;

    public abstract void offset(Long offset);

    public abstract void offset(boolean reserve);

    protected abstract Page offset(long offset, boolean reserve) throws IOException;
}
