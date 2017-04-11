package com.itzyf.colorpicker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;
import android.support.annotation.ColorInt;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * @author 依风听雨
 * @version 创建时间：2017/4/7 17:16
 */

public class ColorView extends View {
    private Context mContext;
    private LinearGradient linearGradient = null;
    private Paint mHuePaint = null;
    private Paint mSaturationPaint = null;
    private RectF mHueRectF = null;
    private RectF mSaturationRectF = null;
    private int mWidth;

    private Paint mSwipePaint;
    private Bitmap mSwipeBitmap;

    private onSelectColorListener mOnSelectColorListener;

    private int mColorHeight;
    private float mSwipeRadius;
    private int marginTopAndBottom;

    private float[] colorHSV = new float[]{0f, 1f, 0f};
    /**
     * 滑块的圆心x
     */
    private float mSwipeHueCx = 0;
    private float mSwipeSatCx = 0;

    public ColorView(Context context) {
        super(context);

        init(context);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ColorView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        this.mContext = context;
        mHuePaint = new Paint();
        mSaturationPaint = new Paint();
        mSwipePaint = new Paint();
        mSwipePaint.setAntiAlias(true);
        mSwipeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.swipe);
        mSwipeRadius = lastSatX = mSwipeBitmap.getWidth() / 2;

        mColorHeight = dp2px(10);
        marginTopAndBottom = dp2px(10);
    }


    public int dp2px(float dpVal) {
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                dpVal, mContext.getResources().getDisplayMetrics());
    }

    public float px2dp(int pxVal) {
        final float scale = mContext.getResources().getDisplayMetrics().density;
        return (pxVal / scale);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        //绘制色相选择区域
        drawHuePanel(canvas);
        //绘制饱和度颜色条
        drawSaturationPanel(canvas);
    }

    private int[] buildHueColorArray() {
        int[] hue = new int[361];
        for (int i = 0; i < hue.length; i++) {
            hue[i] = Color.HSVToColor(new float[]{i, 1f, 1f});
        }
        return hue;
    }

    /**
     * 绘制色相选择区域
     *
     * @param canvas
     */
    private void drawHuePanel(Canvas canvas) {
        //绘制颜色条
        if (mHueRectF == null)
            mHueRectF = new RectF(mSwipeRadius, mSwipeRadius - mColorHeight / 2 + marginTopAndBottom, mWidth - mSwipeRadius, mSwipeRadius + mColorHeight / 2 + marginTopAndBottom);
        if (linearGradient == null) {
            linearGradient = new LinearGradient(mHueRectF.left, mHueRectF.top, mHueRectF.right, mHueRectF.top, buildHueColorArray(), null,
                    Shader.TileMode.CLAMP);
            //设置渲染器
            mHuePaint.setShader(linearGradient);
        }
        canvas.drawRoundRect(mHueRectF, 15, 15, mHuePaint);
        //绘制滑块
        if (mSwipeHueCx < mSwipeRadius)
            mSwipeHueCx = mSwipeRadius;
        else if (mSwipeHueCx > mWidth - mSwipeRadius)
            mSwipeHueCx = mWidth - mSwipeRadius;
        canvas.drawBitmap(mSwipeBitmap, mSwipeHueCx - mSwipeRadius, marginTopAndBottom, mSwipePaint);
    }

    /**
     * 饱和度数组
     *
     * @return
     */
    private int[] buildSatColorArray() {
        int[] sat = new int[11];
        for (int i = 0; i < sat.length; i++) {
            sat[i] = Color.HSVToColor(new float[]{colorHSV[0], 1f, (float) i / 10});
        }
        return sat;
    }

    /**
     * 绘制饱和度选择区域
     *
     * @param canvas
     */
    private void drawSaturationPanel(Canvas canvas) {
        if (mSaturationRectF == null)
            mSaturationRectF = new RectF(mSwipeRadius, mSwipeRadius - mColorHeight / 2 + 3 * mSwipeRadius + marginTopAndBottom,
                    mWidth - mSwipeRadius, mSwipeRadius + mColorHeight / 2 + 3 * mSwipeRadius + marginTopAndBottom);
        final RectF rect = mSaturationRectF;

        //饱和度线性渲染器
        LinearGradient mSaturationShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top,
                buildSatColorArray(), null, Shader.TileMode.CLAMP);

        mSaturationPaint.setShader(mSaturationShader);

        canvas.drawRoundRect(mSaturationRectF, 15, 15, mSaturationPaint);
        //绘制滑块
        if (mSwipeSatCx < mSwipeRadius)
            mSwipeSatCx = mSwipeRadius;
        else if (mSwipeSatCx > mWidth - mSwipeRadius)
            mSwipeSatCx = mWidth - mSwipeRadius;
        canvas.drawBitmap(mSwipeBitmap, mSwipeSatCx - mSwipeRadius, 3 * mSwipeRadius + marginTopAndBottom, mSwipePaint);

    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int measureWidth = getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec);
        setMeasuredDimension(measureWidth,
                measureHeight(heightMeasureSpec));
        mWidth = measureWidth;
    }


    private int measureHeight(int heightMeasureSpec) {
        int result = (int) (5 * mSwipeRadius + 2 * marginTopAndBottom);
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY && specSize < result) {
            throw new IllegalArgumentException("Height is too small to display completely , the height needs to be greater than " + px2dp(result) + "dp !");
        }
        return result;
    }

    private int clickPanel = -1;
    private float lastSatX;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        if (x < mSwipeRadius || x > mWidth - mSwipeRadius)
            return super.onTouchEvent(event);
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (event.getY() < (5 * mSwipeRadius + 2 * marginTopAndBottom) / 2) { //色相区域
                    clickPanel = 1;
                    updateHueDate(x);
                } else if (event.getY() < 5 * mSwipeRadius + 2 * marginTopAndBottom) {
                    clickPanel = 2;
                    mSwipeSatCx = lastSatX = x;
                    updateSatDate();
                    invalidate();
                } else return super.onTouchEvent(event);

                break;
            case MotionEvent.ACTION_MOVE:
                if (clickPanel == 1) {
                    updateHueDate(x);
                } else if (clickPanel == 2) {
                    mSwipeSatCx = lastSatX = x;
                    updateSatDate();
                    invalidate();
                } else return super.onTouchEvent(event);
                break;
            case MotionEvent.ACTION_UP:
                clickPanel = -1;
                break;

        }
        return true;

    }

    private void updateHueDate(float x) {
        mSwipeHueCx = x;
        colorHSV[0] = 360 * (x - mSwipeRadius) / (mWidth - mSwipeBitmap.getWidth());
        updateSatDate();
        invalidate();
    }

    private void updateSatDate() {
        colorHSV[2] = (lastSatX - mSwipeRadius) / (mWidth - mSwipeBitmap.getWidth());
        if (mOnSelectColorListener != null) {
            mOnSelectColorListener.onSelectColor(Color.HSVToColor(colorHSV));
        }
    }

    public void setOnSelectColorListener(onSelectColorListener mListener) {
        this.mOnSelectColorListener = mListener;
        mListener.onSelectColor(Color.HSVToColor(colorHSV)); //初始
    }


    interface onSelectColorListener {
        void onSelectColor(@ColorInt int color);
    }

    @Override
    public int getSolidColor() {
        return Color.HSVToColor(colorHSV);
    }
}
