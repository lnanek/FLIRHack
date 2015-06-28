package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flir.flironesdk.RenderedImage;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;

/**
 * Offers options for running FLIR One in background or foreground.
 * <p/>
 * Created by lnanek on 6/27/15.
 */
public class CompareActivity extends Activity {

    private static interface OnTempReadListener {
        void onTemp(final int tempInC);
    }

    private static final String LOG_TAG = CompareActivity.class.getSimpleName();

    private ImageView itemAPreviewImage;

    private CrosshairView itemACrosshairs;

    private ImageView itemBPreviewImage;

    private CrosshairView itemBCrosshairs;

    private TextView tempText;

    private TextView diagnosisText;

    private Integer itemATempInC;

    private Integer itemBTempInC;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compare);
        itemAPreviewImage = (ImageView) findViewById(R.id.itemAPreviewImage);
        itemACrosshairs = (CrosshairView) findViewById(R.id.itemACrosshairs);
        itemBPreviewImage = (ImageView) findViewById(R.id.itemBPreviewImage);
        itemBCrosshairs = (CrosshairView) findViewById(R.id.itemBCrosshairs);

        tempText = (TextView) findViewById(R.id.temperatureReadout);
        diagnosisText = (TextView) findViewById(R.id.diagnosis);

        if (ThermalNurseApp.INSTANCE.displays.isEmpty()) {
            Toast.makeText(this, "Save a thermal image first!", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }

        // TODO pick compare indexes in a list
        final int itemAIndex = 0;
        final int itemBIndex = ThermalNurseApp.INSTANCE.displays.size() > 1 ? 1 : 0;

        setupItem(itemAIndex, itemAPreviewImage, itemACrosshairs, new OnTempReadListener() {
            @Override
            public void onTemp(final int tempInC) {
                itemATempInC = tempInC;
                updateText();
            }
        });
        setupItem(itemBIndex, itemBPreviewImage, itemBCrosshairs, new OnTempReadListener() {
            @Override
            public void onTemp(final int tempInC) {
                itemBTempInC = tempInC;
                updateText();
            }
        });

    }

    private void updateText() {
        if ( null != itemATempInC && null != itemBTempInC ) {
            final int tempDifference = itemBTempInC - itemATempInC;
            String displayString = "Item A: " + itemATempInC + "\u00B0C  ";
            displayString += "Item B: " + itemBTempInC + "\u00B0C  ";
            displayString += "Delta T: " + tempDifference + "\u00B0C  ";
            tempText.setText(displayString);

            if (tempDifference > 5) {
                diagnosisText.setText("Diagnosis: possible tumor blood flow or infection. Check with Doctor!");
            } else if (tempDifference < -5) {
                diagnosisText.setText("Diagnosis: possible poor blood flow or calcification. Check with Doctor!");
            } else {
                diagnosisText.setText("Diagnosis: situation normal!");
            }

        } else if ( null != itemATempInC ) {
            String displayString = "Item A: " + itemATempInC + "\u00B0C  ";
            displayString += "Tap Item B to Compare!";
            tempText.setText(displayString);

        } else if ( null != itemBTempInC ) {
            String displayString = "Item B: " + itemBTempInC + "\u00B0C  ";
            displayString += "Tap Item A to Compare!";
            tempText.setText(displayString);
        }
    }

    private void setupItem(final int displayIndex,
                           final ImageView imageView,
                           final CrosshairView crosshairs,
                           final OnTempReadListener tempListener) {

        final SavedDisplay display = ThermalNurseApp.INSTANCE.displays.get(displayIndex);

        Log.d(LOG_TAG, "renderedImage width,height = " + display.renderedImage.width() + "," + display.renderedImage.height());

        final Bitmap previewBitmap = BitmapFactory.decodeFile(display.savedFrame);

        imageView.setImageBitmap(previewBitmap);

        crosshairs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(LOG_TAG, "onTouch x,y = " + event.getX() + "," + event.getY());

                crosshairs.setLocation(event.getX(), event.getY());
                crosshairs.invalidate();

                final int horizontalPadding = (crosshairs.getWidth() - previewBitmap.getWidth()) / 2;
                final int verticalPadding = (crosshairs.getHeight() - previewBitmap.getHeight()) / 2;

                final int imageX = (int) event.getX() - horizontalPadding;
                final int imageY = (int) event.getY() - verticalPadding;

                final Integer tempInC = getTempAt(CompareActivity.this, display.renderedImage, imageX, imageY);
                Log.d(LOG_TAG, "temp is: " + tempInC);
                tempListener.onTemp(tempInC);

                return true;
            }
        });

    }

    private static Integer getTempAt(final Context context, final RenderedImage renderedImage,
                              final int previewImageX, final int previewImageY) {
        Log.d(LOG_TAG, "getTempAt x,y = " + previewImageX + "," + previewImageY);

        // Didn't save temps
        if (renderedImage.imageType() != RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            Toast.makeText(context, "Save a radiometric image type!", Toast.LENGTH_LONG).show();
            return null;
        }

        // Preview image is 480x640, temp image is 240x320
        final int tempImageX = previewImageX / 2;
        final int tempImageY = previewImageY / 2;

        // Out of bounds
        if ( tempImageX < 0 || tempImageY < 0
                || tempImageX >= renderedImage.width() || tempImageY >= renderedImage.height()) {
            return null;
        }

        final int index = tempImageX + tempImageY * renderedImage.width();

        short[] shortPixels = new short[renderedImage.pixelData().length / 2];

        // Thermal data is little endian.
        ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);

        int tempInC = (shortPixels[index] - 27315) / 100;

        return tempInC;
    }


    @Override
    public boolean dispatchTouchEvent(final MotionEvent touchEvent) {
        Log.d(LOG_TAG, "dispatchTouchEvent x,y = " + touchEvent.getX() + "," + touchEvent.getY());

        // XXX coordinates are in terms of activity, not imageview which is smaller part of activity coordinates
        //itemACrosshairs.setLocation(touchEvent.getX(), touchEvent.getY());
        //itemACrosshairs.invalidate();

        return super.dispatchTouchEvent(touchEvent);
    }
}
