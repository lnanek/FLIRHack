package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.flironeexampleapplication.nursethermalibrary.R;

/**
 * Offers options for running FLIR One in background or foreground.
 *
 * Created by lnanek on 6/27/15.
 */
public class ListActivity extends Activity {

    private static final String LOG_TAG = ListActivity.class.getSimpleName();

    private TextView header;

    private ViewGroup contents;

    private Handler bgHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreate");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_list);
        header = (TextView) findViewById(R.id.header);
        contents = (ViewGroup) findViewById(R.id.contents);

        final HandlerThread thread = new HandlerThread("bg");
        thread.start();
        bgHandler = new Handler(thread.getLooper());
    }

    @Override
    protected void onResume() {
        super.onResume();

        contents.removeAllViews();

        if (ThermalNurseApp.INSTANCE.displays.isEmpty()) {
            header.setText("Take a picture to start populating your items!");
            return;
        }

        for(final SavedDisplay display : ThermalNurseApp.INSTANCE.displays) {

            final ViewGroup root = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_list_item, null);

            final TextView label = (TextView) root.findViewById(R.id.label);

            label.setText("Taken at: " + display.created);

            final ImageView image = (ImageView) root.findViewById(R.id.image);

            bgHandler.post(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapFactory.decodeFile(display.savedFrame);
                    image.setImageBitmap(bitmap);
                }
            });

            contents.addView(root);
        }

        // TODO tap to pick items to compare
    }
}
