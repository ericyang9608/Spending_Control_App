package com.hyy.accountassis.widget;

import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.LinearInterpolator;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;

import com.hyy.accountassis.R;

import static com.hyy.accountassis.util.CommonUtils.dp2px;

public class ArcView extends View {

    private Paint mArcPaint;
    private Paint mTextPaint;
    private float startAngle = 135;
    private float endAngle = 45;
    private float mAngle = 270;
    private float mIncludedAngle = 0;
    private float mStrokeWith = 10;
    private String mDes = "";
    private int mAnimatorValue, mMinValue, mMaxValue;
    private float centerX, centerY;

    public ArcView(Context context) {
        this(context, null);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    private void initPaint() {
        mArcPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.black6));
        mArcPaint.setAlpha(100);
        mArcPaint.setStrokeJoin(Paint.Join.ROUND);
        mArcPaint.setStrokeCap(Paint.Cap.ROUND);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(dp2px(mStrokeWith));

        mTextPaint = new Paint();
        mTextPaint.setAntiAlias(true);
        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.black3));
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        //mTextPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.dp_12));
        mTextPaint.setTextSize(dp2px(25));

    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        centerX = getWidth() / 2;
        centerY = getHeight() / 2;
        initPaint();
        drawArc(canvas);
        drawText(canvas);
    }

    /**
     * @param canvas
     */
    private void drawText(Canvas canvas) {
        Rect mRect = new Rect();
        String mValue = String.valueOf(mAnimatorValue);
        mTextPaint.getTextBounds(mValue, 0, mValue.length(), mRect);
        canvas.drawText(String.valueOf(mAnimatorValue), centerX, centerY + mRect.height(), mTextPaint);

        mTextPaint.setColor(ContextCompat.getColor(getContext(), R.color.gray8f));
        mTextPaint.setTextSize(dp2px(12));
        mTextPaint.getTextBounds(mDes, 0, mDes.length(), mRect);
        canvas.drawText(mDes, centerX, centerY + 2 * mRect.height() + dp2px(10), mTextPaint);

        String minValue = String.valueOf(mMinValue);
        String maxValue = String.valueOf(mMaxValue);
        mTextPaint.setTextSize(dp2px(18));
        mTextPaint.getTextBounds(minValue, 0, minValue.length(), mRect);
        canvas.drawText(minValue, (float) (centerX - 0.6 * centerX - dp2px(5)), (float) (centerY + 0.75 * centerY + mRect.height() + dp2px(5)), mTextPaint);
        mTextPaint.getTextBounds(maxValue, 0, maxValue.length(), mRect);
        canvas.drawText(maxValue, (float) (centerX + 0.6 * centerX + dp2px(5)), (float) (centerY + 0.75 * centerY + mRect.height() + dp2px(5)), mTextPaint);
    }

    private void drawArc(Canvas canvas) {
        RectF mRectF = new RectF(mStrokeWith + dp2px(5), mStrokeWith + dp2px(5), getWidth() - mStrokeWith - dp2px(5), getHeight() - mStrokeWith);
        canvas.drawArc(mRectF, startAngle, mAngle, false, mArcPaint);
        mArcPaint.setColor(ContextCompat.getColor(getContext(), R.color.blueColor));
        canvas.drawArc(mRectF, startAngle, mIncludedAngle, false, mArcPaint);
    }

    private void setAnimation(float startAngle, float currentAngle, int currentValue, int time) {
        ValueAnimator progressAnimator = ValueAnimator.ofFloat(startAngle, currentAngle);
        progressAnimator.setDuration(time);
        progressAnimator.setTarget(mIncludedAngle);
        progressAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                mIncludedAngle = (float) animation.getAnimatedValue();
                postInvalidate();
            }
        });
        progressAnimator.start();

        ValueAnimator valueAnimator = ValueAnimator.ofInt(mAnimatorValue, currentValue);
        valueAnimator.setDuration(1500);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                mAnimatorValue = (int) valueAnimator.getAnimatedValue();
                postInvalidate();
            }
        });
        valueAnimator.start();
    }

    public void setValues(int minValue, int maxValue, int currentValue, String des) {
        mDes = des;
        mMaxValue = maxValue;
        mMinValue = minValue;
        if (currentValue > maxValue) {
            currentValue = maxValue;
        }
        float scale = (float) currentValue / maxValue;
        float currentAngle = scale * mAngle;
        setAnimation(0, currentAngle, currentValue, 2500);
    }
}
