package com.andresgarrido.musicsearch.adapter;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.andresgarrido.musicsearch.AlbumDetailsActivity;
import com.andresgarrido.musicsearch.R;
import com.andresgarrido.musicsearch.databinding.ItemSearchResultBinding;
import com.andresgarrido.musicsearch.model.SongResponse;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SongRecyclerAdapter extends RecyclerView.Adapter<SongRecyclerAdapter.SongHolder> {
	private Context context;
	private List<SongResponse> items;

	public SongRecyclerAdapter(Context context) {
		this.context = context;
		this.items = new ArrayList<>();
	}

	public void clearItems() {
		items = new ArrayList<>();
		notifyDataSetChanged();
	}

	public void addItems(List<SongResponse> newItems) {
		final int size = items.size();
		items.addAll(newItems);
		notifyItemRangeInserted(size, newItems.size());
	}

	@Override
	public SongHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
		ItemSearchResultBinding itemBinding = ItemSearchResultBinding .inflate(LayoutInflater.from(parent.getContext()), parent, false);
		SongHolder holder = new SongHolder(itemBinding);
		itemBinding.getRoot().setOnClickListener(v -> {
			int position =  holder.getAdapterPosition();
			if (position < 0)
				return;
			long collectionId = items.get(position).albumId;

			Intent intent = new Intent(context, AlbumDetailsActivity.class);
			intent.putExtra(AlbumDetailsActivity.EXTRA_ID, collectionId);
			context.startActivity(intent);
		});
		return holder;
	}

	@Override
	public void onBindViewHolder(SongRecyclerAdapter.SongHolder holder, int pos) {
		final SongResponse item = items.get(pos);

		Glide.with(context)
				.load(item.artworkUrl100)
				.into(holder.imageView);

		holder.trackNameTv.setText(item.trackName);
		holder.artistNameTv.setText(context.getString(R.string.artist_name, item.bandName));
		holder.albumNameTv.setText(context.getString(R.string.album_name, item.albumName));
		holder.genreTv.setText(context.getString(R.string.genre_name, item.genre));
	}


	@Override
	public int getItemCount() {
		return items.size();
	}


	class SongHolder extends RecyclerView.ViewHolder {

		TextView albumNameTv;
		TextView artistNameTv;
		TextView trackNameTv;
		TextView genreTv;
		ImageView imageView;

		SongHolder(ItemSearchResultBinding binding) {
			super(binding.getRoot());
			albumNameTv = binding.albumNameTv;
			artistNameTv = binding.artistNameTv;
			imageView = binding.imageView;
			trackNameTv = binding.trackNameTv;
			genreTv = binding.genreTv;
		}
	}
}