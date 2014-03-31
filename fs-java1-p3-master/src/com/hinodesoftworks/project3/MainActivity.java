/* 
 * Date: Sep 26, 2013
 * Project: Project3
 * Package: com.hinodesoftworks.project3
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.project3;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.os.AsyncTask;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView.ScaleType;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

import com.hinodesoftworks.jsonutils.JSONFactory;
import com.hinodesoftworks.netutils.ConnectionManager;


// TODO: Auto-generated Javadoc
/**
 * The Class MainActivity.
 */
public class MainActivity extends Activity implements OnClickListener, OnItemSelectedListener
{
	//static mode values
	final static int MODE_UID = 0;
	final static int MODE_ARTIST = 1;
	final static int MODE_TAG = 2;
	
	//ui handles
	Spinner modeSpinner;
	EditText entryField;
	Button submitButton;
	LinearLayout mediaFlowView;
	
	RelativeLayout uidSection;
	TextView uidTitle;
	TextView uidArtistName;
	TextView uidAlbumName;
	TextView uidPlaycount;
	
	RelativeLayout artistSection;
	TextView artistTitle;
	TextView artistAlbumName;
	TextView artistListeners;
	
	RelativeLayout tagSection;
	TextView tagTitle;
	TextView tagAlbumName;
	TextView tagArtistName;
	
	//misc variables
	public int currentMode = MODE_UID;
	
	private int count = 0;
	
	private JSONFactory factory;
	ArrayList<Media> albumList;
	
	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		//init/capture variables
		modeSpinner = (Spinner)findViewById(R.id.mode_spinner);
		entryField = (EditText)findViewById(R.id.uid_entry);
		submitButton = (Button)findViewById(R.id.submit_button);
		mediaFlowView = (LinearLayout)findViewById(R.id.media_flow_view);
		
		uidSection = (RelativeLayout)findViewById(R.id.user_metadata_container);
		uidTitle = (TextView)findViewById(R.id.user_title_label);
		uidArtistName = (TextView)findViewById(R.id.album_artist_text);
		uidAlbumName = (TextView)findViewById(R.id.album_name_text);
		uidPlaycount = (TextView)findViewById(R.id.album_count_text);
		
		artistSection = (RelativeLayout)findViewById(R.id.artist_metadata_container);
		artistTitle = (TextView)findViewById(R.id.artist_name_label);
		artistAlbumName = (TextView)findViewById(R.id.artist_album_name_text);
		artistListeners = (TextView)findViewById(R.id.artist_listener_text);
		
		tagSection = (RelativeLayout)findViewById(R.id.tag_metadata_container);
		tagTitle = (TextView)findViewById(R.id.tag_title_label);
		tagAlbumName = (TextView)findViewById(R.id.tag_album_text);
		tagArtistName = (TextView)findViewById(R.id.tag_artist_text);
		
		factory = JSONFactory.getInstance();
		
		albumList = new ArrayList<Media>();
		
		//create adapter for spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		        R.array.spinner_choices, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	
		//set adapters and listeners
		modeSpinner.setAdapter(adapter);
		modeSpinner.setOnItemSelectedListener(this);
		submitButton.setOnClickListener(this);
		
