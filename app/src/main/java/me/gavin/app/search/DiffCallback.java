package me.gavin.app.search;

import android.support.v7.util.DiffUtil;

import java.util.List;

import me.gavin.app.core.model.Book;

/**
 * DiffCallback
 *
 * @author gavin.xiong 2017/12/1
 */
public class DiffCallback extends DiffUtil.Callback {

    private final List<Book> mOldList, mNewList;

    DiffCallback(List<Book> oldList, List<Book> newList) {
        this.mOldList = oldList;
        this.mNewList = newList;
    }

    @Override
    public int getOldListSize() {
        return mOldList.size();
    }

    @Override
    public int getNewListSize() {
        return mNewList.size();
    }

    @Override
    public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).name.equals(mNewList.get(newItemPosition).name)
                && mOldList.get(oldItemPosition).author.equals(mNewList.get(newItemPosition).author);
    }

    @Override
    public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
        return mOldList.get(oldItemPosition).srcs.equals(mNewList.get(newItemPosition).srcs);
    }
}
