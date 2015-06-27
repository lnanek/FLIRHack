FLIR ONE Android SDK - BETA 2
===================
Developer Guide
---------------

Introduction:

Welcome to FLIR ONE Android app development. This document will introduce you to developing apps with the FLIR ONE accessory for Android (aka the FLIR ONE device). Note that this is not to be confused with the FLIR ONE Android app (aka the FLIR ONE app).
This document is intended to serve as a quick start guide. It will introduce the developer to all of the important terms and methodologies related to the SDK. This document will help give an overall understanding of how the SDK works. Answers to commonly asked questions are also included.
This Documentation was written for the FLIR ONE SDK – BETA release. Updates to the SDK and documentation will be rolled out soon. For any support questions, contact <FLIROneSDK@FLIR.com>. 

The FLIR ONE Android SDK allows you to receive, save, and load Frames from the camera in formats including MSX, Colorized thermal, and radiometric (kelvin) formats.


Table of Contents
===

0. Prerequisites
---
* Android Development Basics

1. Initial Project Setup
---
* Development environment requirements
* Supported target hardware
* Using the Android Library (AAR) file in a new or existing project
* Using the bundled Example App as a starting point

2. Getting Up and Running: Streaming Frames
---
* Establishing USB Connectivity
* Creating a Device Object
* Implementing Callbacks and Rendering
* Rendering and Displaying Frames
* Understanding Frame and RenderedFrame classes

3. Using the FLIROneSDK, delegation and using delegates
---
* Connecting and Disconnecting the FLIR ONE device
* Streaming Frames
* Device state updates

4. Tuning the FLIR ONE device
---
* What is Tuning?
* How to present tuning options in the application

5. Streaming frames from the FLIR ONE device
---
* Controlling the frame stream 
* What is the `FrameProcessor` class?
* How to save an image

6. Managing and editing media collected by the application
---
* Using Android Best Practices for saved media


7. Saving and Accessing images on disk
---
* Saving a Frame instance to disk with options
* Rendering a saved Frame instance

8. Common pitfalls and questions
---
* How are images saved?
* How to edit an image?
* Why am I not getting live streamed images?
* Why am I not able to `import com.flir.flirone.*`?
* How do I get the temperature out of the pixels?
* How do I change the color palette of an image?
* How do I get information about the FLIR ONE device battery level?
* What is FLIR Tools?

0. Prerequisites
===
Much of this guide assumes some familiarity with Android application development, the Java
programming language, and common computer image storage and processing techniques. The following
guides will prove helpful for those less familiar with these topics:

