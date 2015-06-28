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

    private ImageView imageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_compare);
        imageView = (ImageView) findViewById(R.id.imageView);

        if (ThermalNurseApp.INSTANCE.displays.isEmpty()) {
            Toast.makeText(this, "Save a thermal image first!", Toast.LENGTH_LONG).show();
            finish();
            return;
        }

        final SavedDisplay display = ThermalNurseApp.INSTANCE.displays.get(0);

        Bitmap previewBitmap = BitmapFactory.decodeFile(display.savedFrame);

        imageView.setImageBitmap(previewBitmap);
    }

    // TODO tap spot on image, display temperature

}
