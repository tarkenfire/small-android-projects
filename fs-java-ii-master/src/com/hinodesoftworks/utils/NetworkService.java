/* 
 * Date: Oct 3, 2013
 * Project: LFMTopTracker
 * Package: com.hinodesoftworks.utils
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.utils;

import java.net.MalformedURLException;
import java.net.URL;

import com.hinodesoftworks.lfmtoptracker.AlbumQueryFragment;
import com.hinodesoftworks.netutils.ConnectionManager;

import android.app.Activity;
import android.app.IntentService;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.util.Log;

/**
 * The Class NetworkService. Handles API calls in a seperate thread.
 */
public class NetworkService extends IntentService
{
	public final static String FILE_NAME = "data";
	DataIOController dataIOController;

	/**
	 * Instantiates a new network service.
	 */
	public NetworkService()
	{
		super("NetworkService");
		Log.i("Progress", "Created service");
		
		dataIOController = DataIOController.getInstance();
	}

	/* (non-Javadoc)
	 * @see android.app.IntentService#onHandleIntent(android.content.Intent)
	 */
	@Override
	protected void onHandleIntent(Intent intent)
	{
		Log.e("Progress", "in service");
		
		Bundle extras = intent.getExtras();
		Messenger msgr = (Messenger)extras.get("messenger");

		Message msg = Message.obtain();
		msg.arg1 = Activity.RESULT_OK;
		
		Log.i("Net Connection", ConnectionManager.isNetworkConnected(this) ? "Connected" : "Not Connected");
		
		int mode = extras.getInt("mode");
		String query = extras.getString("query");
		
		try
		{	
			String dataString = ConnectionManager.getJSONStringFromURL(createURL(query, mode));
			dataIOController.writeFileToSystem(FILE_NAME, dataString);
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		try
		{
			msgr.send(msg);
		}
		catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
	}
	
	/**
	 * Creates the url from a query string and a mode number, provded in the main activity.
	 *
	 * @param query the query
	 * @param mode the mode
	 * @return the url
	 * @throws MalformedURLException the malformed url exception
	 */
	private URL createURL(String query, int mode) throws MalformedURLException
	{
		String urlString = null;
		
		//"clean" query
		String trimmedQuery = query.trim();
		String cleanQuery= trimmedQuery.replaceAll("\\s+","+");
		
		
		switch (mode)
		{
		case AlbumQueryFragment.MODE_UID:
			urlString = "http://ws.audioscrobbler.com/2.0/?method=user.gettopalbums&user="+ cleanQuery +"&api_key=907ac9ed5a3384eae314b98362049d33&format=json";
			break;
		case AlbumQueryFragment.MODE_ARTIST:
			urlString = "http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist="+ cleanQuery +"&autocorrect=1&api_key=907ac9ed5a3384eae314b98362049d33&format=json"; 
			break;
		case AlbumQueryFragment.MODE_TAG:
			urlString="http://ws.audioscrobbler.com/2.0/?method=tag.gettopalbums&tag=" + cleanQuery + "&api_key=907ac9ed5a3384eae314b98362049d33&format=json";
			break;
		
		}
		Log.i("query", urlString);
		return new URL(urlString);
		
	}
	
}
