package me.gavin.app.explorer;

import android.content.Context;

import java.util.List;

import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ItemExplorerBinding;

/**
 * 这里是萌萌哒注释君
 *
 * @author gavin.xiong 2018/1/2
 */
public class ExplorerAdapter extends RecyclerAdapter<FileItem, ItemExplorerBinding> {

    public ExplorerAdapter(Context context, List<FileItem> list) {
        super(context, list, R.layout.item_explorer);
    }

    @Override
    protected void onBind(RecyclerHolder<ItemExplorerBinding> holder, FileItem t, int position) {
        holder.binding.tvName.setText(t.getName());
        holder.binding.tvSub.setText(t.getSub());
        holder.binding.tvTime.setText(t.getTime());
    }
}
