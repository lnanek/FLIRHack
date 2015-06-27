using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;
using System;

public class CallJavaCode : MonoBehaviour {

	private IntPtr	JavaClass;
	private int		getActivityCacheDir;
	void Start ()
	{
		// attach our thread to the java vm; obviously the main thread is already attached but this is good practice..
		JavaVM.AttachCurrentThread();

		// first we try to find our main activity..
		IntPtr cls_Activity	= JNI.FindClass("com/unity3d/player/UnityPlayer");
		int fid_Activity	= JNI.GetStaticFieldID(cls_Activity, "currentActivity", "Landroid/app/Activity;");
		IntPtr obj_Activity	= JNI.GetStaticObjectField(cls_Activity, fid_Activity);
		Debug.Log("obj_Activity = " + obj_Activity);
		
		// create a JavaClass object...
		IntPtr cls_JavaClass	= JNI.FindClass("org/example/ScriptBridge/JavaClass");
		int mid_JavaClass		= JNI.GetMethodID(cls_JavaClass, "<init>", "(Landroid/app/Activity;)V");
		IntPtr obj_JavaClass	= JNI.NewObject(cls_JavaClass, mid_JavaClass, obj_Activity);
		Debug.Log("JavaClass object = " + obj_JavaClass);

		// create a global reference to the JavaClass object and fetch method id(s)..
		JavaClass			= JNI.NewGlobalRef(obj_JavaClass);
		getActivityCacheDir	= JNI.GetMethodID(cls_JavaClass, "getActivityCacheDir", "()Ljava/lang/String;");
		Debug.Log("JavaClass global ref = " + JavaClass);
		Debug.Log("JavaClass method id = " + getActivityCacheDir);
	}

	private string cacheDir = "Push to get cache dir";
	void OnGUI ()
	{
		if (GUI.Button(new Rect (15, 125, 450, 100), cacheDir))
		{
			String cache = getCacheDir();
			Debug.Log("getCacheDir returned " + cache);
			cacheDir = cache;
		}
	}
	
	private string getCacheDir()
	{
		// again, make sure the thread is attached..
		JavaVM.AttachCurrentThread();

		// get the Java String object from the JavaClass object
		IntPtr str_cacheDir 	= JNI.CallObjectMethod(JavaClass, getActivityCacheDir);
		Debug.Log("str_cacheDir = " + str_cacheDir);
	
		// convert the Java String into a Mono string
		IntPtr stringPtr = JNI.GetStringUTFChars(str_cacheDir, 0);
		Debug.Log("stringPtr = " +stringPtr);
		String cache = Marshal.PtrToStringAnsi(stringPtr);
		JNI.ReleaseStringUTFChars(str_cacheDir, stringPtr);

		Debug.Log("return value is = " + cache);

		return cache;
	}

}
