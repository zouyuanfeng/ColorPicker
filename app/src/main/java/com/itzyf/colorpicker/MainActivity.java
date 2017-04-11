package com.itzyf.colorpicker;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private ColorView mColorView;
    private ImageView mView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_color);

        mView = (ImageView) findViewById(R.id.imageView);
        mColorView = (ColorView) findViewById(R.id.colorView);
        mColorView.setOnSelectColorListener(new ColorView.onSelectColorListener() {
            @Override
            public void onSelectColor(@ColorInt int color) {
                mView.setBackgroundColor(color);
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_color) {
            startActivity(new Intent(this, HSVActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
