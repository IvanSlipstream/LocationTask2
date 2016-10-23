package ua.itstep.android11.kharlamov.locationtask.views;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Slipstream on 10.10.2016 in LocationTask.
 */
public class LocationView extends View {

    public static final float SIMILARITY = 1.4f;
    public static final float BORDER_WIDTH = 0.06f;
    public static final float TEXT_SHIFT = 0.17f;

    Paint mPaint;
    RectF mRectFExternal;
    RectF mRectFInternal;
    String mText;
    float mTextWidth;
    float mTextSize;
    float mDiameter;
    long mModelId;

    public LocationView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        mPaint.setColor(Color.BLACK);
        canvas.drawArc(mRectFExternal, 0, 360, true, mPaint);
        mPaint.setColor(Color.WHITE);
        canvas.drawArc(mRectFInternal, 0, 360, true, mPaint);
        mPaint.setColor(Color.BLACK);
        canvas.drawText(mText, 0.5f*(mDiameter-mTextWidth), 0.5f*(mDiameter+(1-TEXT_SHIFT)*mTextSize), mPaint);
    }

    public void drawText(String text, float textSize) {
        this.mText = text;
        this.mTextSize = textSize;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setTextSize(mTextSize);
        mTextWidth = mPaint.measureText(mText);
        mDiameter = Math.max(mTextSize, mTextWidth)*SIMILARITY;
        mRectFExternal = new RectF(0, 0, mDiameter, mDiameter);
        mRectFInternal = new RectF(BORDER_WIDTH*mDiameter, BORDER_WIDTH*mDiameter, (1-BORDER_WIDTH)*mDiameter, (1-BORDER_WIDTH)*mDiameter);
        invalidate();
    }

    public void setModelId(long id) {
        this.mModelId = id;
    }

    public long getModelId(){
        return mModelId;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        setMeasuredDimension((int) mDiameter, (int) mDiameter);
    }
}
