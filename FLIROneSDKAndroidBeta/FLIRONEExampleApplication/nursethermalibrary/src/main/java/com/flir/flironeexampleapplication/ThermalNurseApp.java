package com.flir.flironeexampleapplication;

import android.app.Application;

import com.flir.flironesdk.RenderedImage;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by lnanek on 6/27/15.
 */
public class ThermalNurseApp extends Application {

    public static ThermalNurseApp INSTANCE;

    public List<SavedDisplay> displays = new LinkedList<>();

    @Override
    public void onCreate() {

        INSTANCE = this;

        super.onCreate();
    }

    public void saveDisplay(RenderedImage renderedImage, String savedFrame) {

        final SavedDisplay display = new SavedDisplay();
        display.renderedImage = renderedImage;
        display.savedFrame = savedFrame;

        displays.add(display);
    }
}
