package com.andresgarrido.musicsearch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.TextureView;
import android.view.View;

import com.andresgarrido.musicsearch.adapter.SongRecyclerAdapter;
import com.andresgarrido.musicsearch.databinding.ActivityMainBinding;
import com.andresgarrido.musicsearch.event.SearchTermResponseOk;
import com.andresgarrido.musicsearch.network.ApiHelper;
import com.andresgarrido.musicsearch.util.EndlessRecyclerOnScrollListener;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

public class MainActivity extends AppCompatActivity {
	private ActivityMainBinding binding;

	private Thread waitThreadAmount;
	private long lastRequestTimeSearch = 0;
	private final long UPDATE_AMOUNT_DEBOUNCE_TIME = 1000;
	private SongRecyclerAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		binding = ActivityMainBinding.inflate(getLayoutInflater());
		View view = binding.getRoot();
		setContentView(view);


		adapter = new SongRecyclerAdapter(this);
		binding.recyclerView.setAdapter(adapter);
		LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
		binding.recyclerView.setLayoutManager(linearLayoutManager);
		binding.recyclerView.addOnScrollListener(new EndlessRecyclerOnScrollListener(linearLayoutManager) {
			@Override
			public void onLoadMore(int currentPage) {
				final String term = getSearchTerm();
				if (currentPage > 0 && !TextUtils.isEmpty(term)) {
					ApiHelper.search(term, adapter.getItemCount());
				}
			}
		});

		binding.searchEditText.addTextChangedListener(new TextWatcher() {
			@Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
			@Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

			}
			@Override public void afterTextChanged(Editable editable) {
				trySearch();
			}
		});
	}

	@Override
	protected void onResume() {
		super.onResume();
		EventBus.getDefault().register(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		EventBus.getDefault().unregister(this);
	}

	private void trySearch() {
		adapter.clearItems();
		lastRequestTimeSearch = System.currentTimeMillis();
		if (waitThreadAmount == null || !waitThreadAmount.isAlive()) {
			waitThreadAmount = new Thread(() -> {
				long current = System.currentTimeMillis();
				boolean interrupted = false;
				while (current - lastRequestTimeSearch < UPDATE_AMOUNT_DEBOUNCE_TIME) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
						interrupted = true;
					}
					current = System.currentTimeMillis();
				}

				if (!interrupted) runOnUiThread(this::search);

			});
			waitThreadAmount.start();
		}
	}


	private String getSearchTerm() {
		if (binding.searchEditText.getText() == null) return "";
		return binding.searchEditText.getText().toString().trim();
	}

	private void search() {
		String term = getSearchTerm();

		if (TextUtils.isEmpty(term)) return;

		ApiHelper.search(term, 0);
	}

	@Subscribe(threadMode = ThreadMode.MAIN)
	public void onSearchTermResult(SearchTermResponseOk event) {
		adapter.addItems(event.result);
	}
}
