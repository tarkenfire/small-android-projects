/* 
 * Date: Sep 17, 2013
 * Project: Project2
 * Package: com.hinodesoftworks.jsonutils
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.jsonutils;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A factory for creating JSON objects that allows for only one JSON object to be created at a time. 
 */
public final class JSONFactory
{
	private static JSONFactory instance = null;
	private JSONObject holder;
	
	//this constructor exists only to prevent non-singleton instantiation.
	/**
	 * Instantiates a new jSON factory.
	 */
	protected JSONFactory()
	{}
	
	/**
	 * Gets the single instance of JSONFactory.
	 *
	 * @return single instance of JSONFactory
	 */
	public static JSONFactory getInstance()
	{
		if (instance == null)
		{
			instance = new JSONFactory();
		}	
		return instance;
	}
	
	/**
	 * Creates a new JSON object.
	 */
	public void createEmptyObject()
	{
		//TODO: Make it so only one "JSON object" can exist at a time without a call to a clear function or a return function.
		holder = new JSONObject();		
	}
	
	public void createObjectFromString(String jsonString) throws JSONException
	{
		holder = new JSONObject(jsonString);
	}
	
	/**
	 * Adds a hashmap to the current JSON object in the factory
	 *
	 * @param objectName the object name
	 * @param map the map that will be converted into a JSON object
	 */
	public void addObjectToJSONObject(String objectName, HashMap<String, Object> map)
	{
		try
		{
			JSONObject objectToAdd = new JSONObject(map);
			holder.put(objectName, objectToAdd);
		}
		catch (NullPointerException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		catch (JSONException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}		
	}
	
	//these functions are technically not needed as I will be using the hashmap function this week
	//and the URL function next week, but are added for the sake of making a complete, reusable implementation
	/**
	 * Adds the array to object.
	 *
	 * @param name the name
	 * @param value the value
	 */
	public void addArrayToObject(String name, Object value)
	{
			try
			{
				holder.putOpt(name, value);
			}
			catch (JSONException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}		
	}
	
	

	//stubbed method for week two implementation
	
	
	/**
	 * Gets the JSON object currently in the factory class
	 *
	 * @return the JSON object
	 */
	public JSONObject getJSONObject()
	{
		if (holder != null)
		{
			JSONObject valueToReturn = holder;
			holder = null;
			return valueToReturn;
		}
		return null;
	}	
}
