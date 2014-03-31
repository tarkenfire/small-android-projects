/* 
 * Date: Oct 7, 2013
 * Project: LFMTopTracker
 * Package: com.hinodesoftworks.utils
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.util.Log;

public class DataIOController
{
	private static DataIOController _instance = null;
	private static Context ctx = null;
	
	
	public static DataIOController getInstance() throws NullPointerException
	{
		if (ctx == null)
		{
			throw new NullPointerException();
		}
		
		return _instance;
	}
	
	public static DataIOController getInstance(Context ctx)
	{
		if(_instance == null)
		{
			_instance = new DataIOController(ctx);
		}
		return _instance;
	}
	
	private DataIOController(Context ctx)
	{
		DataIOController.ctx = ctx;
	}
	
	
	//public methods
	public String getJSONStringFromFileName(String fileName) throws IOException, FileNotFoundException
	{
		FileInputStream fin = ctx.openFileInput(NetworkService.FILE_NAME);
		Log.i("File System", "File Opened");
		
		BufferedReader reader = new BufferedReader(new InputStreamReader(fin));
		
		StringBuilder sb = new StringBuilder();
	    String line = null;
	    while ((line = reader.readLine()) != null) 
	    {
	      sb.append(line);
	    }
	    
	    return sb.toString();	
	}
	
	public void writeFileToSystem(String fileName, String content) throws IOException
	{
		OutputStreamWriter osr = new OutputStreamWriter(ctx.openFileOutput(fileName, Context.MODE_PRIVATE));
		osr.write(content);
		osr.close();
		Log.i("File System", "File Written");
		
	}

}
