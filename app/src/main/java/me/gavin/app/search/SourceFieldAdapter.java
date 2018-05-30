package me.gavin.app.search;

import android.content.Context;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;

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
public class SourceFieldAdapter extends RecyclerAdapter<Source.Field, ItemSourceFieldBinding> implements TextWatcher {

    public SourceFieldAdapter(Context context, List<Source.Field> list) {
        super(context, list, R.layout.item_source_field);
    }

    @Override
    protected void onBind(RecyclerHolder<ItemSourceFieldBinding> holder, Source.Field t, int position) {
        holder.binding.setItem(t);
        holder.binding.executePendingBindings();
        holder.binding.etSel.addTextChangedListener(this);
        holder.binding.etAttr.addTextChangedListener(this);
        holder.binding.etFeature.addTextChangedListener(this);
        holder.binding.etType.addTextChangedListener(this);
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {

    }

    @Override
    public void afterTextChanged(Editable s) {
        boolean flag = false;
        for (Source.Field t : mList) {
            flag = TextUtils.isEmpty(t.type) && TextUtils.isEmpty(t.select)
                    && TextUtils.isEmpty(t.attr) && TextUtils.isEmpty(t.feature);
            if (flag) break;
        }
        if (!flag) {
            mList.add(new Source.Field());
            notifyItemInserted(mList.size() - 1);
        }
    }
}
