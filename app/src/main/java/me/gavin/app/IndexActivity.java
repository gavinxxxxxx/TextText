package me.gavin.app;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import me.gavin.app.shelf.ShelfActivity;

/**
 * Index
 *
 * @author gavin.xiong 2018/1/4
 */
public class IndexActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        startActivity(new Intent(this, ReadActivity.class)
//                .setData(Uri.parse("file:///storage/emulated/0/gavin/book/dpcq.a.txt")));
        startActivity(new Intent(this, ShelfActivity.class));
        finish();
    }
}
