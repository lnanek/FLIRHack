package com.flir.flironeexampleapplication;

import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.view.MotionEvent;
import android.view.OrientationEventListener;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.flir.flironesdk.Device;
import com.flir.flironesdk.Frame;
import com.flir.flironesdk.FrameProcessor;
import com.flir.flironesdk.RenderedImage;
import com.flir.flironesdk.SimulatedDevice;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.EnumSet;
import java.util.Locale;
import com.flir.flironeexampleapplication.nursethermalibrary.R;

/**
 * Created by lnanek on 6/27/15.
 */
public class BackgroundService extends Service implements Device.Delegate, FrameProcessor.Delegate, Device.StreamDelegate, Device.PowerUpdateDelegate {

    private static final String LOG_TAG = BackgroundService.class.getSimpleName();

    @Override
    public IBinder onBind(Intent intent) {
        Log.i(LOG_TAG, "onBind");

        return null;
    }

    @Override
    public void onCreate() {
        Log.i(LOG_TAG, "onCreate");

        super.onCreate();

        startForeground(ForegroundNotification.NOTIFICATION_ID, ForegroundNotification.show(this));

        // From activity onCreate

        String[] imageTypeNames = new String[RenderedImage.ImageType.values().length];
        // Massage the type names for display purposes
        for (RenderedImage.ImageType t : RenderedImage.ImageType.values()){
            String name = t.name().replaceAll("(RGBA)|(YCbCr)|(8)","").replaceAll("([a-z])([A-Z])", "$1 $2");

            if (name.contains("YCbCr888")){
                name = name.replace("YCbCr888", "Aligned");
            }
            imageTypeNames[t.ordinal()] = name;
        }
        RenderedImage.ImageType defaultImageType = RenderedImage.ImageType.BlendedMSXRGBA8888Image;
        frameProcessor = new FrameProcessor(this, this, EnumSet.of(defaultImageType));

        // From activity onStart

        try {
            Device.startDiscovery(this, this);
        }catch(IllegalStateException e){
            // it's okay if we've already started discovery
        }

        // Fake pressing simulate button

        if (MainActivity.getSimulatePref(this)) {
            onConnectSimClicked(null);
        }

        // Fake pressing image capture button

        imageCaptureRequested = true;

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(LOG_TAG, "onStartCommand");

        super.onStartCommand(intent, flags, startId);

        startForeground(ForegroundNotification.NOTIFICATION_ID, ForegroundNotification.show(this));

        return START_STICKY;
    }

    static final String ACTION_USB_PERMISSION = "com.android.example.USB_PERMISSION";
    static final String ACTION_USB_DISCONNECT = "android.hardware.usb.action.USB_DEVICE_DETACHED";
    static final String ACTION_USB_CONNECT = "android.hardware.usb.action.USB_DEVICE_ATTACHED";

    //ImageView thermalImageView;
    private volatile boolean imageCaptureRequested = false;
    private volatile Socket streamSocket = null;
    private boolean chargeCableIsConnected = true;

    private int deviceRotation= 0;


    private volatile Device flirOneDevice;
    private FrameProcessor frameProcessor;

    private String lastSavedPath;

