/* 
 * Date: Oct 10, 2013
 * Project: LFMTopTracker
 * Package: com.hinodesoftworks.lfmtoptracker
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.lfmtoptracker;


import java.util.ArrayList;
import java.util.Locale;

import org.json.JSONArray;
import org.json.JSONObject;

import com.hinodesoftworks.utils.DataIOController;
import com.hinodesoftworks.utils.JSONFactory;
import com.hinodesoftworks.utils.NetworkService;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.util.Log;

// TODO: Auto-generated Javadoc
/**
 * The Class AlbumProvider.
 */
public class AlbumProvider extends ContentProvider
{
	DataIOController dataIOController;
	JSONFactory jsonFactory;
	
	public static final String AUTH = "com.hinodesoftworks.lfmtoptracker.albumprovider";
	
	/**
	 * The Class AlbumData.
	 */
	public static final class AlbumData implements BaseColumns
	{
		public static final Uri CONTENT_URL = Uri.parse("content://" + AUTH + "/items");
		
		public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.hinodesoftworks.album.item";
		public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.hinodesoftworks.album.item";
	
		
		public static final String ARTIST_COLUMN = "artist";
		public static final String ALBUM_COLUMN = "album";
		public static final String ALBUM_ID = "album_id";
		
		
		public static final String[] PROJECTION = {"_ID", ALBUM_COLUMN, ARTIST_COLUMN, ALBUM_ID};
		
		/**
		 * Instantiates a new album data.
		 */
		private AlbumData(){};
		
	}
	public static final int ITEMS = 1;
	public static final int FILTERED_ITEMS = 2;
	
	private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
	
	static
	{
		uriMatcher.addURI(AUTH, "items/", ITEMS);
		uriMatcher.addURI(AUTH, "filtered/", FILTERED_ITEMS);
	}
	
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#getType(android.net.Uri)
	 */
	@Override
	public String getType(Uri uri)
	{
		Log.e("Progress", "In GetType()");
		switch (uriMatcher.match(uri))
		{
			case ITEMS:
				return AlbumData.CONTENT_TYPE;		
		}
		return null;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#onCreate()
	 */
	@Override
	public boolean onCreate()
	{	
		jsonFactory = JSONFactory.getInstance();
		
		return false;
	}

	/* (non-Javadoc)
	 * @see android.content.ContentProvider#query(android.net.Uri, java.lang.String[], java.lang.String, java.lang.String[], java.lang.String)
	 */
	@Override
	public Cursor query(Uri uri, String[] projection, String selection,
			String[] selectionArgs, String sortOrder)
	{
		dataIOController = DataIOController.getInstance(this.getContext());
		JSONArray albums = null;
		ArrayList<Album> albumList = new ArrayList<Album>();
		
		//get data string. Put everything in try to catch ~20 JSON Exceptions
		try
		{
			jsonFactory.createObjectFromString(dataIOController.getJSONStringFromFileName(NetworkService.FILE_NAME));
			JSONObject outerShell = jsonFactory.getJSONObject();
			
			if (outerShell.has("error"))
				return null;
			
			JSONObject innerShell = outerShell.getJSONObject("topalbums");
			albums = innerShell.getJSONArray("album");
			
			//double looping seems convoluted, but it will be needed
			//if I ever implement images into the project in future.
			for (int i = 0; i < albums.length(); i++)
			{
				JSONObject currentAlbum = albums.getJSONObject(i);
		    	Album albumHolder = new Album();
		    	albumHolder.setAlbumName(currentAlbum.getString("name"));
		    	albumHolder.setAlbumID(currentAlbum.getString("mbid"));
		    	
		    	JSONObject artistInfo = currentAlbum.getJSONObject("artist");
		    	albumHolder.setArtistName(artistInfo.getString("name"));
		    	
		    	//images are triple wrapped. IMAGES WERE NOT IMPLEMENTED THIS WEEK
		    	JSONArray albumArt = currentAlbum.getJSONArray("image");
		    	JSONObject actualImage = albumArt.getJSONObject(3);
		    	albumHolder.setImageURL(actualImage.getString("#text"));
		    	
		    	albumList.add(albumHolder);
			}
			
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
		
		MatrixCursor cursor = new MatrixCursor(AlbumData.PROJECTION);
		
		int count = 0;
		

		
		switch (uriMatcher.match(uri))
		{
			case ITEMS:
				Log.i("uri", "items");
				
				for (Album album : albumList)
				{
					cursor.addRow(new Object[] {count++, album.getAlbumName(), album.getArtistName(), album.getAlbumID()});	
				}
				break;
				
			case FILTERED_ITEMS:
				Log.i("uri", "filtered items");
				for (Album album : albumList)
				{
					Log.d("selection", "Select loop");
					if (album.getArtistName().toLowerCase(Locale.getDefault()).equals(selection.toLowerCase(Locale.getDefault())))
					{
						Log.d("selection", album.getArtistName().equals(selection) ? "True": "False");
						cursor.addRow(new Object[] {count++, album.getAlbumName(), album.getArtistName(), album.getAlbumID()});
					}	
				}
				
				break;
		}
		//Log.i("cursor", DatabaseUtils.dumpCursorToString(cursor));
		return cursor;
	}
	
	
	
	//unsupported inherited methods 
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#insert(android.net.Uri, android.content.ContentValues)
	 */
	@Override
	public Uri insert(Uri uri, ContentValues values)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#delete(android.net.Uri, java.lang.String, java.lang.String[])
	 */
	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}
	
	/* (non-Javadoc)
	 * @see android.content.ContentProvider#update(android.net.Uri, android.content.ContentValues, java.lang.String, java.lang.String[])
	 */
	@Override
	public int update(Uri uri, ContentValues values, String selection,
			String[] selectionArgs)
	{
		// TODO Auto-generated method stub
		throw new UnsupportedOperationException();
	}

}
