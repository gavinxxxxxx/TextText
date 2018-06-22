package me.gavin.app.search;

import android.content.Context;

import java.util.List;

import me.gavin.app.core.model.Book;
import me.gavin.base.function.IntConsumer;
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.R;
import me.gavin.databinding.ItemSearchBinding;

/**
 * 搜索结果适配器
 *
 * @author gavin.xiong 2018/5/22
 */
public class SearchAdapter extends RecyclerAdapter<Book, ItemSearchBinding> {

    private IntConsumer mListener;

    public SearchAdapter(Context context, List<Book> list) {
        super(context, list, R.layout.item_search);
    }

    public void setOnItemClickListener(IntConsumer onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    @Override
    protected void onBind(RecyclerHolder<ItemSearchBinding> holder, Book t, int position) {
        holder.binding.setItem(t);
        holder.binding.executePendingBindings();
        if (mListener != null) {
            holder.itemView.findViewById(R.id.item).setOnClickListener(v -> mListener.accept(position));
        }
    }
}
