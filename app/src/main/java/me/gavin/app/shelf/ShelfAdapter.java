package me.gavin.app.shelf;

import android.content.Context;
import android.view.View;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import me.gavin.app.core.model.Book;
import me.gavin.base.function.BiConsumer;
import me.gavin.base.function.IntConsumer;
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ItemShelfBookBinding;

/**
 * 书架
 *
 * @author gavin.xiong 2018/5/5
 */
public class ShelfAdapter extends RecyclerAdapter<Book, ItemShelfBookBinding> {

    private IntConsumer mListener;
    private BiConsumer<View, Integer> mListenerL;

    public ShelfAdapter(Context context, List<Book> list) {
        super(context, list, R.layout.item_shelf_book);
    }

    public void setOnItemClickListener(IntConsumer onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    public void setOnItemLongClickListener(BiConsumer<View, Integer> onItemClickListener) {
        this.mListenerL = onItemClickListener;
    }

    @Override
    protected void onBind(RecyclerHolder<ItemShelfBookBinding> holder, Book t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.executePendingBindings();
        if (mListener != null) {
            holder.binding.item.setOnClickListener(v -> mListener.accept(position));
        }
        if (mListenerL != null) {
            holder.binding.item.setOnLongClickListener(v -> {

                mListenerL.accept(v, position);
                return true;
            });
        }
    }
}
