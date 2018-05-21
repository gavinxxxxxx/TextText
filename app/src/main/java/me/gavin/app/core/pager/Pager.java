package me.gavin.app.core.pager;

import me.gavin.app.core.model.Page;

/**
 * 分页器
 *
 * @author gavin.xiong 2018/4/27
 */
public interface Pager {

    void curr();

    void last(Page target, Page page);

    void next(Page target, Page page);

    void onFlip(Page page);
}