		entryField.setOnEditorActionListener(new OnEditorActionListener()
		{
			@Override
			public boolean onEditorAction(TextView v, int actionId,
					KeyEvent event)
			{
				submitButton.performClick();
				return true;
			}
		});
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreateOptionsMenu(android.view.Menu)
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	
	//private methods
	/**
	 * Process json returned from web api.
	 *
	 * @throws JSONException the jSON exception
	 */
	private void processJSON() throws JSONException
	{
		//the objects returned for all three items are shockingly similar.
		JSONObject obj = factory.getJSONObject();
		
		if (obj.has("error")) //error code returned
		{
			Toast.makeText(this, R.string.error_no_results, Toast.LENGTH_LONG).show();
			return;
		}
		else
		{
			//the JSON object is double wrapped.
			JSONObject topAlbums = obj.getJSONObject("topalbums");
			JSONArray albums = topAlbums.getJSONArray("album");
			
			albumList.clear();
			mediaFlowView.removeAllViews();
			
			for (int i = 0; i < albums.length(); i++)
			{
				JSONObject currentAlbum = albums.getJSONObject(i);
				Media albumHolder = new Media();

				//the json object is broken up into multiple parts
				JSONObject artistInfo = currentAlbum.getJSONObject("artist");

				//the album image urls come in the form of json objects nested inside of a 
				//json array, which is nested inside of a json object, which is nested
				//inside of another json object.
				JSONArray imageInfo = currentAlbum.getJSONArray("image");
				JSONObject imageMedium = imageInfo.getJSONObject(3);

				albumHolder.setArtistName(artistInfo.getString("name"));
				albumHolder.setAlbumName(currentAlbum.getString("name"));
				albumHolder.setImageURL(imageMedium.getString("#text"));

				if (currentAlbum.has("playcount")) 
				{
					albumHolder
						.setPlayCount(Integer.valueOf(currentAlbum
								.getString("playcount")));
				}
				
				albumList.add(albumHolder);
			}
			
			ImageRequest iReq = new ImageRequest();
			
			Media[] mediaHolder = new Media[albumList.size()];
			for (int i = 0; i < albumList.size(); i++)
			{
				mediaHolder[i] = albumList.get(i);
			}
			
			iReq.execute(mediaHolder);
		}
	}
	
	/**
	 * Creates image buttons from album art covers.
	 *
	 * @param draw the draw
	 */
	private void createImageButton(Drawable draw)
	{
		// set and add image for album.
		final int ONE_HUNDRED_DP = (int) Math.ceil(100.0f * getResources()
				.getDisplayMetrics().density);
		ImageView albumArtHolder = new ImageView(this);
		albumArtHolder.setLayoutParams(new LayoutParams(ONE_HUNDRED_DP,
				ONE_HUNDRED_DP));
		albumArtHolder.setScaleType(ScaleType.CENTER_INSIDE);
		albumArtHolder.setPadding(20, 0, 0, 0);
		
		// set tag to be able to recognize clicks in the onclicklistener
		albumArtHolder.setId(count++);
		
		albumArtHolder.setOnClickListener(this);
		
		//set image
		albumArtHolder.setImageDrawable(draw);
		mediaFlowView.addView(albumArtHolder);
	}
	
