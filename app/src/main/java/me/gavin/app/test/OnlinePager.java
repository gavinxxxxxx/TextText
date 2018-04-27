package me.gavin.app.test;

import java.io.IOException;

import me.gavin.app.model.Page;

/**
 * 在线 - 分页
 *
 * @author gavin.xiong 2018/4/27.
 */
public class OnlinePager extends Pager {

    @Override
    public void offset(Long offset) {

    }

    @Override
    public void offset(boolean reserve) {

    }

    @Override
    protected Page offset(long offset, boolean reserve) throws IOException {
        return null;
    }
}
