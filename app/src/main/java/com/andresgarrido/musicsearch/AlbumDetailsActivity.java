package com.andresgarrido.musicsearch;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.andresgarrido.musicsearch.databinding.ActivityAlbumDetailsBinding;
import com.andresgarrido.musicsearch.databinding.ContentAlbumDetailsBinding;
import com.andresgarrido.musicsearch.databinding.ItemSongPlayerBinding;
import com.andresgarrido.musicsearch.event.FinishedLoadingSong;
import com.andresgarrido.musicsearch.event.GetAlbumSongsError;
import com.andresgarrido.musicsearch.event.GetAlbumSongsOk;
import com.andresgarrido.musicsearch.event.StartedLoadingSong;
import com.andresgarrido.musicsearch.model.SongResponse;
import com.andresgarrido.musicsearch.network.ApiHelper;
import com.andresgarrido.musicsearch.util.MediaPlayerSingleton;
import com.bumptech.glide.Glide;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;


public class AlbumDetailsActivity extends AppCompatActivity {
	public static final String EXTRA_ID = "extra_id";

	ActivityAlbumDetailsBinding binding;
	ContentAlbumDetailsBinding detailsBinding;

	private long collectionId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityAlbumDetailsBinding.inflate(getLayoutInflater());
		detailsBinding = binding.albumDetailsView;
		View view = binding.getRoot();
		setContentView(view);

		setSupportActionBar(binding.toolbar);

		Bundle extras = getIntent().getExtras();

		if (extras != null) {
			collectionId = extras.getLong(EXTRA_ID);
		}
		else {
			throw new IllegalStateException("Must provide EXTRA_ID extra");
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);

		binding.albumDetailsProgressBar.setVisibility(View.VISIBLE);
		ApiHelper.getAlbumSongs(collectionId);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onGetAlbumOk(GetAlbumSongsOk event) {
		binding.albumDetailsProgressBar.setVisibility(View.GONE);
		if (event.result.size() == 0) {
			Toast.makeText(this, R.string.error_album_details_no_songs, Toast.LENGTH_LONG).show();
		}
		else {
			binding.toolbarLayout.setTitle(event.result.get(0).bandName.concat(" - ").concat(event.result.get(0).albumName));
			Glide.with(this).load(event.result.get(0).artworkUrl100).into(binding.albumBackgroundIv);

			for (SongResponse song : event.result) {
				if (TextUtils.isEmpty(song.trackName) || TextUtils.isEmpty(song.previewUrl))
					continue;
				ItemSongPlayerBinding itemBinding = ItemSongPlayerBinding.inflate(getLayoutInflater());

				itemBinding.playIconIv.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {

						if (itemBinding.playIconIv.getDrawable().getConstantState() == getResources().getDrawable( R.drawable.ic_stop_black_24dp).getConstantState()) {
							MediaPlayerSingleton.getInstance().stop();
						}
						else {
							MediaPlayerSingleton.getInstance().playUrl(new MediaPlayerSingleton.PlayerListener() {
								@Override
								public void onProgressChanged(int progress) {
									itemBinding.progressBar.setProgress(progress);
									if (progress == 100 || progress == 0) {
										itemBinding.playIconIv.setImageResource(R.drawable.ic_play_arrow_black_24dp);
									} else {
										itemBinding.playIconIv.setImageResource(R.drawable.ic_stop_black_24dp);
									}
								}
							}, song.previewUrl);
						}
					}
				});
				itemBinding.titleTv.setText(song.trackName);
				detailsBinding.songsContainerLl.addView(itemBinding.getRoot());
			}
		}
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onGetAlbumError(GetAlbumSongsError event) {
		binding.albumDetailsProgressBar.setVisibility(View.GONE);
		Toast.makeText(this, R.string.error_connectivity, Toast.LENGTH_LONG).show();
		finish();
	}
	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onStartLoadingSong(StartedLoadingSong event) {
		binding.albumDetailsProgressBar.setVisibility(View.VISIBLE);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onFinishLoadingSong(FinishedLoadingSong event) {
		binding.albumDetailsProgressBar.setVisibility(View.GONE);

	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		MediaPlayerSingleton.getInstance().stop();
	}
}
