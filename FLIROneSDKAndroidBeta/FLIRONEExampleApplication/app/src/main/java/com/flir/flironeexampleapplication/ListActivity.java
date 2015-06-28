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
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.flir.flironeexampleapplication.R;

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

    private Integer selectedDisplayIndex;

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

        updateDisplay();

    }

    private void updateDisplay() {

        selectedDisplayIndex = null;

        contents.removeAllViews();

        if (ThermalNurseApp.INSTANCE.displays.isEmpty()) {
            header.setText("Take a picture to start populating your items!");
            return;
        }

        int index = 0;
        for(final SavedDisplay display : ThermalNurseApp.INSTANCE.displays) {

            final ViewGroup root = (ViewGroup) getLayoutInflater().inflate(R.layout.activity_list_item, null);

            final TextView label = (TextView) root.findViewById(R.id.label);

            label.setText("Taken at: " + display.created);

            final ImageView image = (ImageView) root.findViewById(R.id.image);

            bgHandler.post(new Runnable() {
                @Override
                public void run() {
                    final Bitmap bitmap = BitmapFactory.decodeFile(display.savedFrame);

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            image.setImageBitmap(bitmap);
                        }
                    });
                }
            });

            final Button delete = (Button) root.findViewById(R.id.delete);
            delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ThermalNurseApp.INSTANCE.displays.remove(display);
                    updateDisplay();
                }
            });
            final Button select = (Button) root.findViewById(R.id.select);
            final int currentIndex = index;
            select.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if ( null == selectedDisplayIndex ) {
                        selectedDisplayIndex = currentIndex;
                        label.setText("**Selected** Taken at: " + display.created);
                        select.setEnabled(false);
                        return;
                    }

                    if (selectedDisplayIndex.equals(currentIndex)) {
                        return;
                    }

                    CompareActivity.startFor(ListActivity.this, selectedDisplayIndex, currentIndex);
                }
            });

            contents.addView(root);

            index++;
        }
    }
}
