using UnityEngine;
using System.Collections;
using System.IO;
using System.Linq;
using System;

public class GUITest : MonoBehaviour {

	public Texture aTexture;

	string lastLoadedFilePath = null;

	bool isLoading;

	// Use this for initialization
	void Start () {
		Debug.Log("Start");

	}
	
	// Update is called once per frame
	void Update () {
		//Debug.Log("Update");

		if (!isLoading) {
			isLoading = true;
			StartCoroutine("load_image");
		}	
	}
	
	void OnGUI () {
		//Debug.Log("OnGUI");

		if (!aTexture) {
			Debug.LogError("No Texture found.");
		} else {

			float left = (Screen.width - aTexture.width) / 2;
			float top = (Screen.height - aTexture.height) / 2;
			Rect centered = new Rect(left, top, aTexture.width, aTexture.height);

			float imageAspect = 0F; // Preserve aspect ratio
			GUI.DrawTexture(centered, aTexture, ScaleMode.ScaleToFit, true, imageAspect);
		}

		// Make a background box
		GUI.Box(new Rect(10,10,100,90), "Test Label");
		
		if(GUI.Button(new Rect(20,40,80,20), "Button 1")) {
			Debug.Log("Button 1 Pressed");
		}
		
		// Make the second button.
		if(GUI.Button(new Rect(20,70,80,20), "Button 2")) {
			Debug.Log("Button 2 Pressed");
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

		// create a new Texture2D (you could use a gloabaly defined array of Texture2D )
		Texture2D new_texture = new Texture2D(512,512);              

		// put the downloaded image file into the new Texture2D
		www.LoadImageIntoTexture(new_texture);                     

		// put the new image into the current material as defuse material for testing.
		//this.renderer.material.mainTexture = new_texture;           

		aTexture = new_texture;

		lastLoadedFilePath = newestFilePath;

		//load_image_after(1.0F);

		isLoading = false;
	}

}
	