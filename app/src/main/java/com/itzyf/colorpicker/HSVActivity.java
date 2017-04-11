package com.itzyf.colorpicker;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.widget.SeekBar;
import android.widget.TextView;

public class HSVActivity extends AppCompatActivity implements SeekBar.OnSeekBarChangeListener {
    private TextView mView;
    private float[] colorHSV = new float[]{0f, 1f, 1f};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hsv);

        ActionBar mActionBar = getSupportActionBar();
        mActionBar.setHomeButtonEnabled(true);
        mActionBar.setDisplayHomeAsUpEnabled(true);

        mView = (TextView) findViewById(R.id.colorView);
        SeekBar mColorH = (SeekBar) findViewById(R.id.colorH);
        SeekBar mColorS = (SeekBar) findViewById(R.id.colorS);
        SeekBar mColorV = (SeekBar) findViewById(R.id.colorV);
        mColorH.setOnSeekBarChangeListener(this);
        mColorS.setOnSeekBarChangeListener(this);
        mColorV.setOnSeekBarChangeListener(this);

        mView.setText("[ 0.0 , 1 , 1 ]\n#" + Integer.toHexString(Color.HSVToColor(colorHSV)));
        mView.setBackgroundColor(Color.HSVToColor(colorHSV));
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        switch (seekBar.getId()) {
            case R.id.colorH:
                colorHSV[0] = progress;
                break;
            case R.id.colorS:
                colorHSV[1] = (float) progress / 100;
                break;
            case R.id.colorV:
                colorHSV[2] = (float) progress / 100;
                break;
        }
        mView.setText("[ " + colorHSV[0] + " , " + colorHSV[1] + " , " + colorHSV[2] + " ]\n#" + Integer.toHexString(Color.HSVToColor(colorHSV)));

        mView.setBackgroundColor(Color.HSVToColor(colorHSV));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
