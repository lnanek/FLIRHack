using UnityEngine;
using System.Collections;
using System.IO;

public class GUITest : MonoBehaviour {

	public Texture aTexture;

	// Use this for initialization
	void Start () {
		StartCoroutine("load_image");
	}
	
	// Update is called once per frame
	void Update () {
	
	}
	
	void OnGUI () {

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

	IEnumerator load_image()
	{
		// get every file in chosen directory with the extension .jpg

		string pathForThermalImages = Application.dataPath + "/../..";
		if (Application.platform == RuntimePlatform.Android) {
			pathForThermalImages = "/sdcard/Pictures/";
		}

		string[] filePaths = Directory.GetFiles(pathForThermalImages, "FLIROne-*.jpg"); 

		// "download" the first file from disk
		WWW www = new WWW("file://" + filePaths[0]);                  

		// Wait unill its loaded
		yield return www;                                                               

		// create a new Texture2D (you could use a gloabaly defined array of Texture2D )
		Texture2D new_texture = new Texture2D(512,512);              

		// put the downloaded image file into the new Texture2D
		www.LoadImageIntoTexture(new_texture);                     

		// put the new image into the current material as defuse material for testing.
		//this.renderer.material.mainTexture = new_texture;           

		aTexture = new_texture;
	}

}
	