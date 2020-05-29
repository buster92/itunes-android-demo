package com.andresgarrido.musicsearch;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.andresgarrido.musicsearch.adapter.SongPlayerRecyclerAdapter;
import com.andresgarrido.musicsearch.databinding.ActivityAlbumDetailsBinding;
import com.andresgarrido.musicsearch.databinding.ActivityMainBinding;
import com.andresgarrido.musicsearch.databinding.ContentAlbumDetailsBinding;
import com.andresgarrido.musicsearch.event.FinishedLoadingSong;
import com.andresgarrido.musicsearch.event.GetAlbumSongsOk;
import com.andresgarrido.musicsearch.event.SearchTermResponseOk;
import com.andresgarrido.musicsearch.event.StartedLoadingSong;
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
	private SongPlayerRecyclerAdapter adapter;

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

		ApiHelper.getAlbumSongs(collectionId);
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
		binding.albumDetailsProgressBar.setVisibility(View.GONE);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSearchTermResult(GetAlbumSongsOk event) {
		if (event.result.size() == 0) {
			Toast.makeText(this, R.string.error_album_details_no_songs, Toast.LENGTH_LONG).show();
		}
		else {
			binding.toolbarLayout.setTitle(event.result.get(0).albumName);
			Glide.with(this).load(event.result.get(0).artworkUrl100).into(binding.albumBackgroundIv);

			adapter = new SongPlayerRecyclerAdapter(this, event.result);
			detailsBinding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
			detailsBinding.recyclerView.setAdapter(adapter);
		}
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
