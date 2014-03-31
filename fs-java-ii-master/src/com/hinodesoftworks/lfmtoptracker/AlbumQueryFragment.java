package com.hinodesoftworks.lfmtoptracker;

import com.hinodesoftworks.lfmtoptracker.AlbumProvider.AlbumData;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleCursorAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;
import android.widget.Toast;

public class AlbumQueryFragment extends Fragment implements OnClickListener, OnItemSelectedListener, OnItemClickListener
{
	//interfaces
	public interface AlbumQueryListener
	{
		public void onAlbumSelected(String mbid);
		public void onQuery(String query, int mode);
		public void onQueryFilter(String filter);
	}
	
	//static variables
	public static final int MODE_UID = 0;
	public static final int MODE_ARTIST = 1;
	public static final int MODE_TAG = 2;
	
	public static final int DISPLAY_MODE_SEARCH = 100;
	public static final int DISPLAY_MODE_FILTER = 200;
	public static final int DISPLAY_MODE_RESET = 300;
	
	//local variables
	AlbumQueryListener handler;
	int currentMode;
	
	
	
	//ui handles
	EditText inputField;
	Button submitButton;
	Spinner modeSpinner;
	ListView albumList;
	TextView instructView;
	EditText filterField;
	Button filterButton;
	Button backButton;
	Button clearFilterButton;
	
	LinearLayout displayHeader;
	RelativeLayout searchArea;
	RelativeLayout filterArea;
	
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		
		this.setRetainInstance(true);
		
		//init variables
		currentMode = MODE_UID;
		
		//grab ui handles
		Activity activity = getActivity();
		
		inputField = (EditText)activity.findViewById(R.id.edit_text_entry_field);
		submitButton = (Button)activity.findViewById(R.id.button_submit);
		modeSpinner = (Spinner)activity.findViewById(R.id.spinner_mode_select);
		albumList = (ListView)activity.findViewById(R.id.list_view_display_list);
		displayHeader = (LinearLayout)activity.findViewById(R.id.display_header);
		instructView = (TextView)activity.findViewById(R.id.text_view_instructions);
		searchArea = (RelativeLayout)activity.findViewById(R.id.search_container);
		filterArea = (RelativeLayout)activity.findViewById(R.id.filter_container);
		filterField = (EditText)activity.findViewById(R.id.edit_text_filter_field);
		filterButton = (Button)activity.findViewById(R.id.button_filter);
		clearFilterButton = (Button)activity.findViewById(R.id.button_clear_filter);
		backButton = (Button)activity.findViewById(R.id.button_back);
		
		Log.i("instance", savedInstanceState == null ? "True": "False");
		
		if (savedInstanceState != null)
		{
			
			inputField.setText(savedInstanceState.getString("search_value"));
		}
		