    final File outputDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);

    private Device.TuningState currentTuningState = Device.TuningState.Unknown;
    // Device Delegate methods

    // Called during device discovery, when a device is connected
    // During this callback, you should save a reference to device
    // You should also set the power update delegate for the device if you have one
    // Go ahead and start frame stream as soon as connected, in this use case
    // Finally we create a frame processor for rendering frames

    public void onDeviceConnected(Device device){
        Log.i(LOG_TAG, "Device connected!");

        flirOneDevice = device;
        flirOneDevice.setPowerUpdateDelegate(this);
        flirOneDevice.startFrameStream(this);

    }

    /**
     * Indicate to the user that the device has disconnected
     */
    public void onDeviceDisconnected(Device device){
        Log.i(LOG_TAG, "Device disconnected!");

        flirOneDevice = null;
    }

    /**
     * If using RenderedImage.ImageType.ThermalRadiometricKelvinImage, you should not rely on
     * the accuracy if tuningState is not Device.TuningState.Tuned
     * @param tuningState
     */
    public void onTuningStateChanged(Device.TuningState tuningState){
        Log.i(LOG_TAG, "Tuning state changed changed!");

        currentTuningState = tuningState;

    }

    @Override
    public void onAutomaticTuningChanged(boolean deviceWillTuneAutomatically) {

    }
    private ColorFilter originalChargingIndicatorColor = null;
    @Override
    public void onBatteryChargingStateReceived(final Device.BatteryChargingState batteryChargingState) {
        Log.i(LOG_TAG, "Battery charging state received! + batteryChargingState");
        
    }
    @Override
    public void onBatteryPercentageReceived(final byte percentage){
        Log.i(LOG_TAG, "Battery percentage received!" + String.valueOf((int) percentage) + "%");
        
    }

    private void updateThermalImageView(final Bitmap frame){
        Log.d(LOG_TAG, "updateThermalImageView");

        //imageCaptureRequested = true;
    }

    // StreamDelegate method
    public void onFrameReceived(Frame frame){
        Log.v(LOG_TAG, "Frame received!");

        if (currentTuningState != Device.TuningState.InProgress){
            frameProcessor.processFrame(frame);
        }
    }

    private Bitmap thermalBitmap = null;

    // Frame Processor Delegate method, will be called each time a rendered frame is produced
    public void onFrameProcessed(final RenderedImage renderedImage){
        Log.v(LOG_TAG, "Frame processed!");

        long startTime = System.nanoTime();
        if (renderedImage.imageType() == RenderedImage.ImageType.VisualJPEGImage){
            final Bitmap visBitmap = BitmapFactory.decodeByteArray(renderedImage.pixelData(), 0, renderedImage.pixelData().length);

            // we must rotate the raw visual JPEG to match phone/tablet screen
            android.graphics.Matrix mtx = new android.graphics.Matrix();
            mtx.postRotate(90);
            final Bitmap rotatedVisBitmap = Bitmap.createBitmap(visBitmap, 0, 0, visBitmap.getWidth(), visBitmap.getHeight(), mtx, true);
            updateThermalImageView(rotatedVisBitmap);
        }
        else {
            if (thermalBitmap == null || (renderedImage.width() != thermalBitmap.getWidth() || renderedImage.height() != thermalBitmap.getHeight())) {
                Log.d(LOG_TAG, "Creating thermalBitmap with dimensions: " + renderedImage.width() + "x" + renderedImage.height() );
                thermalBitmap = Bitmap.createBitmap(renderedImage.width(), renderedImage.height(), Bitmap.Config.ARGB_8888);

            }
            if (renderedImage.imageType() == RenderedImage.ImageType.ThermalRadiometricKelvinImage){
                /**
                 * Here is a simple example of showing color for 9 bands of tempuratures:
                 * Below 0 celceus is black
                 * 0-10C is dark blue
                 * 10-20C is light blue
                 * 20-36C is green
                 * 36-40C is dark red (human body)
                 * 40-50C is bright red
                 * 50-60C is orange
                 * 60-100C is yellow
                 * Above 100C is white
                 */
                short[] shortPixels = new short[renderedImage.pixelData().length / 2];
                byte[] argbPixels = new byte[renderedImage.width() * renderedImage.height() * 4];
                // Thermal data is little endian.
                ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);
                final byte aPixValue = (byte)255;
                for (int p = 0; p < shortPixels.length; p++) {
                    int destP = p * 4;
                    int tempInC = (shortPixels[p]-27315)/100;
                    byte rPixValue;
                    byte gPixValue;
                    byte bPixValue;
                    if (tempInC < 0){
                        rPixValue = gPixValue = bPixValue = 0;
                    }else if (tempInC < 10){
                        rPixValue = gPixValue = 0;
                        bPixValue = 127;
                    }else if (tempInC < 20){
                        rPixValue = gPixValue = 0;
                        bPixValue = (byte)255;
                    }else if (tempInC < 36){
                        rPixValue = bPixValue = 0;
                        gPixValue = (byte)160;
                    }else if (tempInC < 40){
                        bPixValue = gPixValue = 0;
                        rPixValue = 127;
                    }else if (tempInC < 50){
                        bPixValue = gPixValue = 0;
                        rPixValue = (byte)255;
                    }else if (tempInC < 60){
                        rPixValue = (byte)255;
                        gPixValue = (byte)166;
                        bPixValue = 0;
                    }else if (tempInC < 100){
                        rPixValue = gPixValue = (byte)255;
                        bPixValue = 0;
                    }else{
                        bPixValue = rPixValue = gPixValue = (byte)255;
                    }
                    // alpha always high
                    argbPixels[destP + 3] = aPixValue;
                    // red pixel
                    argbPixels[destP] = rPixValue;
                    argbPixels[destP + 1] = gPixValue;
                    argbPixels[destP + 2] = bPixValue;
                }
                thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(argbPixels));

            }
            else if(renderedImage.imageType() == RenderedImage.ImageType.ThermalLinearFlux14BitImage) {
                /**
                 * Here is an example of how to apply custom pseudocolor to a 14 bit greyscale image
                 * This example crates a 768-color Black->Green->Aqua->White by linearly mapping
                 * RGB values. Try experimenting with different color mapping approaches.
                 *
                 * This example normalizes the scene linearly. If you want to map colors to temperatures,
                 * use the Radiometic Kelvin image type and do not apply a scale as done below.
                 */

                short[] shortPixels = new short[renderedImage.pixelData().length / 2];
                byte[] argbPixels = new byte[renderedImage.width() * renderedImage.height() * 4];
                // Thermal data is little endian.
                ByteBuffer.wrap(renderedImage.pixelData()).order(ByteOrder.LITTLE_ENDIAN).asShortBuffer().get(shortPixels);
                int minValue = 65535;
                int maxValue = 0;
                for (int p = 0; p < shortPixels.length; p++) {
                    minValue = Math.min(minValue, shortPixels[p]);
                    maxValue = Math.max(maxValue, shortPixels[p]);
                }
                int range = (maxValue - minValue);
                float scale = ((float) 767 / (float) range);
                for (int p = 0; p < shortPixels.length; p++) {
                    int destP = p * 4;

                    short pixelValue = (short)((shortPixels[p] - minValue) * scale);
                    byte redValue = 0;
                    byte greenValue;
                    byte blueValue = 0;
                    if (pixelValue < 256){
                        greenValue = (byte)pixelValue;
                    }else if (pixelValue < 512){
                        greenValue = (byte)255;
                        blueValue = (byte)(pixelValue - 256);
                    }else{
                        greenValue = (byte)255;
                        blueValue = (byte)255;
                        redValue = (byte)(pixelValue -512);
                    }


                    // alpha always high
                    argbPixels[destP + 3] = (byte) 255;
                    // red pixel
                    argbPixels[destP] = redValue;
                    argbPixels[destP + 1] = greenValue;
                    argbPixels[destP + 2] = blueValue;
                }

                thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(argbPixels));
            } else {
                Log.e(LOG_TAG, "width: "+renderedImage.width()+", height: "+renderedImage.height());

                thermalBitmap.copyPixelsFromBuffer(ByteBuffer.wrap(renderedImage.pixelData()));
            }

            updateThermalImageView(thermalBitmap);

            /*
                    Capture this image if requested.
                    */
            if (this.imageCaptureRequested && null != thermalBitmap) {
                imageCaptureRequested = false;
                final Context context = this;
                new Thread(new Runnable() {
                    public void run() {
                        try{

                            // Save to temp file
                            final File outputFile = File.createTempFile("temp-flir-saving", ".jpg", outputDir);
                            final OutputStream outputStream = new FileOutputStream(outputFile);

                            thermalBitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream);

                            // XXX gives invalid image
                            //renderedImage.getFrame().save(outputStream, RenderedImage.Palette.Iron);

                            // XXX buggy, leaves file locked/in-use
                            //renderedImage.getFrame().save(outputFile.getPath(), RenderedImage.Palette.Iron, RenderedImage.ImageType.BlendedMSXRGBA8888Image);

                            outputStream.close();

                            // Rename to file name format Unity looks for
                            String path = outputDir.toString();
                            String fileName = "FLIROne-" + lPadZero(SystemClock.elapsedRealtimeNanos(), 19) + ".jpg";
                            lastSavedPath = path+ "/" + fileName;
                            final File finalOutputFile = new File(lastSavedPath);
                            finalOutputFile.delete();
                            outputFile.renameTo(finalOutputFile);

                            Log.d(LOG_TAG, "***Lance*** thermal image saved to path: " + lastSavedPath);
                            //Toast.makeText(BackgroundService.this, "Saved to: " + lastSavedPath, Toast.LENGTH_LONG).show();

                            imageCaptureRequested = true;

                        }catch (Exception e){
                            Log.e(LOG_TAG, "Error saving frame", e);
                        }
                    }
                }).start();
            }
            long endTime = System.nanoTime();
            long duration = (endTime - startTime);
            Log.d(LOG_TAG, "Duration: "+(duration/1000000)+"ms");

        }
    }

    /**
     * @param in The long value
     * @param fill The number of digits to fill
     * @return The given value left padded with the given number of digits
     */
    public static String lPadZero(long in, int fill){

        boolean negative = false;
        long value, len = 0;

        if(in >= 0){
            value = in;
        } else {
            negative = true;
            value = - in;
            in = - in;
            len ++;
        }

        if(value == 0){
            len = 1;
        } else{
            for(; value != 0; len ++){
                value /= 10;
            }
        }

        StringBuilder sb = new StringBuilder();

        if(negative){
            sb.append('-');
        }

        for(int i = fill; i > len; i--){
            sb.append('0');
        }

        sb.append(in);

        return sb.toString();
    }

    public void onTuneClicked(View v){
        if (flirOneDevice != null){
            flirOneDevice.performTuning();
        }

    }
    public void onCaptureImageClicked(View v){


        // if nothing's connected, let's load an image instead?

        //if(flirOneDevice == null && lastSavedPath != null) {
        // load!
        //    File file = new File(lastSavedPath);


        //    Frame frame = new Frame(file);

        // load the frame
        //    onFrameReceived(frame);
        //} else {
        this.imageCaptureRequested = true;
        //}
    }
    public void onConnectSimClicked(View v){
        if(flirOneDevice == null){
            Log.d(LOG_TAG, "Starting simulation");

            try {
                flirOneDevice = new SimulatedDevice(this, getResources().openRawResource(R.raw.sampleframes), 10);
                flirOneDevice.setPowerUpdateDelegate(this);
                chargeCableIsConnected = true;
            } catch(Exception ex) {
                flirOneDevice = null;
                Log.w(LOG_TAG, "IO EXCEPTION", ex);
                ex.printStackTrace();
            }
        }else if(flirOneDevice instanceof SimulatedDevice) {

            Log.d(LOG_TAG, "Stopping simulation");

            flirOneDevice.close();
            flirOneDevice = null;
        }
    }

    public void onSimulatedChargeCableToggleClicked(View v){
        if(flirOneDevice instanceof SimulatedDevice){
            chargeCableIsConnected = !chargeCableIsConnected;
            ((SimulatedDevice)flirOneDevice).setChargeCableState(chargeCableIsConnected);
        }
    }


    @Override
    public void onDestroy() {
        // We must unregister our usb receiver, otherwise we will steal events from other apps
        Log.e(LOG_TAG, "onDestroy, stopping discovery!");
        Device.stopDiscovery();
        super.onDestroy();
    }


}
