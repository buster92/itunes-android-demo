package com.andresgarrido.musicsearch.event;

import com.andresgarrido.musicsearch.model.SongResponse;

import java.util.List;

public class SearchTermResponseOk {
	public final List<SongResponse> result;

	public SearchTermResponseOk(List<SongResponse> result) {
		this.result = result;
	}
}