		//create adapter for spinner
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(),
		        R.array.spinner_choices, android.R.layout.simple_spinner_item);
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		//set listeners/adapters
		submitButton.setOnClickListener(this);
		filterButton.setOnClickListener(this);
		clearFilterButton.setOnClickListener(this);
		backButton.setOnClickListener(this);
		modeSpinner.setAdapter(adapter);
		modeSpinner.setOnItemSelectedListener(this);
		albumList.setOnItemClickListener(this);
		
        inputField.setOnEditorActionListener(new OnEditorActionListener()
        {
                @Override
                public boolean onEditorAction(TextView v, int actionId,
                                KeyEvent event)
                {
                        submitButton.performClick();
                        return true;
                }
        });
        
        filterField.setOnEditorActionListener(new OnEditorActionListener()
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
	
	@Override
    public void onAttach(Activity activity) 
	{
		super.onAttach(activity);

		//checks if the base activity implements the listener interface.
		try 
		{
            handler = (AlbumQueryListener) activity;
        } 
		catch (ClassCastException e) 
		{
            throw new ClassCastException(activity.toString() + " does not implement AlbumQueryListener");
        }

	}
	
	@Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, 
    		Bundle savedInstanceState) 
	{ 
		Log.e("instance", String.valueOf(this.getId()));
		return inflater.inflate(R.layout.query_fragment, container, false);
    }

	@Override
	public void onSaveInstanceState(Bundle outState)
	{
		
		Log.e("instance", "in saveinstancestate");
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
		
//		outState.putInt("search_mode", currentSearchMode);
		outState.putString("search_value", inputField.getText().toString());
//		outState.putString("filter_value", filterField.getText().toString());
		outState.putInt("spinner_mode", modeSpinner.getSelectedItemPosition());

	}

	
	//spinner handlers
	@Override
	public void onItemSelected(AdapterView<?> parent, View v, int position,
			long id)
	{
		Log.i("mode", String.valueOf(position));
		inputField.setText("");
		
		switch (position)
		{
			case MODE_UID:
				inputField.setHint(getString(R.string.hint_user));
				currentMode = MODE_UID;
				break;
			case MODE_ARTIST:
				inputField.setHint(getString(R.string.hint_artist));
				currentMode = MODE_ARTIST;
				break;
			case MODE_TAG:
				inputField.setHint(getString(R.string.hint_tag));
				currentMode = MODE_TAG;
				break;
		}
		
	}
	
	@Override
	public void onItemClick(AdapterView<?> parent, View v, int position, long id)
	{
		Cursor albumCursor = ((SimpleCursorAdapter)albumList.getAdapter()).getCursor();
		
		albumCursor.moveToPosition(position);
		handler.onAlbumSelected(albumCursor.getString(3));	
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
			case R.id.button_submit:
			{
				Log.i("progress", "submit button pressed");
				
				instructView.setVisibility(View.GONE);
				albumList.setVisibility(View.VISIBLE);
				displayHeader.setVisibility(View.VISIBLE);
				
				
				
				//close keyboard.
				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
							      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(inputField.getWindowToken(), 0);
				
				if (inputField.getText().toString().equals("") || inputField.getText().toString().equals(" "))
				{
					Log.i("toast", "in toast");
					Toast.makeText(getActivity(), R.string.toast_no_input, Toast.LENGTH_LONG).show();
					return;
				}
				
				handler.onQuery(inputField.getText().toString(), currentMode);
				break;
			}
			
			case R.id.button_back:
			{
				//make UI Changes.
				Log.i("switch", "back");
				searchArea.setVisibility(View.VISIBLE);
				filterArea.setVisibility(View.INVISIBLE);
				inputField.setText("");
				filterField.setText("");
				albumList.setVisibility(View.GONE);
				displayHeader.setVisibility(View.GONE);
				instructView.setVisibility(View.VISIBLE);
				break;
			}
			
			case R.id.button_filter:
			{

				InputMethodManager imm = (InputMethodManager)getActivity().getSystemService(
			      Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(filterField.getWindowToken(), 0);

				String filterString = filterField.getText().toString();

				if (filterString.equals(" "))
				{
					Toast.makeText(getActivity(), R.string.toast_no_input, Toast.LENGTH_LONG).show();
					return;
				}
				
				handler.onQueryFilter(filterString);
				
				break;
			}
			
			case R.id.button_clear_filter:
			{
				handler.onQuery(inputField.getText().toString(), currentMode);
				filterField.setText("");
				break;
			}
		}
		
	}
	
	private void setMode(int mode)
	{
		switch (mode)
		{
			case DISPLAY_MODE_FILTER:
				filterArea.setVisibility(View.VISIBLE);
				searchArea.setVisibility(View.GONE);
				
				break;
				
			case DISPLAY_MODE_SEARCH:
				filterArea.setVisibility(View.GONE);
				searchArea.setVisibility(View.VISIBLE);
				break;
		}
		
	}
	
	//public methods
	public void displayQueryResults(Cursor results)
	{
		albumList.setAdapter(new SimpleCursorAdapter(getActivity(), 
				  R.layout.album_cell, 
				  results, 
				  new String[] { AlbumData.ALBUM_COLUMN, AlbumData.ARTIST_COLUMN }, 
				  new int[] { R.id.cell_album_name, R.id.cell_album_artist }, 0));
		setMode(DISPLAY_MODE_FILTER);
	}
}
