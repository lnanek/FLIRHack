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
			Debug.LogError("Assign a Texture in the inspector.");
			return;
		}
		GUI.DrawTexture(new Rect(10, 10, 300, 300), aTexture, ScaleMode.ScaleToFit, true, 10.0F);



		// Make a background box
		GUI.Box(new Rect(10,10,100,90), "Loader Menu");
		
		// Make the first button. If it is pressed, Application.Loadlevel (1) will be executed
		if(GUI.Button(new Rect(20,40,80,20), "Level 1")) {
			Application.LoadLevel(1);
		}
		
		// Make the second button.
		if(GUI.Button(new Rect(20,70,80,20), "Level 2")) {
			Application.LoadLevel(2);
		}
	}

	IEnumerator load_image()
	{
		// get every file in chosen directory with the extension .jpg
		string[] filePaths = Directory.GetFiles(@"/Users/lnanek/Desktop/FLIRHack", "*.jpg"); 

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
	