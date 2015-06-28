package com.flir.flironeexampleapplication;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import com.flir.flironeexampleapplication.nursethermalibrary.R;

public class CrosshairView extends View {

    private static final String LOG_TAG = CrosshairView.class.getSimpleName();

    private Float x;

    private Float y;

    private Paint linePaint;

    private Drawable crosshairs;

    private int crosshairsHeight;

    private int crosshairsWidth;

    public CrosshairView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public CrosshairView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CrosshairView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        linePaint = new Paint();
        linePaint.setColor(Color.GREEN);
        linePaint.setStrokeWidth(2);
        crosshairs = context.getResources().getDrawable(R.drawable.crosshairs);
        crosshairsHeight = crosshairs.getIntrinsicHeight();
        crosshairsWidth = crosshairs.getIntrinsicWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if ( null != x && null != y ) {
            canvas.drawLine(0, y, getWidth(), y, linePaint);
            canvas.drawLine(x, 0, x, getHeight(), linePaint);

            int crossHairLeft = (int) (x - crosshairsWidth / 2);
            int crossHairRight = (int) (x + crosshairsWidth - crosshairsWidth / 2);
            int crossHairTop = (int) (y - crosshairsHeight / 2);
            int crossHairBottom = (int) (y + crosshairsHeight - crosshairsHeight / 2);
            crosshairs.setBounds(crossHairLeft, crossHairTop, crossHairRight, crossHairBottom);
            crosshairs.draw(canvas);
        }
    }

    public void setLocation(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Float getLocationX() {
        return x;
    }

    public Float getLocationY() {
        return y;
    }

    public boolean hasLocation() {
        return null != x && null != y;
    }

    /*
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        Log.d(LOG_TAG, "dispatchTouchEvent x,y = " + event.getX() + "," + event.getY());

        setLocation(event.getX(), event.getY());
        invalidate();

        return super.dispatchTouchEvent(event);
    }
    */
}