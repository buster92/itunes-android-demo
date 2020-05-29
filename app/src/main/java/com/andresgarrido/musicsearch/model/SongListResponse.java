package com.andresgarrido.musicsearch.model;

import java.util.ArrayList;
import java.util.List;

public class SongListResponse {
	public int resultCount;
	public List<SongResponse> results = new ArrayList<>();
}
