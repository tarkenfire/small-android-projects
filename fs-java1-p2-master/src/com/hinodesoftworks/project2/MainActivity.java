/* 
 * Date: Sep 19, 2013
 * Project: Project2
 * Package: com.hinodesoftworks.project2
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.project2;

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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ImageView.ScaleType;

import com.hinodesoftworks.jsonutils.JSONFactory;
import com.hinodesoftworks.netutils.ConnectionManager;

// TODO: Auto-generated Javadoc
/**
 * The Main activity for the app.
 */
public class MainActivity extends Activity implements OnClickListener
{
	ArrayList<Media> albumList;
	private int count;
	
	//local handles for views.
	private LinearLayout mediaFlowView;
	private EditText uidEntry;
	private TextView playCountView;
	private TextView albumTitleView;
	private TextView artistNameView;
	
	private JSONFactory factory;

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		count = 0;
		albumList = new ArrayList<Media>();
		factory = JSONFactory.getInstance();
		
		createActivityLayout();
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

	/**
	 * Process the JSON file returned from the JSON Factory class..
	 *
	 * @throws JSONException the jSON exception
	 */
	private void processJSON() throws JSONException
	{
		JSONObject returnObject = factory.getJSONObject();
		
		if (returnObject.has("error")) //error code returned
		{
			Toast.makeText(MainActivity.this, R.string.api_invalid_param_error, Toast.LENGTH_LONG).show();
		}
		else
		{
			//the JSON object is double wrapped.
			JSONObject topAlbums = returnObject.getJSONObject("topalbums");
			JSONArray albums = topAlbums.getJSONArray("album");
			
			//JSON array isn't iteratable, of course.
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
				albumHolder.setPlayCount(Integer.valueOf(currentAlbum.getString("playcount")));
				albumHolder.setImageURL(imageMedium.getString("#text"));

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
	 * Creates the image buttons for individual albums displayed in the horizontal scroll view.
	 *
	 * @param drawable the drawable
	 */
	private void createImageButtons(Drawable drawable)
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
		albumArtHolder.setId(count);
		count++;
		albumArtHolder.setOnClickListener(this);
		
		//set image
		albumArtHolder.setImageDrawable(drawable);
		mediaFlowView.addView(albumArtHolder);
	}
	
	/**
	 * Creates the activity layout from code rather than xml.
	 */
	private void createActivityLayout()
	{
		// base view
		LinearLayout container = new LinearLayout(this);
		container.setOrientation(LinearLayout.VERTICAL);
		container.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));

		// label for user id edittext
		TextView uidEntryLabel = new TextView(this);
		uidEntryLabel.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		uidEntryLabel.setText(getString(R.string.label_uid_entry));

		container.addView(uidEntryLabel);

		// entry field for userid
		uidEntry = new EditText(this);
		uidEntry.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));

		container.addView(uidEntry);

		// submit button for user id entry
		Button submitButton = new Button(this);
		submitButton.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		submitButton.setText(getString(R.string.button_submit));

		submitButton.setId(100);
		submitButton.setOnClickListener(this);

		container.addView(submitButton);

		// label for scroll view
		TextView mediaFlowLabel = new TextView(this);
		mediaFlowLabel.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
		mediaFlowLabel.setText(getString(R.string.label_media_flow));

		container.addView(mediaFlowLabel);

		// scrollview container for media display
		HorizontalScrollView scrollContainer = new HorizontalScrollView(this);
		scrollContainer.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		container.addView(scrollContainer);

		// container for multiple ImageViews representing different media.
		mediaFlowView = new LinearLayout(this);
		mediaFlowView.setOrientation(LinearLayout.HORIZONTAL);
		mediaFlowView.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

		// add this view to the scroll container
		scrollContainer.addView(mediaFlowView);

		// generic container for various album metadata.
		LinearLayout metadataContainer = new LinearLayout(this);
		metadataContainer.setOrientation(LinearLayout.VERTICAL);
		metadataContainer.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

		container.addView(metadataContainer);

		// metadata section for albums

		// album title field
		albumTitleView = new TextView(this);
		albumTitleView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		metadataContainer.addView(albumTitleView);

		// artist name field
		artistNameView = new TextView(this);
		artistNameView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

		metadataContainer.addView(artistNameView);

		// play count field
		playCountView = new TextView(this);
		playCountView.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		playCountView.setText(getString(R.string.playcount_base));

		metadataContainer.addView(playCountView);

		setContentView(container);
	}

	/* (non-Javadoc)
	 * @see android.view.View.OnClickListener#onClick(android.view.View)
	 */
	@Override
	public void onClick(View v)
	{
		//close keyboard.
		InputMethodManager imm = (InputMethodManager)getSystemService(
			      Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(uidEntry.getWindowToken(), 0);
		
		
		//TODO: There is a bug in this code if there are multiple users 
		//are entered in rapid succession.
		APIRequest rq = new APIRequest();
		URL url = null;
		
		if(v.getId() == 100)
		{
			mediaFlowView.removeAllViews();
			String inputString = uidEntry.getText().toString();
			
			if (inputString.equals(""))
			{
				Toast.makeText(this, R.string.blank_error, Toast.LENGTH_LONG).show();
				return;
			}
			
			//check connection
			if (!ConnectionManager.isNetworkConnected(this))
			{
				Toast.makeText(this, R.string.no_internet_error, Toast.LENGTH_LONG).show();
				return;
			}
			

			try
			{
				url = new URL("http://ws.audioscrobbler.com/2.0/?method=user.gettopalbums&user="+ inputString +"&limit=20&api_key=907ac9ed5a3384eae314b98362049d33&format=json");
			}
			catch (MalformedURLException e)
			{
				Toast.makeText(this, R.string.api_temp_error, Toast.LENGTH_LONG).show();
				e.printStackTrace();
			}
			
			rq.execute(url);
		}
		else
		{
			Media selectedMedia = albumList.get(v.getId());
			albumTitleView.setText(selectedMedia.getAlbumName());
			artistNameView.setText(selectedMedia.getArtistName());
			playCountView.setText(getString(R.string.playcount_base) + " "
					+ selectedMedia.getPlayCount());
			
		}		
	}
	
	
	//threading tasks
	/**
	 * Makes a call to the Last.fm api to get the data for a specific user.
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
			if (result != null)
			{
				try
				{
					factory.createObjectFromString(result);
					processJSON();
				}
				catch (JSONException e)
				{
					Toast.makeText(MainActivity.this, R.string.api_temp_error, Toast.LENGTH_LONG).show();
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Uses a URL from the JSON file to get images for the image buttons for each album.
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
						Toast.makeText(MainActivity.this, R.string.api_temp_error, Toast.LENGTH_LONG).show();
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
			createImageButtons(drawables[0]);
		}
		
	}
	
}
