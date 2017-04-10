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
import android.view.MotionEvent;
import android.view.View;

/**
 * @author 依风听雨
 * @version 创建时间：2017/4/7 17:16
 */

public class ColorView extends View {
    private LinearGradient linearGradient = null;
    private Paint mHuePaint = null;
    private Paint mSaturationPaint = null;
    //    private Paint mPaintColor;
    private RectF mHueRectF = null;
    private RectF mSaturationRectF = null;
    private int mWidth;

    private Paint mSwipePaint;
    private Bitmap mSwipeBitmap;

    private onSelectColorListener mOnSelectColorListener;

    private static final int mColorHeight = 20;
    private float mSwipeRadius;
    private int marginTopAndBottom = 20;

    /**
     * 滑块的圆心x
     */
    private float mSwipeHueCx = 0;
    private float mSwipeSatCx = 0;

    private float mHue = 0f;

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
        mHuePaint = new Paint();
        mSaturationPaint = new Paint();
        mSwipePaint = new Paint();
        mSwipePaint.setAntiAlias(true);
        mSwipeBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.swipe);
        mSwipeRadius = lastSatX = mSwipeBitmap.getWidth() / 2;

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
        if (linearGradient == null) {
            linearGradient = new LinearGradient(0, 0, mWidth, 0, buildHueColorArray(), null,
                    Shader.TileMode.CLAMP);
            //设置渲染器
            mHuePaint.setShader(linearGradient);
        }
        //绘制颜色条
        if (mHueRectF == null)
            mHueRectF = new RectF(mSwipeRadius, mSwipeRadius - mColorHeight / 2 + marginTopAndBottom, mWidth - mSwipeRadius, mSwipeRadius + mColorHeight / 2 + marginTopAndBottom);
        canvas.drawRoundRect(mHueRectF, 15, 15, mHuePaint);
        //绘制滑块
        if (mSwipeHueCx < mSwipeRadius)
            mSwipeHueCx = mSwipeRadius;
        else if (mSwipeHueCx > mWidth - mSwipeRadius)
            mSwipeHueCx = mWidth - mSwipeRadius;
        canvas.drawBitmap(mSwipeBitmap, mSwipeHueCx - mSwipeRadius, marginTopAndBottom, mSwipePaint);
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

        //HSV转化为RGB
        int rgb = Color.HSVToColor(new float[]{mHue, 1f, 1f});
        //饱和线性渲染器
        LinearGradient mSaturationShader = new LinearGradient(rect.left, rect.top, rect.right, rect.top,
                0xff000000, rgb, Shader.TileMode.CLAMP);

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
        setMeasuredDimension(getDefaultSize(getSuggestedMinimumWidth(), widthMeasureSpec),
                measureHeight(heightMeasureSpec));
        mWidth = getWidth();
    }


    private int measureHeight(int heightMeasureSpec) {
        int result;
        int specMode = MeasureSpec.getMode(heightMeasureSpec);
        int specSize = MeasureSpec.getSize(heightMeasureSpec);
        if (specMode == MeasureSpec.EXACTLY) {
            result = specSize;
        } else {
            result = (int) (5 * mSwipeRadius + 2 * marginTopAndBottom);
            if (specMode == MeasureSpec.AT_MOST) {
                result = Math.min(result, specSize);
            }
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
                if (event.getY() < mSwipeRadius + marginTopAndBottom * 2) { //色相区域
                    clickPanel = 1;
                    updateHueDate(x);
                } else if (event.getY() > 3 * mSwipeRadius && event.getY() < 4 * mSwipeRadius + 2 * marginTopAndBottom) {
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
        mHue = 360 * (x - mSwipeRadius) / (mWidth - mSwipeBitmap.getWidth());
        updateSatDate();
        invalidate();
    }

    private void updateSatDate() {
        int rgb = Color.HSVToColor(new float[]{mHue, 1f, 1f});
        int colorFrom = getColorFrom(0xff000000, rgb, (lastSatX - mSwipeRadius) / (mWidth - mSwipeBitmap.getWidth()));
        if (mOnSelectColorListener != null) {
            mOnSelectColorListener.onSelectColor(colorFrom);
        }
    }

    public void setOnSelectColorListener(onSelectColorListener mListener) {
        this.mOnSelectColorListener = mListener;
        mListener.onSelectColor(0xff000000); //初始
    }


    interface onSelectColorListener {
        void onSelectColor(@ColorInt int color);
    }

    /**
     * 取两个颜色间的渐变区间 中的某一点的颜色
     *
     * @param startColor
     * @param endColor
     * @param radio
     * @return
     */
    public int getColorFrom(int startColor, int endColor, float radio) {
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);

        int red = (int) (redStart + ((redEnd - redStart) * radio + 0.5));
        int greed = (int) (greenStart + ((greenEnd - greenStart) * radio + 0.5));
        int blue = (int) (blueStart + ((blueEnd - blueStart) * radio + 0.5));
        return Color.argb(255, red, greed, blue);
    }
}
