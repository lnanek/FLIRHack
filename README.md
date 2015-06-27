# FLIRHack
FLIR Hackathon

Instructions:  
  
1) Install FLIRSDKDemo.apk . This is the FLIR SDK Demo app modified to save images even when showing simulation frames.  
adb uninstall com.flir.flironeexampleapplication  
adb install FLIRSDKDemo.apk    
  
2) Start FLIR One Example app  
  
3) Click start foreground button  
  
3) Press Simulation mode button in lower right corner if you do not have a FLIR One and want to use fake data.
  
4) Press camera button in middle center to save a thermal picture.
  
5) Install Unity app. So far this simply loads the thermal image into a texture and shows it on the screen.  
adb install UnityAndroidApp.apk    
  
6) Run Unity App  
  
7) Thermal image is displayed  
  
  