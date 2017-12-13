package me.gavin.text;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        TextTextView text = findViewById(R.id.text);
        text.setText(AssetsUtils.readText(this, "zx.txt"));
    }

}
