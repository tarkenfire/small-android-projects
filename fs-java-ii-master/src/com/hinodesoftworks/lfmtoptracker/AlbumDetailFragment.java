package com.hinodesoftworks.lfmtoptracker;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.hinodesoftworks.netutils.ConnectionManager;

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

public class AlbumDetailFragment extends Fragment implements OnClickListener
{
	//no interface is needed in this fragment, as no data will be passed back
	
	
	JSONObject albumInfo;
	ArrayList<String> trackNames;
	String mbid;
	
	//ui handles
	ImageView albumArtView;
	TextView albumNameView;
	TextView albumArtistView;
	TextView albumSummaryView;
	ListView trackList;
	Button shareButton;

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		//grab ui handles
		Activity activity = getActivity();
		
		albumArtView = (ImageView)activity.findViewById(R.id.image_album_art);
		albumNameView = (TextView)activity.findViewById(R.id.text_view_detail_album_name);
		albumArtistView = (TextView)activity.findViewById(R.id.text_view_detail_artist_name);
		albumSummaryView = (TextView)activity.findViewById(R.id.text_view_album_summary);
		shareButton = (Button)activity.findViewById(R.id.button_share);
		
		shareButton.setOnClickListener(this);
		
		//set actionpqr logo as a "back button"
		ActionBar actionBar = getActivity().getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
    		Bundle savedInstanceState) 
	{
        return inflater.inflate(R.layout.display_fragment, container, false);		
    }


	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem menuItem)
    {       
		return true;
    }
	
	//UI handlers/listeners
	@Override
	public void onClick(View v)
	{
		Log.i("Share", "Share button pressed.");
		
		//string builder is more resource friendly for 
		//multiple string joining than normal concatenation
		StringBuilder sb = new StringBuilder("");
		try
		{
			sb.append(albumInfo.getString("name"));
			sb.append(" by ");
			sb.append(albumInfo.getString("artist"));
			sb.append(" on Last.FM. ");
			sb.append(albumInfo.getString("url"));
		}
		catch (JSONException e)
		{
			e.printStackTrace();
		}
		
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_SUBJECT, "Album Share");
		share.putExtra(Intent.EXTRA_TEXT, sb.toString());
		
		startActivity(Intent.createChooser(share, "Share via "));
		
	}


	//private methods
	private void displayData(String result)
	{
		//the API I used has an issue where some items
		//may not appear in the JSON, therfore
		//multiple try/catch blocks for different 
		//sections that could not be shown by the API
		//to show alt data in that event.
		
		try
		{
			//album basic data
			JSONObject holder = new JSONObject(result);
			albumInfo = holder.getJSONObject("album");
			albumNameView.setText(albumInfo.getString("name"));
			albumArtistView.setText(albumInfo.getString("artist"));	
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//album summary
		
		try
		{
			JSONObject wiki = albumInfo.getJSONObject("wiki");
			albumSummaryView.setText(wiki.getString("summary"));
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			albumSummaryView.setText(R.string.error_no_summary);
		}
		catch (Exception e)
		{
			albumSummaryView.setText(R.string.error_no_summary);			
		}
		
		//album art
		try
		{
			JSONArray image = albumInfo.getJSONArray("image");
			JSONObject imageActual = image.getJSONObject(3);
			
			URL url = new URL(imageActual.getString("#text"));
			
			ImageRequest irq = new ImageRequest();
			irq.execute(url);
		}
		catch (JSONException e)
		{
			albumArtView.setImageDrawable(this.getResources().getDrawable(R.drawable.no_art));
		}
		catch (MalformedURLException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (Exception e) //null pointer is occasionally thrown
		{
			albumArtView.setImageDrawable(this.getResources().getDrawable(R.drawable.no_art));
		}
	
	}
	
	
	//public methods
	public void displayAlbumData(String mbid)
	{
		this.mbid = mbid;
		
		URL url = null;
		try
		{
			url = new URL("http://ws.audioscrobbler.com/2.0/?method=album.getinfo&api_key=907ac9ed5a3384eae314b98362049d33&mbid=" + mbid + "&format=json");
		}
		catch (MalformedURLException e)
		{
			e.printStackTrace();
		}
		
		JSONRequest jrq = new JSONRequest();
		jrq.execute(url);
		
	}
	
	
	/**
	 * The Class JSONRequest.
	 */
	private class JSONRequest extends AsyncTask<URL, Void, String>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected String doInBackground(URL... urls)
		{
			Log.i("tag", "In asynch.");
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
			displayData(result);
	    }

		
	}
	
	/**
	 * The Class ImageRequest.
	 */
	private class ImageRequest extends AsyncTask<URL, Void, Drawable>
	{

		/* (non-Javadoc)
		 * @see android.os.AsyncTask#doInBackground(Params[])
		 */
		@Override
		protected Drawable doInBackground(URL... urls)
		{
			
            Drawable draw = null;
            for (URL url : urls)
            {
                    draw = ConnectionManager.getDrawableFromURL(url);
            }
            return draw;
		}
		
		/* (non-Javadoc)
		 * @see android.os.AsyncTask#onPostExecute(java.lang.Object)
		 */
		@Override
		protected void onPostExecute(Drawable result) 
		{
			albumArtView.setImageDrawable(result);
	    }
	}
	
}
