package smart.sprinkler.app.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.ProgressBar;

import smart.sprinkler.app.R;

public class ArcBar extends ProgressBar {
    private RectF mArcRect = new RectF();
    private Paint mArcPaint;
    private Paint mProgressPaint;

    public ArcBar(Context context) {
        this(context, null);
    }

    public ArcBar(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public ArcBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs);
    }

    private void init(AttributeSet attrs) {
        int progressColor = getResources().getColor(R.color.blue_solid);
        int ovalColor = getResources().getColor(R.color.grey_average);
        int DEFAULT_ARC_WIDTH = 24;
        int progressWidth = DEFAULT_ARC_WIDTH;
        int arcWidth = DEFAULT_ARC_WIDTH;

        // Init attributes for ArcBar
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.ArcBar, 0, 0);
            progressWidth = (int) a.getDimension(R.styleable.ArcBar_progressWidth, DEFAULT_ARC_WIDTH);
            arcWidth = (int) a.getDimension(R.styleable.ArcBar_arcWidth, DEFAULT_ARC_WIDTH);
            ovalColor = a.getColor(R.styleable.ArcBar_arcColor, ovalColor);
            progressColor = a.getColor(R.styleable.ArcBar_progressColor, progressColor);
            a.recycle();
        }

        // Paint for background of ArcBar
        mArcPaint = new Paint();
        mArcPaint.setColor(ovalColor);
        mArcPaint.setAntiAlias(true);
        mArcPaint.setStyle(Paint.Style.STROKE);
        mArcPaint.setStrokeWidth(arcWidth);

        // Paint for progressbar of ArcBar
        mProgressPaint = new Paint();
        mProgressPaint.setColor(progressColor);
        mProgressPaint.setAntiAlias(true);
        mProgressPaint.setStyle(Paint.Style.STROKE);
        mProgressPaint.setStrokeWidth(progressWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawArc(mArcRect, 0, 360, false, mArcPaint);
        //360 degrees- max
        //x degrees - progress
        float progressArcAngle = getProgress() * 360f / getMax();
        // The initial offset 90 means we start at 6 o'clock
        int PROGRESS_ARC_STARTS_FROM = 90;
        canvas.drawArc(mArcRect, PROGRESS_ARC_STARTS_FROM, progressArcAngle, false, mProgressPaint);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        float widestLineHalf = Math.max(mArcPaint.getStrokeWidth(), mProgressPaint.getStrokeWidth()) / 2;
        mArcRect.set(0 + widestLineHalf, 0 + widestLineHalf,
                getMeasuredWidth() - widestLineHalf,getMeasuredHeight() - widestLineHalf);
    }
}