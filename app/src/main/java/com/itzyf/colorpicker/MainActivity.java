package com.itzyf.colorpicker;

import android.os.Bundle;
import android.support.annotation.ColorInt;
import android.support.v7.app.AppCompatActivity;
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

}
