package com.andresgarrido.musicsearch.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.andresgarrido.musicsearch.R;
import com.andresgarrido.musicsearch.databinding.ItemSongPlayerBinding;
import com.andresgarrido.musicsearch.model.SongResponse;
import com.andresgarrido.musicsearch.util.MediaPlayerSingleton;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

public class SongPlayerRecyclerAdapter extends RecyclerView.Adapter<SongPlayerRecyclerAdapter.SongPlayerHolder> {
	private Context context;
	private List<SongResponse> items;

	public SongPlayerRecyclerAdapter(Context context, List<SongResponse> newItems) {
		this.context = context;
		this.items = newItems;
	}

	@Override
	public SongPlayerHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		ItemSongPlayerBinding itemBinding = ItemSongPlayerBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
		SongPlayerHolder holder = new SongPlayerHolder(itemBinding);

		holder.playIcon.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				int position =  holder.getAdapterPosition();
				if (position < 0)
					return;

				if (holder.playIcon.getDrawable().getConstantState() == context.getResources().getDrawable( R.drawable.ic_stop_black_24dp).getConstantState()) {
					MediaPlayerSingleton.getInstance().stop();
				}
				else {
					MediaPlayerSingleton.getInstance().playUrl(new MediaPlayerSingleton.PlayerListener() {
						@Override
						public void onProgressChanged(int progress) {
							holder.progressBar.setProgress(progress);
							if (progress == 100 || progress == 0) {
								holder.playIcon.setImageResource(R.drawable.ic_play_arrow_black_24dp);
							} else {
								holder.playIcon.setImageResource(R.drawable.ic_stop_black_24dp);
							}
						}
					}, items.get(position).previewUrl);
				}
			}
		});
		return holder;
	}

	@Override
	public void onBindViewHolder(SongPlayerRecyclerAdapter.SongPlayerHolder holder, int pos) {
		final SongResponse item = items.get(pos);
		holder.titleTv.setText(item.trackName);
	}

	@Override
	public long getItemId(int position) {
		return items.get(position).trackId;
	}

	@Override
	public int getItemCount() {
		return items.size();
	}

	@Override
	public int getItemViewType(int position) {
		return position;
	}

	class SongPlayerHolder extends RecyclerView.ViewHolder {
		TextView titleTv;
		ProgressBar progressBar;
		FloatingActionButton playIcon;

		SongPlayerHolder(ItemSongPlayerBinding binding) {
			super(binding.getRoot());
			titleTv = binding.titleTv;
			progressBar = binding.progressBar;
			playIcon = binding.playIconIv;
		}
	}
}