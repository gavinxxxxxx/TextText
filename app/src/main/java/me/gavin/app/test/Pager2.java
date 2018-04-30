package me.gavin.app.test;

import me.gavin.app.model.Page;

/**
 * 分页器
 *
 * @author gavin.xiong 2018/4/27
 */
public interface Pager2 {

    void curr();

    void last();

    void next();

    void onFilped(Page page);
}
