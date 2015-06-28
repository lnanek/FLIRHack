using UnityEngine;
using System.Collections;
using System.IO;
using System.Linq;
using System;

public class GUITest : MonoBehaviour {

	private bool DEBUG_CALL_ANDROID = true;

	private Texture2D displayingTexture ;
	
	private Texture2D loadingTexture;

	private string lastLoadedFilePath = null;

	private bool isLoading;

	private bool isFirstLoaded = false;

	// Use this for initialization
	void Start () {
		Debug.Log("Start");

		displayingTexture = new Texture2D(512, 512);  
		loadingTexture = new Texture2D(512, 512);  
	}
	
	// Update is called once per frame
	void Update () {
		//Debug.Log("Update");

		// If we're not loading a texture
		if (!isLoading) {

			// If a texture has been loaded, swap texture we are loading into and texture we are displaying
			if (isFirstLoaded) {
				Texture2D previousDisplayTexture = displayingTexture;
				displayingTexture = loadingTexture;
				loadingTexture = previousDisplayTexture;
			}

			// Start loading a texture
			isLoading = true;
			StartCoroutine("load_image");
		}	
	}
	
	void OnGUI () {
		//Debug.Log("OnGUI");

		if (!isFirstLoaded) {
			Debug.LogError("No Texture loaded yet.");
		} else {

			float left = (Screen.width - displayingTexture.width) / 2;
			float top = (Screen.height - displayingTexture.height) / 2;
			Rect centered = new Rect(left, top, displayingTexture.width, displayingTexture.height);

			float imageAspect = 0F; // Preserve aspect ratio
			GUI.DrawTexture(centered, displayingTexture, ScaleMode.ScaleToFit, true, imageAspect);
		}

		GUI.Box(new Rect(10,10,500,230), "Thermal Control");
		
		if(GUI.Button(new Rect(20,80,480,40), "Background Capture")) {
			Debug.Log("StartBackgroundCapture Pressed");
			StartBackgroundCapture();
		}
		
		if(GUI.Button(new Rect(20,140,480,40), "Background Capture Simulated")) {
			Debug.Log("StartBackgroundCaptureSimulated Pressed");
			StartBackgroundCaptureSimulated();
		}
		
		if(GUI.Button(new Rect(20,200,480,40), "Stop Capture")) {
			Debug.Log("StopBackgroundCapture Pressed");
			StopBackgroundCapture();
		}
	}

	void StartBackgroundCapture() {
		if (Application.platform != RuntimePlatform.Android || !DEBUG_CALL_ANDROID) {
			return;
		}

		AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"); 
		AndroidJavaObject jo = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity"); 

		AndroidJNIHelper.debug = true; 
		using (AndroidJavaClass jc = new AndroidJavaClass("com.flir.flironeexampleapplication.ThermalControl")) { 
			jc.CallStatic("startBackgroundThermalCapture", jo); 
		} 
	}
	
	void StartBackgroundCaptureSimulated() {
		if (Application.platform != RuntimePlatform.Android || !DEBUG_CALL_ANDROID) {
			return;
		}
		
		AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"); 
		AndroidJavaObject jo = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity"); 
		
		AndroidJNIHelper.debug = true; 
		using (AndroidJavaClass jc = new AndroidJavaClass("com.flir.flironeexampleapplication.ThermalControl")) { 
			jc.CallStatic("startBackgroundThermalCaptureSimulated", jo); 
		} 
	}
	
	void StopBackgroundCapture() {
		if (Application.platform != RuntimePlatform.Android || !DEBUG_CALL_ANDROID) {
			return;
		}
		
		AndroidJavaClass unityPlayerClass = new AndroidJavaClass("com.unity3d.player.UnityPlayer"); 
		AndroidJavaObject jo = unityPlayerClass.GetStatic<AndroidJavaObject>("currentActivity"); 
		
		AndroidJNIHelper.debug = true; 
		using (AndroidJavaClass jc = new AndroidJavaClass("com.flir.flironeexampleapplication.ThermalControl")) { 
			jc.CallStatic("stopBackgroundThermalCapture", jo); 
		} 
	}

	IEnumerator load_image_after(float waitTime) {
		Debug.Log("load_image_after");

		yield return new WaitForSeconds(waitTime);

		StartCoroutine("load_image");
	}

	IEnumerator load_image()
	{
		Debug.Log("load_image");

		// get every file in chosen directory with the extension .jpg

		string pathForThermalImages = Application.dataPath + "/../..";
		if (Application.platform == RuntimePlatform.Android) {
			pathForThermalImages = "/sdcard/Pictures/";
		}

		string[] filePaths = Directory.GetFiles(pathForThermalImages, "FLIROne-*.jpg"); 
		if (filePaths.Length == 0) {
			Debug.Log("no FLIR files found");
			isLoading = false;
			yield return null;
		}

		// Read the newest file
		Array.Sort(filePaths);
		Array.Reverse(filePaths);
		string newestFilePath = filePaths[0];

		// Delete older files
		if (filePaths.Length > 1) {
			for(int i = 1; i < filePaths.Length; i++) {
				File.Delete(filePaths[i]);
			}
		}

		// If we already loaded the image, ignore it
		if ( null != lastLoadedFilePath ) {
			if ( 0 == String.Compare(lastLoadedFilePath, newestFilePath) ) {
				Debug.Log("newest file is same as last loaded, stopping");
				isLoading = false;
				yield return null;
			}
		}
		
		// "download" the first file from disk
		WWW www = new WWW("file://" + newestFilePath);                  

		// Wait unill its loaded
		yield return www;                                                               

		// put the downloaded image file into the Texture2D
		www.LoadImageIntoTexture(loadingTexture);                     

		lastLoadedFilePath = newestFilePath;

		isLoading = false;
		isFirstLoaded = true;
	}

}
	