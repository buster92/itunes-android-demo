package com.andresgarrido.musicsearch.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;

import com.andresgarrido.musicsearch.event.FinishedLoadingSong;
import com.andresgarrido.musicsearch.event.StartedLoadingSong;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;

public class MediaPlayerSingleton {
	private static MediaPlayerSingleton INSTANCE;
	private MediaPlayer mediaPlayer;
	private PlayerListener listener;
	private Thread thread;
	private boolean isPlaying = false;

	private MediaPlayerSingleton() {
		mediaPlayer = new MediaPlayer();
		mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
		mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
			@Override
			public void onCompletion(MediaPlayer mp) {
				isPlaying = false;
				if (thread != null)
					thread.interrupt();
			}
		});

		mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
			@Override
			public void onPrepared(MediaPlayer mp) {
				EventBus.getDefault().post(new FinishedLoadingSong());
				mediaPlayer.start();
				isPlaying = true;
				startProgressThread();
			}
		});
		mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
			@Override
			public boolean onError(MediaPlayer mp, int what, int extra) {
				isPlaying = false;
				if (thread != null)
					thread.interrupt();

				mediaPlayer.setLooping(false);
				mediaPlayer.stop();
				mediaPlayer.reset();
				return false;
			}
		});
	}

	public static MediaPlayerSingleton getInstance() {
		if (INSTANCE == null)
			INSTANCE = new MediaPlayerSingleton();
		return INSTANCE;
	}


	public void playUrl(PlayerListener listener, String url) {
		stop();
		this.listener = listener;
		try {
			mediaPlayer.setDataSource(url);
			mediaPlayer.prepareAsync();
			EventBus.getDefault().post(new StartedLoadingSong());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public boolean isPlaying() {
		return isPlaying;
	}

	public void stop() {
		if (mediaPlayer != null) {
			mediaPlayer.setLooping(false);
			mediaPlayer.stop();
			mediaPlayer.reset();
			if (listener != null)
				listener.onProgressChanged(0);
		}
		isPlaying = false;
	}

	private void startProgressThread() {
		thread = new Thread(new Runnable() {
			public void run() {
				try {

					while (isPlaying && mediaPlayer != null && mediaPlayer.isPlaying()) {
						float progress = ((float) mediaPlayer.getCurrentPosition() / mediaPlayer.getDuration()) * 100;

						Log.d("andres", "Progress: " + progress);
						if (listener != null)
							listener.onProgressChanged((int) progress);

						Thread.sleep(200);

					}
				} catch (Exception e) {
					e.printStackTrace();
					if (listener != null)
						listener.onProgressChanged(0);
				}
			}
		});
		thread.start();
	}


	public interface PlayerListener {
		void onProgressChanged(int progress);
	}
}

