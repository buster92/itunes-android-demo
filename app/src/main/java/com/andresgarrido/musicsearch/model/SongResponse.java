package com.andresgarrido.musicsearch.model;

import com.google.gson.annotations.SerializedName;

public class SongResponse {
	public long trackId;

	public String artworkUrl100;

	public String artworkUrl30;

	@SerializedName("collectionId")
	public long albumId;

	@SerializedName("collectionName")
	public String albumName;

	@SerializedName("artistName")
	public String bandName;

	public String trackName;

	@SerializedName("primaryGenreName")
	public String genre;

	public String previewUrl;
}
