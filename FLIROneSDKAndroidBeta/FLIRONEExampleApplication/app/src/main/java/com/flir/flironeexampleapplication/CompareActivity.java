package com.flir.flironeexampleapplication;

import android.app.Activity;
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

    private static final String LOG_TAG = CompareActivity.class.getSimpleName();

    private ImageView imageView;

    private CrosshairView crosshairs;

    private TextView temperatureReadout;

    private TextView diagnosis;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compare);
        imageView = (ImageView) findViewById(R.id.imageView);
        crosshairs = (CrosshairView) findViewById(R.id.crosshairs);

        temperatureReadout = (TextView) findViewById(R.id.temperatureReadout);
        diagnosis = (TextView) findViewById(R.id.diagnosis);

        if (ThermalNurseApp.INSTANCE.displays.isEmpty()) {
            Toast.makeText(this, "Save a thermal image first!", Toast.LENGTH_LONG).show();
            //finish();
            return;
        }

        final SavedDisplay display = ThermalNurseApp.INSTANCE.displays.get(0);

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

                final Integer tempInC = getTempAt(display.renderedImage, imageX, imageY);

                Log.d(LOG_TAG, "temp is: " + tempInC);
                if ( null == tempInC ) {
                    temperatureReadout.setText("Select a point on the image");
                } else {
                    temperatureReadout.setText("Temperature at selected point is: " + tempInC);
                }

                return true;
            }
        });
    }

    private Integer getTempAt(final RenderedImage renderedImage,
                              final int previewImageX, final int previewImageY) {
        Log.d(LOG_TAG, "getTempAt x,y = " + previewImageX + "," + previewImageY);

        // Didn't save temps
        if (renderedImage.imageType() != RenderedImage.ImageType.ThermalRadiometricKelvinImage) {
            Toast.makeText(this, "Save a radiometric image type!", Toast.LENGTH_LONG).show();
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
        //crosshairs.setLocation(touchEvent.getX(), touchEvent.getY());
        //crosshairs.invalidate();

        return super.dispatchTouchEvent(touchEvent);
    }
}
