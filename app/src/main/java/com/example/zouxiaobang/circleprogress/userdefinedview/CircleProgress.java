package com.example.zouxiaobang.circleprogress.userdefinedview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.view.View;

import com.example.zouxiaobang.circleprogress.R;

/**
 * Created by zouxiaobang on 16/9/25.
 */

public class CircleProgress extends View {
    private float mBallRadius;
    private int mBallColor;
    private float mCenterTextSize;
    private int mCenterTextColor;
    private String mCenterText;
    private int mProgressColor;

    private Paint mRoundPaint;
    private Paint mFontPaint;
    private Paint mProgressPaint;
    private Path mPath;
    private Bitmap mBitmap;
    private Canvas mCanvas;
    private int currentProgress = 0;
    private int maxProgress = 100;
    private int space = 30;
    private int move = 0;


    public CircleProgress(Context context) {
        this(context, null);
    }

    public CircleProgress(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CircleProgress(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CircleProgress);
        mBallRadius = typedArray.getDimension(R.styleable.CircleProgress_ballRadius, 260f);
        mBallColor = typedArray.getColor(R.styleable.CircleProgress_ballColor, Color.argb(255, 33, 150, 243));
        mCenterText = typedArray.getString(R.styleable.CircleProgress_centerText);
        mCenterTextSize = typedArray.getDimension(R.styleable.CircleProgress_centerTextSize, 20f);
        mCenterTextColor = typedArray.getColor(R.styleable.CircleProgress_centerTextColor, Color.argb(255, 48, 63, 159));
        mProgressColor = typedArray.getColor(R.styleable.CircleProgress_progressColor, Color.argb(255, 48, 63, 159));
        typedArray.recycle();

        initPaint();
    }

    /**
     * 初始化Paint
     */
    private void initPaint() {
        mRoundPaint = new Paint();
        mRoundPaint.setColor(mBallColor);
        mRoundPaint.setAntiAlias(true);

        mFontPaint = new Paint();
        mFontPaint.setColor(mCenterTextColor);
        mFontPaint.setAntiAlias(true);
        mFontPaint.setTextSize(mCenterTextSize);
        mFontPaint.setFakeBoldText(true);

        mProgressPaint = new Paint();
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setColor(mProgressColor);
        //取两层绘制的交集,取上层
        mProgressPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));

        mBitmap = Bitmap.createBitmap((int) mBallRadius*2, (int)mBallRadius*2, Bitmap.Config.ARGB_8888);
        mCanvas = new Canvas(mBitmap);
        mPath = new Path();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension(measure(widthMeasureSpec), measure(heightMeasureSpec));
    }

    /**
     * 对宽高进行测量
     * @param measureSpec
     * @return
     */
    private int measure(int measureSpec) {
        int result = 0;
        int specMode = MeasureSpec.getMode(measureSpec);
        int specSize = MeasureSpec.getSize(measureSpec);

        if (specMode == MeasureSpec.EXACTLY){
            result = specSize;
        } else {
            result = (int) mBallRadius * 2;
            if (specMode == MeasureSpec.AT_MOST){
                result = result<specSize?result:specSize;
            }
        }

        return result;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        //绘制圆
        mCanvas.drawCircle(width/2, height/2, mBallRadius, mRoundPaint);
        //绘制波形
        mPath.reset();
        int count = (int)(mBallRadius + 1)*2 / space;
        float y1 = (1 - (float)currentProgress/maxProgress)*mBallRadius*2 + height/2 - mBallRadius;
        move += 20;
        if (move>width){
            move = width;
        }
        mPath.moveTo(-width+y1, y1);
        float d = (1-(float)currentProgress/maxProgress) * space;
        for (int i = 0;i < count;i ++){
            mPath.rQuadTo(space, -d, space*2, 0);
            mPath.rQuadTo(space, d, space*2, 0);
        }
        mPath.lineTo(width, y1);
        mPath.lineTo(width, height);
        mPath.lineTo(0, height);
        //实心
        mPath.close();
        mCanvas.drawPath(mPath, mProgressPaint);

        //写字
        mCenterText = currentProgress + " %";
        float textWidth = mFontPaint.measureText(mCenterText);
        Paint.FontMetrics fontMetrics = new Paint.FontMetrics();
        float dy = -(fontMetrics.descent + fontMetrics.ascent)/2;
        float x = width/2 - textWidth/2;
        float y = height/2 + dy;
        mCanvas.drawText(mCenterText, x, y, mFontPaint);

        canvas.drawBitmap(mBitmap, 0, 0, null);

    }

    public void setProgress(int progress){
        currentProgress = progress;
        invalidate();
    }
}