	//handlers
	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onItemSelected(android.widget.AdapterView, android.view.View, int, long)
	 */
	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int pos,
			long id)
	{
		entryField.setText("");
		
		switch (pos)
		{
		case MODE_UID: //username
			entryField.setHint(getString(R.string.user_hint_text));
			currentMode = MODE_UID;
			break;
		case MODE_ARTIST: //artist name
			entryField.setHint(getString(R.string.artist_hint_text));
			currentMode = MODE_ARTIST;
			break;
		case MODE_TAG: //tag
			entryField.setHint(getString(R.string.tag_hint_text));
			currentMode = MODE_TAG;
			break;
		}
	}

	/* (non-Javadoc)
	 * @see android.widget.AdapterView.OnItemSelectedListener#onNothingSelected(android.widget.AdapterView)
	 */
	@Override
	public void onNothingSelected(AdapterView<?> parent){} //no action taken

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		Log.i("tag", ConnectionManager.isNetworkConnected(this) ? "Network: True" : "Network: False");
		Log.i("tag", String.valueOf(v.getId()));
	
		//close keyboard.
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(entryField.getWindowToken(), 0);
		
		if(v.getId() == R.id.submit_button)
		{
			String queryString = entryField.getText().toString();
			URL url = null;
			count = 0;
			
			if (queryString.equals(""))
			{
				Toast.makeText(this, R.string.error_blank_entry, Toast.LENGTH_LONG).show();
				return;
			}
			
			if (!ConnectionManager.isNetworkConnected(this))
			{
				Toast.makeText(this, R.string.error_no_connection, Toast.LENGTH_LONG).show();
				return;
			}
			
			try
			{
				switch (currentMode)
				{
					case MODE_UID:
						url = new URL("http://ws.audioscrobbler.com/2.0/?method=user.gettopalbums&user=" + queryString + "&limit=20&api_key=907ac9ed5a3384eae314b98362049d33&format=json");
						uidTitle.setText(getString(R.string.uid_base) + " " + entryField.getText().toString());
						uidSection.setVisibility(View.VISIBLE);
						artistSection.setVisibility(View.GONE);
						tagSection.setVisibility(View.GONE);
						break;
					case MODE_ARTIST:
						url = new URL("http://ws.audioscrobbler.com/2.0/?method=artist.gettopalbums&artist=" + queryString + "&limit=20&autocorrect=1&api_key=907ac9ed5a3384eae314b98362049d33&format=json");
						artistTitle.setText(getString(R.string.artist_base) + " " + entryField.getText().toString());
						uidSection.setVisibility(View.GONE);
						artistSection.setVisibility(View.VISIBLE);
						tagSection.setVisibility(View.GONE);
						break;
					case MODE_TAG:
						url = new URL("http://ws.audioscrobbler.com/2.0/?method=tag.gettopalbums&tag=" + queryString + "&limit=20&api_key=907ac9ed5a3384eae314b98362049d33&format=json");
						tagTitle.setText(getString(R.string.tag_base) + " " + entryField.getText().toString());
						uidSection.setVisibility(View.GONE);
						artistSection.setVisibility(View.GONE);
						tagSection.setVisibility(View.VISIBLE);
						break;
				}
			}
			catch(MalformedURLException e)
			{
				Toast.makeText(this, R.string.error_unknown, Toast.LENGTH_LONG).show();
				return;
			}
			
			APIRequest rq = new APIRequest();
			rq.execute(url);
			
		}
		else //clicked on album
		{
			Media currentAlbum = albumList.get(v.getId());
			
			switch (currentMode)
			{
			case MODE_UID:
				uidArtistName.setText(" " + currentAlbum.getArtistName());
				uidAlbumName.setText(" " + currentAlbum.getAlbumName());
				uidPlaycount.setText(" " + String.valueOf(currentAlbum.getPlayCount()));
				break;
			case MODE_ARTIST:
				artistAlbumName.setText(" " + currentAlbum.getAlbumName());
				artistListeners.setText(" " + String.valueOf(currentAlbum.getPlayCount()));
				break;
			case MODE_TAG:
				tagArtistName.setText(" " + currentAlbum.getArtistName());
				tagAlbumName.setText(" " + currentAlbum.getAlbumName());
				break;
			}
		}
	}
	
	//threaded network tasks
	/**
	 * The Class APIRequest.
	 */
	private class APIRequest extends AsyncTask<URL,Void, String>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(URL... urls)
		{
			String jsonString = "";
			for (URL url : urls)
			{
				jsonString = ConnectionManager.getJSONStringFromURL(url);
			}
			return jsonString;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(String result)
		{	
			try
			{
				factory.createObjectFromString(result);
				processJSON();
			}
			catch (JSONException e)
			{
				e.printStackTrace();
			}
		}
		
	}
	
	/**
	 * The Class ImageRequest.
	 */
	private class ImageRequest extends AsyncTask<Media, Drawable, Void>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Void doInBackground(Media... media)
		{		
				for (int i = 0; i < media.length ; i++)
				{
					Media album = media[i];
					
					URL url = null;
					try
					{
						url = new URL(album.getImageURL());
						publishProgress(ConnectionManager.getDrawableFromURL(url));
					}
					catch (MalformedURLException e)
					{
						Toast.makeText(MainActivity.this, R.string.error_unknown, Toast.LENGTH_LONG).show();
						e.printStackTrace();
					}	
					
				}
				return null;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onProgressUpdate(Progress[])
		 */
		@Override 
		protected void onProgressUpdate(Drawable... drawables)
		{
			createImageButton(drawables[0]);
		}
	}

}
