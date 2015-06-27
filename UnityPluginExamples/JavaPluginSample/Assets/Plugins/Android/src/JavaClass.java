package org.example.ScriptBridge;

import android.app.Activity;
import android.util.Log;
import java.io.File;

public class JavaClass
{
	private Activity mActivity;
	public JavaClass(Activity currentActivity)
	{
		Log.i("JavaClass", "Constructor called with currentActivity = " + currentActivity);
		mActivity = currentActivity;
	}
	
	// we could of course do this straight from native code using JNI, but this is an example so.. ;)
	public String getActivityCacheDir()
	{
		// calling Context.getCacheDir();
		// http://developer.android.com/reference/android/content/Context.html#getCacheDir()
		//
		File cacheDir = mActivity.getCacheDir();
		String path = cacheDir.getPath();
		Log.i("JavaClass", "getActivityCacheDir returns = " + path);
		return path;
	}
}
