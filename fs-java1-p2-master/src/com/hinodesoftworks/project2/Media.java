/* 
 * Date: Sep 17, 2013
 * Project: Project2
 * Package: com.hinodesoftworks.project2
 * @author Michael Mancuso
 *
 */
package com.hinodesoftworks.project2;


/**
 * Generic data model class that can represent either a music artist, a music album, or a song.
 */
public class Media
{
	private String artistName;
	private String albumName;
	private int playCount;
	private String imageURL;
	
	
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
	 * Gets the play count.
	 *
	 * @return the play count
	 */
	public int getPlayCount()
	{
		return playCount;
	}
	
	/**
	 * Sets the play count.
	 *
	 * @param playCount the new play count
	 */
	public void setPlayCount(int playCount)
	{
		this.playCount = playCount;
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

}
