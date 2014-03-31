/* 
 * Date: Sep 11, 2013
 * Project: Project1
 * Package: com.hinodesoftworks.project1
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.project1;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Bundle;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.hinodesoftworks.jsonutils.JSONFactory;
import com.hinodesoftworks.project1.APIError;

/**
 * The default activity for this android app where all user interaction takes place.
 */
public class MainActivity extends Activity implements OnClickListener
{
	private LinearLayout mediaFlowView;
	private JSONObject jsonObject;
	private ArrayList<Media> mediaList;

	//local handles for views.
	private EditText uidEntry;
	private TextView playCountView;
	private TextView albumTitleView;
	private TextView artistNameView;
	

	/* (non-Javadoc)
	 * @see android.app.Activity#onCreate(android.os.Bundle)
	 */
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		jsonObject = new JSONObject();
		mediaList = new ArrayList<Media>();

		createActivityLayout();
	}


	/**
	 * convenience method to create a simulated JSON file (object), as would be
	 * provided from an API
	 */
	private void createDummyData()
	{
		// create json object from hash map. This object is much flatter than
		// what I would get from any RESTful API.
		JSONFactory factory = JSONFactory.getInstance();
		factory.createEmptyObject();

		// using object for a generic is generally frowned upon for normal use,
		// but for static data it should work.
		HashMap<String, Object> map = new HashMap<String, Object>();

		// album 1
		map.put("album_name", new String("UKF Dubstep 2012"));
		map.put("artist_name", new String("UKF"));
		map.put("image_url", new String("ukf")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(49));

		factory.addObjectToJSONObject("album1", map);
		map.clear();

		// album 2
		map.put("album_name", new String("Circus One"));
		map.put("artist_name", new String("Flux Pavillion and Doctor P."));
		map.put("image_url", new String("cir_one")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(100));

		factory.addObjectToJSONObject("album2", map);
		map.clear();

		// album 3
		map.put("album_name", new String("Dethalbum I"));
		map.put("artist_name", new String("Dethklok"));
		map.put("image_url", new String("dethalbum")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(4));

		factory.addObjectToJSONObject("album3", map);
		map.clear();

		// album 4
		map.put("album_name", new String("Hold Your Colour"));
		map.put("artist_name", new String("Pendulum"));
		map.put("image_url", new String("hold_your_colour")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(23));

		factory.addObjectToJSONObject("album4", map);
		map.clear();

		// album 5
		map.put("album_name", new String("Invaders Must Die"));
		map.put("artist_name", new String("The Prodigy"));
		map.put("image_url", new String("invade")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(242));

		factory.addObjectToJSONObject("album5", map);
		map.clear();

		// album 6
		map.put("album_name", new String("Mezmerize"));
		map.put("artist_name", new String("System of a Down"));
		map.put("image_url", new String("mezmerize")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(30));

		factory.addObjectToJSONObject("album6", map);
		map.clear();
		
		// album 7
		map.put("album_name", new String("2"));
		map.put("artist_name", new String("Netsky"));
		map.put("image_url", new String("netsky_2")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(8));

		factory.addObjectToJSONObject("album7", map);
		map.clear();
		
		// album 8 - THis one is meant to test for non-english unicode characters
		map.put("album_name", new String("うみねこのなく頃に"));
		map.put("artist_name", new String("志方あきこ"));
		map.put("image_url", new String("prologo")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(43));

		factory.addObjectToJSONObject("album8", map);
		map.clear();
		
		// album 9
		map.put("album_name", new String("Radiance"));
		map.put("artist_name", new String("P*light and DJ Noriken"));
		map.put("image_url", new String("radiance")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(102));

		factory.addObjectToJSONObject("album9", map);
		map.clear();
		
		// album 10
		map.put("album_name", new String("トップマン"));
		map.put("artist_name", new String("SAISEN TURN"));
		map.put("image_url", new String("toppuman")); // IN REAL APP THIS WOULD BE A URL, for this week, it will be the name of a static resource.
		map.put("play_count", Integer.valueOf(22));

		factory.addObjectToJSONObject("album10", map);
		map.clear();

		// get finalized JSON object from factory
		jsonObject = factory.getJSONObject();

		// simulates a callback that would be received if this were data from a
		// web api
		try
		{
			onDataLoaded();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	
	/**
	 * // simulated http callback once JSON data is downloaded and created.
	 *
	 * @throws JSONException if the data returned is not a valid JSON object
	 */
	protected void onDataLoaded() throws JSONException
	{
		//Using the math.random method rather than Random.nextInt for the sake of using a double.
		double random = Math.random();
		//simulates possible API error with a 1 in 20 chance of an API error being simulated
		int responseCode = (int)(random * 100);
		
		Log.i("rand", String.valueOf(random));
		Log.i("code", String.valueOf(responseCode));
		
		
		//soapbox: if it's really just a psuedo-class, don't call it a enum.
		if (responseCode == APIError.AUTH_FAILED.getCode())
		{
			Toast.makeText(this, R.string.api_auth_error, Toast.LENGTH_LONG).show();
			return;
		}
		else if(responseCode == APIError.INVALID_PARAMATER.getCode())
		{
			Toast.makeText(this, R.string.api_invalid_param_error, Toast.LENGTH_LONG).show();
			return;
		}
		else if(responseCode == APIError.REQUEST_FAILED.getCode())
		{
			Toast.makeText(this, R.string.api_request_failed_error, Toast.LENGTH_LONG).show();
			return;
		}
		else if(responseCode == APIError.SERVICE_OFLINE.getCode())
		{
			Toast.makeText(this, R.string.api_service_offline_error, Toast.LENGTH_LONG).show();
			return;
		}
		else if(responseCode == APIError.TEMP_ERROR.getCode())
		{
			Toast.makeText(this, R.string.api_temp_error, Toast.LENGTH_LONG).show();
			return;
		}
		else //no error code reported, assume success
		{}
		
		
		// transfer data from json objects into media objects
		Iterator<?> keys = jsonObject.keys();

		while (keys.hasNext())
		{
			String key = (String) keys.next();
			if (jsonObject.get(key) instanceof JSONObject)
			{
				JSONObject currentMedia = (JSONObject) jsonObject.get(key);
				Media mediaHolder = new Media();

				mediaHolder.setAlbumName(currentMedia.getString("album_name"));
				mediaHolder
						.setArtistName(currentMedia.getString("artist_name"));
				mediaHolder.setPlayCount(currentMedia.getInt("play_count"));
				mediaHolder.setImageURL(currentMedia.getString("image_url"));

				mediaList.add(mediaHolder);

			}

		}

		// constants for px to dp size conversions, pixels are never acceptable
		// measurment units in Android.
		final int ONE_HUNDRED_DP = (int) Math.ceil(100.0f * getResources()
				.getDisplayMetrics().density);

		// use list of objects to populate data in the app's main screen.
		for (Media currentMedia : mediaList)
		{
			// set and add image for album.
			ImageView albumArtHolder = new ImageView(this);
			albumArtHolder.setLayoutParams(new LayoutParams(ONE_HUNDRED_DP,
					ONE_HUNDRED_DP));
			albumArtHolder.setScaleType(ScaleType.CENTER_INSIDE);
			albumArtHolder.setPadding(20, 0, 0, 0);

			// set tag to be able to recognize clicks in the onclicklistener
			albumArtHolder.setId(mediaList.indexOf(currentMedia));
			albumArtHolder.setOnClickListener(this);

			// get and set drawable. In non-dummy data program, this would be
			// acomplished via a http call.
			albumArtHolder.setImageResource(getResources().getIdentifier(
					currentMedia.getImageURL(), "drawable",
					"com.hinodesoftworks.project1"));
			mediaFlowView.addView(albumArtHolder);

		}
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
		if (v.getId() == 100) // is submit button
		{
			//for the dummy data version, the only invalid option is a blank field; any other string will work.
			String sentinal = uidEntry.getText().toString();
			
			if (sentinal.matches(""))
			{
				Toast.makeText(this, R.string.blank_error, Toast.LENGTH_LONG).show();
			}
			else
			{
				createDummyData();
			}
		}
		else
		// is album
		{
			Media selectedMedia = mediaList.get(v.getId());
			albumTitleView.setText(selectedMedia.getAlbumName());
			artistNameView.setText(selectedMedia.getArtistName());
			playCountView.setText(getString(R.string.playcount_base) + " "
					+ selectedMedia.getPlayCount());
		}

	}

}
