package me.gavin.app.read;

import android.content.Context;

import java.util.List;

import me.gavin.app.model.Page;
import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ItemTextBinding;

public class PageAdapter extends RecyclerAdapter<Page, ItemTextBinding> {

    PageAdapter(Context context, List<Page> list) {
        super(context, list, R.layout.item_text);
    }

    @Override
    protected void onBind(RecyclerHolder<ItemTextBinding> holder, Page t, int position) {
        holder.binding.text.set(t);
    }
}