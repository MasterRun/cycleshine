package com.jsongo.cycleshine.view;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.jsongo.cycleshine.R;

/**
 * @author jsongo
 * @date 2019/12/1 11:30
 * @desc 转动圆环
 */
public class CycleShine extends View {

    //region fields
    /**
     * 背景和弧形的画笔
     */
    private Paint bgPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    private Paint arcPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
    /**
     * 弧形（环形）的宽度
     */
    private float progressWidth;

    /**
     * view的宽度
     */
    private int mMeasureWidth;
    /**
     * view的高度
     */
    private int mMeasureHeight;
    /**
     * view的上下左右边界
     */
    private RectF pRectF;
    /**
     * 开始颜色
     */
    private int progressStartColor;
    /**
     * 结束颜色
     */
    private int progressEndColor;
    /**
     * 背景色
     */
    private int bgColor;
    /**
     * 弧形绘制的其实角度
     */
    private int startAngle;

    /**
     * 单位角度
     */
    private float unitAngle;
    /**
     * 扫过的角度（对应弧长）
     */
    private int sweepAngle;
    /**
     * 旋转属性动画， mark 因为背景是圆形，所以使用属性动画直接将整个view旋转，如果背景不是整个圆，需要旋转，需要onDraw中进行不断绘制
     */
    private ObjectAnimator rotationAnim;
    //endregion

    //region constructor

    public CycleShine(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CycleShine(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }
    //endregion

    protected void init(Context context, AttributeSet attrs) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CycleShine);

        progressWidth = typedArray.getDimension(R.styleable.CycleShine_pr_progress_width, 8f);
        progressStartColor = typedArray.getColor(R.styleable.CycleShine_pr_progress_start_color, Color.YELLOW);
        progressEndColor = typedArray.getColor(R.styleable.CycleShine_pr_progress_end_color, progressStartColor);
        bgColor = typedArray.getColor(R.styleable.CycleShine_pr_bg_color, Color.TRANSPARENT);
        startAngle = typedArray.getInt(R.styleable.CycleShine_pr_start_angle, 150);
        sweepAngle = typedArray.getInt(R.styleable.CycleShine_pr_sweep_angle, 240);

        typedArray.recycle();

        //分成一百份
        unitAngle = sweepAngle / 100f;

        bgPaint.setStyle(Paint.Style.STROKE);
        bgPaint.setStrokeCap(Paint.Cap.ROUND);
        bgPaint.setStrokeWidth(progressWidth);

        arcPaint.setStyle(Paint.Style.STROKE);
        arcPaint.setStrokeCap(Paint.Cap.ROUND);
        arcPaint.setStrokeWidth(progressWidth);

    }

    //region onMeasure onDraw
    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mMeasureWidth = getMeasuredWidth();
        mMeasureHeight = getMeasuredHeight();
        if (pRectF == null) {
            float halfProgressWidth = progressWidth / 2;
            pRectF = new RectF(halfProgressWidth + getPaddingLeft(),
                    halfProgressWidth + getPaddingTop(),
                    mMeasureWidth - halfProgressWidth - getPaddingRight(),
                    mMeasureHeight - halfProgressWidth - getPaddingBottom());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        drawBg(canvas);
        drawArc(canvas);
    }
    //endregion

    /**
     * 绘制背景（绘制 一个底层颜色）
     *
     * @param canvas
     */
    private void drawBg(Canvas canvas) {
        //非透明才绘制
        if (bgColor != Color.TRANSPARENT) {
            //绘制一个圆形
            bgPaint.setColor(bgColor);
            canvas.drawArc(pRectF,
                    0,
                    360,
                    false,
                    bgPaint);
        }
    }

    /**
     * 画弧形，颜色不同，所以sweepAngle为1，每次设置画笔颜色，绘制1个角度
     *
     * @param canvas
     */
    private void drawArc(Canvas canvas) {
        //绘制100个角度
        int curProgress = 100;
        for (int i = 0, end = (int) (curProgress * unitAngle); i <= end; i++) {
            //设置画笔颜色
            arcPaint.setColor(getGradient(i / (float) end, progressStartColor, progressEndColor));
            //画弧 （一个角度）
            canvas.drawArc(pRectF,
                    startAngle + i,
                    1,
                    false,
                    arcPaint);
        }
    }

    /**
     * 获取到旋转的属性动画
     *
     * @return
     */
    public ObjectAnimator getRotationAnim() {
        if (rotationAnim == null) {
            rotationAnim = ObjectAnimator.ofFloat(this, "rotation", 0, 360);
            rotationAnim.setDuration(2000);
            rotationAnim.setRepeatCount(ValueAnimator.INFINITE);
        }
        return rotationAnim;
    }

    /**
     * 开始属性动画
     */
    public void startAnim() {
        getRotationAnim().start();
    }

    /**
     * 获取梯度对应的颜色
     *
     * @param fraction
     * @param startColor
     * @param endColor
     * @return
     */
    public int getGradient(float fraction, int startColor, int endColor) {
        if (fraction > 1) {
            fraction = 1;
        }
        int alphaStart = Color.alpha(startColor);
        int redStart = Color.red(startColor);
        int blueStart = Color.blue(startColor);
        int greenStart = Color.green(startColor);
        int alphaEnd = Color.alpha(endColor);
        int redEnd = Color.red(endColor);
        int blueEnd = Color.blue(endColor);
        int greenEnd = Color.green(endColor);
        int alphaDifference = alphaEnd - alphaStart;
        int redDifference = redEnd - redStart;
        int blueDifference = blueEnd - blueStart;
        int greenDifference = greenEnd - greenStart;
        int alphaCurrent = (int) (alphaStart + fraction * alphaDifference);
        int redCurrent = (int) (redStart + fraction * redDifference);
        int blueCurrent = (int) (blueStart + fraction * blueDifference);
        int greenCurrent = (int) (greenStart + fraction * greenDifference);
        return Color.argb(alphaCurrent, redCurrent, greenCurrent, blueCurrent);
    }

}
