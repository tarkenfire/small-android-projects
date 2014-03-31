/* 
 * Date: Oct 3, 2013
 * Project: LFMTopTracker
 * Package: com.hinodesoftworks.lfmtoptracker
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.lfmtoptracker;
 

/**
 * A data model class that represents a musical album.
 */
public class Album
{
	private String albumName;
	private String artistName;
	
	private String imageURL;
	private String albumID;
	
	/**
	 * Gets the album name.
	 *
	 * @return the album name
	 */
	public String getAlbumName()
	{
		return albumName;
	}
	
	/**
	 * Sets the album name.
	 *
	 * @param albumName the new album name
	 */
	public void setAlbumName(String albumName)
	{
		this.albumName = albumName;
	}
	
	/**
	 * Gets the artist name.
	 *
	 * @return the artist name
	 */
	public String getArtistName()
	{
		return artistName;
	}
	
	/**
	 * Sets the artist name.
	 *
	 * @param artistName the new artist name
	 */
	public void setArtistName(String artistName)
	{
		this.artistName = artistName;
	}

	/**
	 * Gets the image url.
	 *
	 * @return the image url
	 */
	public String getImageURL()
	{
		return imageURL;
	}
	
	/**
	 * Sets the image url.
	 *
	 * @param imageURL the new image url
	 */
	public void setImageURL(String imageURL)
	{
		this.imageURL = imageURL;
	}

	public String getAlbumID()
	{
		return albumID;
	}

	public void setAlbumID(String albumID)
	{
		this.albumID = albumID;
	}
}
