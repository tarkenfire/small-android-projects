/* 
 * Date: Sep 11, 2013
 * Project: Project1
 * Package: com.hinodesoftworks.project1
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.project1;


/**
 * Represents the various states of API errors provided via the LastFM API.
 */
public enum APIError
{
	//the API I'm using eschews normal HTTP errors in favor of it's own system of errors.
	//this means that the service will ALWAYS return an XML/JSON response, even if there 
	//is an error.
	AUTH_FAILED(4),
	INVALID_PARAMATER(6),
	REQUEST_FAILED(8),
	SERVICE_OFLINE(11),
	TEMP_ERROR(16),
	RESPONSE_OK(100) //there is no "okay" code for the api, no errors == assume ok
	;
	
	private int code;
	
	/**
	 * Instantiates a new API error.
	 *
	 * @param code the error code
	 */
	private APIError(int code)
	{
		this.code = code;
	}
	
	/**
	 * Gets the error code.
	 *
	 * @return the error code
	 */
	public int getCode()
	{
		return this.code;
	}
}
