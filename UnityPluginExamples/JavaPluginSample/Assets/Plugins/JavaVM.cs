using UnityEngine;
using System.Collections;
using System.Runtime.InteropServices;

public class JavaVM
{
	[DllImport ("jni")] public static extern int DestroyJavaVM();
	[DllImport ("jni")] public static extern int AttachCurrentThread();
	[DllImport ("jni")] public static extern int DetachCurrentThread();
	[DllImport ("jni")] public static extern int GetEnv(int version);
	[DllImport ("jni")] public static extern int AttachCurrentThreadAsDaemon();
}
