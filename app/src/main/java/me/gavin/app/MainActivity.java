package me.gavin.app;

import android.content.Context;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

import me.gavin.base.recycler.RecyclerAdapter;
import me.gavin.base.recycler.RecyclerHolder;
import me.gavin.text.R;
import me.gavin.text.databinding.ActivityMainBinding;
import me.gavin.text.databinding.ItemTextBinding;

public class MainActivity extends AppCompatActivity {

    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);

        List<String> list = new ArrayList<>();
        for (int i = 0; i < 30; i++) {
            list.add(String.valueOf(i));
        }
        mBinding.recycler.setAdapter(new Adapter(this, list));

//        setContentView(R.layout.item_text);
//        TextTextView text = findViewById(R.id.text);
//        text.setText(AssetsUtils.readText(this, "test.txt"));
    }


    public static class Adapter extends RecyclerAdapter<String, ItemTextBinding> {

        public Adapter(Context context, List<String> list) {
            super(context, list, R.layout.item_text);
        }

        @Override
        protected void onBind(RecyclerHolder<ItemTextBinding> holder, String s, int position) {
            holder.binding.text.setText(AssetsUtils.readText(mContext, "test.txt", 200), 0);
        }
    }
}
