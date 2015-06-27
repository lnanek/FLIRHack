This example shows how to interact with Java code without having to write the JNI trampoline in native code.
It uses a prebuilt libaray (libjni.so) and C# code (JavaVM.cs / JNI.cs) to wrap the native code into C# space.

You will need:
	* Unity 3.1 - http://unity3d.com/unity/download/
	* Android SDK - http://developer.android.com/sdk/index.html
	* ANT Java build tool - http://ant.apache.org/
to build on this example. If you want to recompile the native library (libjni.so) you will need the Android NDK as well.

To build Java code use ANT:
	$ ant build-jar

To rebuild natice code use the NDK / MAKE scripts:
	$ $ANDROID_NDK_ROOT/ndk-build

