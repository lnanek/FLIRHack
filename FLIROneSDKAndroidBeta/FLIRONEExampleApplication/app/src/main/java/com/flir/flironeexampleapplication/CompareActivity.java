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
        void onTemp(final Integer tempInC);
    }

    private static final String LOG_TAG = CompareActivity.class.getSimpleName();

    private ImageView itemAPreviewImage;

    private CrosshairView itemACrosshairs;

    private ImageView itemBPreviewImage;

    private CrosshairView itemBCrosshairs;

    private Integer itemATempInC;

    private Integer itemBTempInC;

    private TextView tempText;

    private TextView diagnosisText;

    private SavedDisplay itemADisplay;

    private SavedDisplay itemBDisplay;

    private Bitmap itemAPreviewBitmap;

    private Bitmap itemBPreviewBitmap;

    private OnTempReadListener itemATempListener = new OnTempReadListener() {
        @Override
        public void onTemp(final Integer tempInC) {
            itemATempInC = tempInC;
            updateText();
        }
    };

    private OnTempReadListener itemBTempListener = new OnTempReadListener() {
        @Override
        public void onTemp(final Integer tempInC) {
            itemBTempInC = tempInC;
            updateText();
        }
    };

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
        itemADisplay = ThermalNurseApp.INSTANCE.displays.get(itemAIndex);
        itemAPreviewBitmap = BitmapFactory.decodeFile(itemADisplay.savedFrame);

        final int itemBIndex = ThermalNurseApp.INSTANCE.displays.size() > 1 ? 1 : 0;
        itemBDisplay = ThermalNurseApp.INSTANCE.displays.get(itemBIndex);
        itemBPreviewBitmap = BitmapFactory.decodeFile(itemBDisplay.savedFrame);

        // To help comparisons, when crosshairs A is dragged, move both
        // When B is dragged, move only B, giving user option of linked and non-linked movement.
        final View.OnTouchListener linkedDrag = new View.OnTouchListener() {
            @Override
            public boolean onTouch(final View v, final MotionEvent event) {
                Log.d(LOG_TAG, "linkedDrag onTouch x,y = " + event.getX() + "," + event.getY());

                if (!itemBCrosshairs.hasLocation()) {
                    updateSelectionCoordinates((int) event.getX(), (int) event.getY(),
                            itemBCrosshairs, itemBPreviewBitmap, itemBDisplay, itemBTempListener);
                    return true;
                }

                if (!itemACrosshairs.hasLocation()) {
                    return true;
                }

                final float deltaX = event.getX() - itemACrosshairs.getLocationX();
                final float deltaY = event.getY() - itemACrosshairs.getLocationY();
                Log.d(LOG_TAG, "crosshair a moved = " + deltaX + "," + deltaY);

                final float newBX = itemBCrosshairs.getLocationX() + deltaX;
                final float newBY = itemBCrosshairs.getLocationY() + deltaY;

                updateSelectionCoordinates((int) newBX, (int) newBY,
                        itemBCrosshairs, itemBPreviewBitmap, itemBDisplay, itemBTempListener);

                return false;
            }
        };

        setupItem(itemADisplay, itemAPreviewBitmap, itemAPreviewImage,
                itemACrosshairs, itemATempListener, linkedDrag);
        setupItem(itemBDisplay, itemBPreviewBitmap, itemBPreviewImage,
                itemBCrosshairs, itemBTempListener, null);
    }

    private void updateText() {
        if ( null != itemATempInC && null != itemBTempInC ) {
            final int tempDifference = itemBTempInC - itemATempInC;
            String displayString = "Item A: " + itemATempInC + "\u00B0C  ";
            displayString += "Item B: " + itemBTempInC + "\u00B0C  ";
            displayString += "Delta T: " + tempDifference + "\u00B0C  ";
            tempText.setText(displayString);

            if (tempDifference > 5) {
                diagnosisText.setText("possible tumor blood flow or infection. See Doctor!");
            } else if (tempDifference < -5) {
                diagnosisText.setText("possible poor blood flow or calcification. See Doctor!");
            } else {
                diagnosisText.setText("situation normal!");
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

    private void setupItem(final SavedDisplay display,
                           final Bitmap previewBitmap,
                           final ImageView imageView,
                           final CrosshairView crosshairs,
                           final OnTempReadListener tempListener,
                           final View.OnTouchListener extraOnTouchListener) {

        Log.d(LOG_TAG, "renderedImage width,height = " + display.renderedImage.width() + "," + display.renderedImage.height());

        imageView.setImageBitmap(previewBitmap);

        crosshairs.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d(LOG_TAG, "setupItem onTouch x,y = " + event.getX() + "," + event.getY());

                if (null != extraOnTouchListener) {
                    extraOnTouchListener.onTouch(v, event);
                }

                updateSelectionCoordinates((int) event.getX(), (int) event.getY(),
                        crosshairs, previewBitmap, display, tempListener);

                return true;
            }
        });

    }

    private void updateSelectionCoordinates(final int newX, final int newY,
                                            CrosshairView crosshairs,
                                            Bitmap previewBitmap,
                                            SavedDisplay display,
                                            OnTempReadListener tempListener) {
        Log.d(LOG_TAG, "updateSelectionCoordinates x,y = " + newX + "," + newY);

        crosshairs.setLocation(newX, newY);
        crosshairs.invalidate();

        final int horizontalPadding = (crosshairs.getWidth() - previewBitmap.getWidth()) / 2;
        final int verticalPadding = (crosshairs.getHeight() - previewBitmap.getHeight()) / 2;

        final int imageX = newX - horizontalPadding;
        final int imageY = newY - verticalPadding;

        final Integer tempInC = getTempAt(CompareActivity.this, display.renderedImage, imageX, imageY);
        Log.d(LOG_TAG, "temp is: " + tempInC);
        tempListener.onTemp(tempInC);
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

    /*
    @Override
    public boolean dispatchTouchEvent(final MotionEvent touchEvent) {
        Log.d(LOG_TAG, "dispatchTouchEvent x,y = " + touchEvent.getX() + "," + touchEvent.getY());

        // XXX coordinates are in terms of activity, not imageview which is smaller part of activity coordinates
        //itemACrosshairs.setLocation(touchEvent.getX(), touchEvent.getY());
        //itemACrosshairs.invalidate();

        return super.dispatchTouchEvent(touchEvent);
    }
    */
}
