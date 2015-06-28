# FLIRHack
FLIR Hackathon

Instructions:  
  
1) Install FLIRSDKDemo.apk . This is the FLIR SDK Demo app modified to save images even when showing simulation frames.  
adb uninstall com.flir.flironeexampleapplication  
adb install FLIRSDKDemo.apk    
  
2) Start FLIR One Example app  
  
3a) Attach FLIR One, press power button, wait to blink green. Click BACKGROUND CAPTIRE button. Press OK on USB access dialog.

3b) Alternatively, click BACKGROUND SIMULATED CAPTURE button to fake frames without a device.
  
5) Install Unity app.
adb install UnityAndroidApp.apk    
  
6) Run Unity App  
  
7) Thermal images are displayed as loaded

Compilation:  
  
To compile on Unity, you have to update the Unity AndroidManifest.xml located here:  
/Applications/Unity/Unity.app/Contents/PlaybackEngines/AndroidPlayer/AndroidManifest.xml  

Add this line:  
<uses-sdk android:minSdkVersion="18" android:targetSdkVersion="21" />  
   
Inside the manifest tag.  
  
****
  
WiFi:
ihangar hack
summer2015

Demo: 2PM Sunday
