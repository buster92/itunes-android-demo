package com.andresgarrido.musicsearch.network;

import com.andresgarrido.musicsearch.model.SongResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface Api {

	@GET("search")
	Call<SongResponse> search(@Query("term") String term, @Query("mediaType") String mediaType,
							  @Query("limit") int limit);
	@GET("lookup")
	Call<SongResponse> getAlbumSongs(@Query("id") String albumId, @Query("entity") String entity);
}
