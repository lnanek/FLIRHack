# FLIRHack
FLIR Hackathon

Instructions:  
  
Unity:  
  
1) Install UnityAndroidApp.apk 
2) Choose to start simulated streaming or camera streaming from top left menu
3) Images will stream into texture on screen
4) Press stop button to stop streaming

Compilation:  
  
To compile on Unity, you have to update the Unity AndroidManifest.xml located here:  
/Applications/Unity/Unity.app/Contents/PlaybackEngines/AndroidPlayer/AndroidManifest.xml  

Add this line:  
<uses-sdk android:minSdkVersion="18" android:targetSdkVersion="21" />  
   
Inside the manifest tag.  

Nurse Therma:  
    
1) Install FLIRSDKDemo.apk . Run Nurse Therma. Switch to kelvin view before taking pictures to enable temperature measurements.  
  
