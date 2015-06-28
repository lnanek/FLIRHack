package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.EnumSet;

/**
 * Offers options for running FLIR One in background or foreground.
 *
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

        Bitmap previewBitmap = BitmapFactory.decodeFile(display.savedFrame);

        imageView.setImageBitmap(previewBitmap);
    }

    // TODO tap spot on image, display temperature

    @Override
    public boolean dispatchTouchEvent(final MotionEvent touchEvent) {
        Log.d(LOG_TAG, "dispatchTouchEvent");

        crosshairs.setLocation(touchEvent.getX(), touchEvent.getY());
        crosshairs.invalidate();

        return super.dispatchTouchEvent(touchEvent);
    }
}
