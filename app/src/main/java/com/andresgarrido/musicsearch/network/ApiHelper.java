package com.andresgarrido.musicsearch.network;

import androidx.annotation.NonNull;

import com.andresgarrido.musicsearch.event.GetAlbumSongsError;
import com.andresgarrido.musicsearch.event.GetAlbumSongsOk;
import com.andresgarrido.musicsearch.event.SearchTermResponseError;
import com.andresgarrido.musicsearch.event.SearchTermResponseOk;
import com.andresgarrido.musicsearch.model.SongListResponse;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ApiHelper {
	private static final int LIMIT = 20;
	private static final String MEDIA_TYPE = "music";
	private static final String ENTITY_TYPE = "song";

	private static Api getEndpoint() {
		return ApiClient.getClient().create(Api.class);
	}

	public static void search(String term, int offset) {
		getEndpoint().search(term, MEDIA_TYPE, LIMIT, offset).enqueue(new Callback<SongListResponse>() {
			@Override
			public void onResponse(@NonNull Call<SongListResponse> call, @NonNull Response<SongListResponse> response) {
				if (response.isSuccessful() && response.body() != null) {
					EventBus.getDefault().post(new SearchTermResponseOk(response.body().results));
					return;
				}
				EventBus.getDefault().post(new SearchTermResponseError());
			}

			@Override
			public void onFailure(@NonNull Call<SongListResponse> call, Throwable t) {
				t.printStackTrace();
				EventBus.getDefault().post(new SearchTermResponseError());
			}
		});
	}

	public static void getAlbumSongs(long albumId) {
		getEndpoint().getAlbumSongs(albumId, ENTITY_TYPE).enqueue(new Callback<SongListResponse>() {
			@Override
			public void onResponse(Call<SongListResponse> call, Response<SongListResponse> response) {
				if (response.isSuccessful() && response.body() != null) {
					EventBus.getDefault().post(new GetAlbumSongsOk(response.body().results));
					return;
				}
				EventBus.getDefault().post(new GetAlbumSongsError());
			}

			@Override
			public void onFailure(Call<SongListResponse> call, Throwable t) {
				t.printStackTrace();
				EventBus.getDefault().post(new GetAlbumSongsError());
			}
		});
	}
}
