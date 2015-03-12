package org.thezero.bitzero;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;

public class Utils {
	public static String File_BackUp="lol";
	public static String File_Change="back";
	public static String File_Temp="temp";
	
	public static void Save(Context c, String path, String s) {
		FileOutputStream fos;
		try {
			fos = c.openFileOutput(path, Context.MODE_PRIVATE);
			fos.write(s.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	public static void Append(Context c, String path, String s,boolean p) {
		FileOutputStream fos;
		try {
			fos = c.openFileOutput(path, Context.MODE_APPEND);
			if (p == true){
				fos.write("\n".getBytes());
			}
			fos.write(s.getBytes());
			fos.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String Load(Context c, String path) {
		FileInputStream fin = null;
		String s = null;
		try {
			fin = c.openFileInput(path);
			InputStream is = fin;
			s = convertStreamToString(is);
			fin.close();
		} catch (FileNotFoundException e){
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return s;
	}
	
	public static boolean CheckFile(Context c, String path){
		boolean r=false;
		File file = c.getFileStreamPath(path);
		if(file.exists()){
			r=true;
		}
		return r;
	}
	
	public static String convertStreamToString(java.io.InputStream is) {
	    java.util.Scanner s = new java.util.Scanner(is).useDelimiter("\\A");
	    return s.hasNext() ? s.next() : "";
	}
}