* [Android Developers Portal](https://developer.android.com/index.html)
* [Get Android Studio](https://developer.android.com/sdk/index.html)
* [Android Bitmap Class](https://developer.android.com/reference/android/graphics/Bitmap.html)


1. Initial Project Setup
===
Development environment requirements
---
The SDK and Example App have been developed with Android Studio 1.2 or later in mind.
Use of older versions of Android Studio or Eclipse is not supported.


Supported target hardware
---
The SDK can be run on any Android device with an ARMv7 CPU and Android 4.0 or greater. 
In order to connect to a FLIR One device, the Android device must support USB host mode. 
The SDK can be used on Android devices without USB host mode support, as well as Android 
Virtual Devices, but use is limited to the simulator and processing frames.

For a complete listing of supported devices, please refer to the FLIR One website.

Using the Android Library (AAR) file in a new or existing project
---
You can import the `flironesdk.aar` file in a new or existing Android Studio app project.

This file is required and must be downloaded from FLIR. The steps required to install these files along with any other project dependencies are included below.
All required external files are likely to be provided at the same place as this document. For any support questions, contact <FLIROneSDK@FLIR.com>.

You must import the `flironesdk.aar` library file into your Android Studio project. This is most easily done in a 2 step process:
1. Use "Import .JAR or .AAR Package" wizard (via `File→New→New Module`) and select the `flironesdk.aar`
2. Add the flironesdk module as a dependency of your app module: select `File→Project Structure`, select you app module, go to the Dependancies tab, click the plus (+) icon and select "Module Dependency" to select the flironesdk module.

Note: The above method of importing the flironesdk.aar module requires Android Studio 1.2 or greater.

Using the bundled Example App as a starting point
---
It is highly recommended that developers examine the source code of the example app as a first
point of reference. The example app can also be used as the base for a new app, since all the 
basic requirements of a FLIR ONE app are present.

2. Getting Up and Running: Streaming Frames
===

Establishing USB Connectivity
---

The first step in connecting to the FLIR One device is to establish a USB connection to it.

### Device.startDiscovery

In your main activity's onResume method, call Device.startDiscovery and in onPause call Device.stopDiscovery()

Your device delegate that you pass to startDiscovery will have its onDeviceConnected method called when a
FLIR One is attached to your phone and the user has given your app permission to use it.

See example app as a guide.

### Application Manifest Device Filter
If you want to prompt the user to open your app when the device is connected, 
add the following to your AndroidManifest.xml
```
	<intent-filter>
		<action android:name="android.intent.action.MAIN" />
		<action android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED" />
		<category android:name="android.intent.category.LAUNCHER" />
	</intent-filter>
	<meta-data android:name="android.hardware.usb.action.USB_DEVICE_ATTACHED"
		android:resource="@xml/device_filter" />
```
And add a file `res/xml/device_filter.xml` with
```
<?xml version="1.0" encoding="utf-8"?>
<resources>
    <usb-device vendor-id="2507" product-id="6550" />
</resources>
```




Getting a Device Object
---
The first step to receiving frames from the FLIR ONE is to create a Device.Delegate object, and use
`Deivce.startDiscovery` to tell the device discovery service to check for a FLIR One device being connected.
The Device.Delegate interface defines methods to be called for device-specific events.


Implementing Callbacks and Rendering
---
Following the same pattern as the Device constructor, the `startFrameStream` method requires 
a Device.StreamDelegate instance to be passed. This delegate handles the receiving of Frame
objects which represent the raw data received from the device on each frame. Raw frames are 
rendered to usable image formats by an instance of the `FrameProcessor` class. 


*Note:* Multiple frame processors can be created, and each can be configured with multiple frame
formats, but keep in mind that processing multiple frame types and palettes requires high
performance hardware.

Implement a `Device.StreamDelegate` instance with the `onFrameReceived` method to receive 
Frame objects in real time from the device. Then create an instance of the `FrameProcessor` 
class with your implementation of a `FrameProcessor.Delegate` and a set of rendered frame 
types and a palette. For a simple live stream application, your `onFrameReceived` method 
should pass the Frame object to your configured `FrameProcessor` instance. 

Rendering and Displaying Frames
---

`FrameProcessor.Delegate.onFrameProcessed` will be called with `RenderedFrame` objects, each
indicating its type with the `frameType` method. The type names indicate the pixel format,
and all can be easily made into Android `android.graphics.Bitmap` objects using `Bitmap.createBitmap`
, or `BitmapFactory.decodeByteArray` for the `VisualJPEGImage` type.
 
In order to receive different formats, use the `setFrameTypes` method with a Set of as many formats as you want.
Warning: you may not use the ```BlendedMSXRGBA8888Image``` and ```ThermalRGBA8888Image``` types concurrently.

*Note:* while not critical, it is recommended to use an EnumSet when calling `setFrameTypes`

### Descriptions of Rendered Frame Types
`ThermalLinearFlux14BitImage`  
Linear 14 bit image data, padded to 16 bits per pixel. This is the raw image from the thermal
image sensor.

`ThermalRGBA8888Image`  
Thermal RGBA image data, with a palette applied.

`BlendedMSXRGBA8888Image`  
MSX (thermal + visual) RGBA image data, with a palette applied. This shows an outline of 
objects using the visible light camera, overlaid on the thermal image.

`VisualJPEGImage`  
Visual JPEG image data, **unaligned** with the thermal.

`VisualYCbCr888Image`  
Visual YCbCr image data, **aligned** with the thermal. This image has been cropped and 
adjusted to line up with the thermal image. You can use this for blending in ways other
than MSX.

`ThermalRadiometricKelvinImage`

Radiometric centi-kelvin (cK) temperature data. Note that is is in centi-kelvin, so a reading 
of 31015 is equal to 310.15K (98.6ºF or 37ºC).


Selecting a Palette
---

When streaming, you can specify a palette to use by calling your FrameProcessor instance's 
`setFramePalette` method.


3. Using the FLIROneSDK, delegation and using delegates
==
Delegate interfaces allow for asynchronous events to be passed to your handler methods. Much like how
an Android Activity has onPause and onResume methods, the Device.Delegate, Device.StreamDelegate, and FrameProcessor.Delegate interfaces
define callback methods that are called when events such as a device being connected, a device sending a frame, and a frame being finished processing, and allow for
non-blocking method calls if needed. For example, you can call frameProcessor.processFrame from a separate thread


4. Tuning the FLIR ONE device
==

What is Tuning?
--

Tuning is a quick recalibration process of the IR camera in the FLIR ONE device. 
Tuning is required by the IR camera on a regular basis because the properties of the IR camera change in time based upon its internal temperature and other factors. 
For this reason tuning is required whenever the FLIR ONE device determines that the IR properties have drifted by too much from the previous tuning. 
During the tuning process, the FLIR ONE device will perform a calibration called a flat field calibration (FFC). 

How to present tuning in the application
--
During tuning, the shutter is closed and frames should not be displayed. Frames are not guaranteed to be delivered by the SDK during tuning.
The user will see this as a gap, stutter, or otherwise useless frames unless the application displays a notification.
In the example application we use a [progress bar](https://developer.android.com/reference/android/widget/ProgressBar.html) in indeterminate mode, along with a shading of the last frame received, when the tuning state is reported.


8. Common pitfalls and questions
==
How are images saved?
--

Radiometric images are images saved as “fat JPEG" files.  These images are saved with all of the meta-data required to reconstruct the radiometric data. This radiometric data includes the ability to read the temperature of a pixel at a given point and the ability to change the color palette and set the rotation.  Keeping this data intact is desired for use with FLIR Tools: For iPhone,  For OS X. The FLIR Tools application allows users extract advanced temperature readings from radiometric jpegs. Currently radiometric jpegs can be imported to the FLIR Tools application from the iPhone or shared from within an SDK using application via email. 
See documentation on saving data or check out the ```Frame``` class documentation.

How to edit or change the color palette of an image?
--

To change how a saved Frame is displayed, simply pass it to a FrameProcessor instance with
new frame type or palette options. To save the image with a new preview image format, call Frame.save with updated options.

Why am I not getting live streamed images
--
If your StreamDelegate's onFrameReceived method is not being called, make sure you passed
For further questions contact <FLIROneSDK@FLIR.com> for support.


Why am I not able to `import com.flir.flirone.*`?
--
Make sure you have imported the `flironesdk.aar` file correctly in Android Studio. Please see the Example Application's configuration for an example.

How do I get the temperature out of the pixels?
--

In order to get the temperature of a pixel, you'll need to add ```ThermalRadiometricKelvinImage``` FrameType to the image processor. Once you receive a RenderedFrame in this format, you can use the width and height supplied to find the value of a particular pixel in this array. The values represent degrees Kelvin * 100. For example, the value 273.15ºK is represented by 27,315.


How do I get information about the FLIR ONE device battery level?
--

Although it is completely optional it is a good idea to inform your user of the power level of the FLIR ONE device.
To receive power updates, implement a `Device.PowerUpdateDelegate` and pass an instance to a connected device's setPowerUpdateDelegate method.


What is FLIR Tools?
--
FLIR Tools is an application developed by FLIR for use with Mac OS X or iPhone. The FLIR Tools application allows users extract advanced temperature readings from radiometric jpegs. Currently radiometric jpegs can be imported to the FLIR Tools application from the iPhone or shared from within an SDK using application via email. See documentation on sharing and uploading data or check out the FLIROneSDKShareActivity documentation. FLIR Tools can be found here: For iPhone,  For OS X, or via a web search.
