package me.gavin.app.search;

import android.content.Context;

import java.util.List;

import me.gavin.app.core.source.Source;
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ItemSourceFieldBinding;

/**
 * 书籍属性
 *
 * @author gavin.xiong 2018/5/30
 */
public class SourceFieldAdapter extends RecyclerAdapter<Source.Field, ItemSourceFieldBinding> {

    public SourceFieldAdapter(Context context, List<Source.Field> list) {
        super(context, list, R.layout.item_source_field);
    }

    @Override
    protected void onBind(RecyclerHolder<ItemSourceFieldBinding> holder, Source.Field t, int position) {
        holder.binding.setItem(t);
        holder.binding.executePendingBindings();
    }
}
