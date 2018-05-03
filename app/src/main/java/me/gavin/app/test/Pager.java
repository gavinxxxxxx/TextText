package me.gavin.app.test;

import me.gavin.app.model.Page;

/**
 * 分页器
 *
 * @author gavin.xiong 2018/4/27
 */
public interface Pager {

    void curr();

    void last(Page target, Page page);

    void next(Page target, Page page);
}
