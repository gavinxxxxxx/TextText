package me.gavin.base.recycler;

import android.content.Context;
import android.databinding.ViewDataBinding;
import android.support.annotation.LayoutRes;

import com.android.databinding.library.baseAdapters.BR;

import java.util.List;

import me.gavin.base.function.Consumer;
import me.gavin.base.function.IntConsumer;
import me.gavin.R;

/**
 * DataBinding 基类适配器
 *
 * @author gavin.xiong 2016/12/28
 */
public class BindingAdapter<T> extends RecyclerAdapter<T, ViewDataBinding> {

    private Consumer<T> mListener;

    public BindingAdapter(Context context, List<T> list, @LayoutRes int layoutId) {
        super(context, list, layoutId);
    }

    public void setOnItemClickListener(Consumer<T> onItemClickListener) {
        this.mListener = onItemClickListener;
    }

    @Override
    protected void onBind(RecyclerHolder<ViewDataBinding> holder, T t, int position) {
        holder.binding.setVariable(BR.item, t);
        holder.binding.executePendingBindings();
        if (mListener != null) {
            holder.itemView.findViewById(R.id.item).setOnClickListener(v -> mListener.accept(t));
        }
    }
}
