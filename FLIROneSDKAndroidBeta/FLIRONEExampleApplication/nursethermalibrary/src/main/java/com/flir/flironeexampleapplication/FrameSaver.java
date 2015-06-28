package com.flir.flironeexampleapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;

import com.flir.flironesdk.Frame;
import com.flir.flironesdk.RenderedImage;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Created by lnanek on 6/27/15.
 */
public class FrameSaver {

    private static final String LOG_TAG = FrameSaver.class.getSimpleName();

    public static String saveFrame(final Context context, final Frame frame) {


        String path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ssZ", Locale.getDefault());
        String formatedDate = sdf.format(new Date());
        String fileName = "FLIROne-" + formatedDate + ".jpg";
        String lastSavedPath = path+ "/" + fileName;

        try{
            frame.save(lastSavedPath, RenderedImage.Palette.Iron, RenderedImage.ImageType.BlendedMSXRGBA8888Image);

            Log.d(LOG_TAG, "***Lance*** thermal data saved to path: " + lastSavedPath);
            Toast.makeText(context, "Saved to: " + lastSavedPath, Toast.LENGTH_LONG).show();

        }catch (Exception e){
            Log.e(LOG_TAG, "Error saving frame data", e);
        }

        return lastSavedPath;

    }

}
