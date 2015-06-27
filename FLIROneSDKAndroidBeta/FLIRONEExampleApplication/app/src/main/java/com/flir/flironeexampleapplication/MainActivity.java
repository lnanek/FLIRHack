package com.flir.flironeexampleapplication;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * Created by lnanek on 6/27/15.
 */
public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
    }

    public void onBackgroundClicked(View v){
        final Intent intent = new Intent(this, BackgroundService.class);
        startService(intent);
    }

    public void onForegroundClicked(View v){
        final Intent intent = new Intent(this, PreviewActivity.class);
        startActivity(intent);
    }
}
