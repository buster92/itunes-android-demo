package com.andresgarrido.musicsearch.event;

import com.andresgarrido.musicsearch.model.SongResponse;

import java.util.List;

public class GetAlbumSongsOk {
	public final List<SongResponse> result;

	public GetAlbumSongsOk(List<SongResponse> result) {
		this.result = result;
	}
}
