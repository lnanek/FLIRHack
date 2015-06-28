package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
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

    private static final String FRAME_FILE_PATH = "/sdcard/normal_hand.jpg";

    private ImageView imageView;

    private FrameProcessor frameProcessor;

    private FrameProcessor.Delegate frameReceiver = new FrameProcessor.Delegate() {
        @Override
        public void onFrameProcessed(RenderedImage renderedImage) {
            Log.d(LOG_TAG, "onFrameProcessed");

            Toast.makeText(CompareActivity.this, "yay", Toast.LENGTH_LONG);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compare);
        imageView = (ImageView) findViewById(R.id.imageView);

        final Frame frame;
        //try {
            final File file = new File(FRAME_FILE_PATH);

            Log.d(LOG_TAG, "file exists: " + file.exists());
            Log.d(LOG_TAG, "file size: " + file.length());

            // XXX crashes
            //final FileInputStream fileInputStream = new FileInputStream(file);
            //frame = Frame.load(fileInputStream);

            frame = new Frame(file);

        //} catch(IOException e) {
        //    Log.e(LOG_TAG, "Error loading frame", e);
        //    throw new RuntimeException(e);
        //}

        Log.d(LOG_TAG, "loaded frame: " + frame);

        frameProcessor = new FrameProcessor(this, frameReceiver,
                EnumSet.of(RenderedImage.ImageType.ThermalRadiometricKelvinImage));

        frameProcessor.setImagePalette(RenderedImage.Palette.Iron);

        frameProcessor.processFrame(frame);

    }



}
