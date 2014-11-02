package com.nti.flux.ntiapp.helper;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class AssetsHelper {
	private static final String TAG = AssetsHelper.class.getName();

	public static void writeBytesToFile(InputStream is, File file) throws IOException{
		FileOutputStream fos = null;
		try {
			byte[] data = new byte[2048];
			int nbread = 0;
			fos = new FileOutputStream(file);
			while((nbread=is.read(data))>-1){
				fos.write(data,0,nbread);               
			}
		}
		catch (Exception ex) {
			Log.e(TAG, "Exception: " + ex);
		}
		finally{
			if (fos!=null){
				fos.close();
			}
		}
	}
	
	public static String getXmlAsString(Context context, String assetFileName){
		try{
	        BufferedReader inputReader = new BufferedReader(new InputStreamReader(context.getAssets().open(assetFileName)));
	        StringBuilder sb = new StringBuilder();
	        String inline = "";
	        try {
				while ((inline = inputReader.readLine()) != null) {
				  sb.append(inline);
				}
			} catch (IOException e) {
				Log.e(TAG, "Exception: " + e);
			}
	        return sb.toString();
		}
		catch(Exception e){
			Log.e(TAG, "Exception: " + e);
			return null;
		}
	}
}
