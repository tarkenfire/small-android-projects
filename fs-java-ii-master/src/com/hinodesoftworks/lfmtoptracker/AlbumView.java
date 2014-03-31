/* 
 * Date: Oct 3, 2013
 * Project: LFMTopTracker
 * Package: com.hinodesoftworks.lfmtoptracker
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.lfmtoptracker;

import java.io.File;

import com.hinodesoftworks.lfmtoptracker.AlbumQueryFragment.AlbumQueryListener;
import com.hinodesoftworks.netutils.ConnectionManager;
import com.hinodesoftworks.utils.DataIOController;
import com.hinodesoftworks.utils.JSONFactory;
import com.hinodesoftworks.utils.NetworkService;

import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Configuration;
import android.database.Cursor;
import android.util.Log;
import android.view.Menu;
import android.widget.Toast;

/**
 * The Main Activity class of the app.
 */
public class AlbumView extends Activity implements  AlbumQueryListener
{
	//misc variables
	JSONFactory jsonFactory;
	DataIOController dataIOController;
	
	//public variables
	String instanceQueryRef = "";

	//handlers
	//android has leaking issues with non-static handlers:
	//http://stackoverflow.com/q/11407943/1067923
	@SuppressLint("HandlerLeak")
	final Handler serviceHandler = new Handler()
	{
		@Override
		public void handleMessage(Message message)
		{
			Log.i("Progress", "In Handler");
			createAndSendCursor();
		}
	};
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_view);
		
		String queryFragTag = "query_fragment";
		
		FragmentManager fragmentManager = getFragmentManager();
		
		//if this is the case, then there is no query fragment, and one must be created
		if (fragmentManager.findFragmentByTag(queryFragTag) == null)
		{
			FragmentTransaction transaction = fragmentManager.beginTransaction();
			AlbumQueryFragment aqf = new AlbumQueryFragment();
			transaction.add(R.id.query_container, aqf, queryFragTag);
			transaction.commit();
		}
	
		jsonFactory = JSONFactory.getInstance();
		dataIOController = DataIOController.getInstance(this);
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.album_view, menu);
		return true;
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState)
	{
		super.onSaveInstanceState(outState);
	}	

	//private methods	
	private void createAndSendCursor()
	{
		Uri uri = Uri.parse("content://com.hinodesoftworks.lfmtoptracker.albumprovider/items");
		
		Cursor matCursor = getContentResolver().query(uri, null, null, null, null);
		AlbumQueryFragment aqf = (AlbumQueryFragment)getFragmentManager().findFragmentByTag("query_fragment");
		
		aqf.displayQueryResults(matCursor);
		
	}
	
	@Override
	public void onAlbumSelected(String mbid)
	{
		//if display fragment already exists, just update the mbid.
		if (getFragmentManager().findFragmentByTag("display_fragment") != null)
		{
			AlbumDetailFragment adf = (AlbumDetailFragment) getFragmentManager().findFragmentByTag("display_fragment");
			adf.displayAlbumData(mbid);
			return;
		}
		
		
		if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE)
		{
			//in landscape, there is space for two fragments
			AlbumDetailFragment adf = new AlbumDetailFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			
			transaction.add(R.id.display_container, adf, "display_fragment");
			transaction.commit();
			
			adf.displayAlbumData(mbid);
		}
		else
		{
			//in portrait, second fragment must replace first fragment
			
			AlbumDetailFragment adf = new AlbumDetailFragment();
			FragmentTransaction transaction = getFragmentManager().beginTransaction();
			
			transaction.replace(R.id.query_container, adf, "display_fragment");
			transaction.addToBackStack(null);
			transaction.commit();
			
			adf.displayAlbumData(mbid);
		}
		
	}

	@Override
	public void onQuery(String query, int mode)
	{
		
		instanceQueryRef = query;
		//check for network con first. 
		if (ConnectionManager.isNetworkConnected(this))
		{
			//get fresh data
			Messenger msgr = new Messenger(serviceHandler);
			
			Intent i = new Intent(this, NetworkService.class);
			i.putExtra("messenger", msgr);
			i.putExtra("query", query);
			i.putExtra("mode", mode);
			
			startService(i);
		}
		else //check if there is a cached version.
		{
			File cache = this.getFileStreamPath(NetworkService.FILE_NAME);
			if (cache.exists())
			{
				Toast.makeText(this, R.string.toast_no_network, Toast.LENGTH_LONG).show();
				
			}
			else // no cache, no network, app cannot display any data.
			{
				Toast.makeText(this, R.string.toast_no_cache, Toast.LENGTH_LONG).show();
				return;
			}		
		}
	}

	@Override
	public void onQueryFilter(String filter)
	{
		Uri uri = Uri.parse("content://com.hinodesoftworks.lfmtoptracker.albumprovider/filtered");
		
		Cursor matCursor = getContentResolver().query(uri, null, filter, null, null);
		
		AlbumQueryFragment aqf = (AlbumQueryFragment)getFragmentManager().findFragmentByTag("query_fragment");
		
		aqf.displayQueryResults(matCursor);
		
	}	
	
	//utility methods
	/**
	 * Allows logging of strings longer than logcat's 4000 character limit.
	 *
	 * @param str the string to log
	 */
	public static void longInfo(String str) 
	{
	    if(str.length() > 4000) 
	    {
	        Log.i("log", str.substring(0, 4000));
	        longInfo(str.substring(4000));
	    } 
	    else
	        Log.i("log", str);
	}


}
