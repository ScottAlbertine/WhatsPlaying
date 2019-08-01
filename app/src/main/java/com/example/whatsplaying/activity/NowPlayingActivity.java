package com.example.whatsplaying.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.whatsplaying.R;
import su.litvak.chromecast.api.v2.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class NowPlayingActivity extends AppCompatActivity {

	private ImageView albumArtView;
	private TextView artistNameView;
	private TextView trackNameView;
	private ProgressBar seekBar;
	private ProgressBar volumeBar;

	private ChromeCast castApi;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		String ip = getIntent().getExtras().getString("ip");
		castApi = new ChromeCast(ip);

		setContentView(R.layout.now_playing);

		albumArtView = findViewById(R.id.albumArt);
		artistNameView = findViewById(R.id.artistName);
		trackNameView = findViewById(R.id.trackName);
		seekBar = findViewById(R.id.seekBar);
		volumeBar = findViewById(R.id.volumeBar);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//go fullscreen
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		trackNameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
											| View.SYSTEM_UI_FLAG_FULLSCREEN
											| View.SYSTEM_UI_FLAG_LAYOUT_STABLE
											| View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
											| View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
											| View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		//this is ghetto as hell but it should work, we can make a much nicer version once it's functional
		new Thread(new Runnable() {
			public void run() {
				try {
					castApi.connect();
					castApi.registerListener(new ChromeCastSpontaneousEventListener() {
						public void spontaneousEventReceived(ChromeCastSpontaneousEvent event) {
							if (event.getType().getDataClass().equals(MediaStatus.class)) {
								showMediaStatus((MediaStatus) event.getData());
							}
						}
					});
					showMediaStatus(castApi.getMediaStatus()); //initial population
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}

	public void showMediaStatus(final MediaStatus mediaStatus) {
		runOnUiThread(new Runnable() {
			public void run() {
				Media media = mediaStatus.media;
				if (media != null) { //we often get partial objects, only use the parts we get.
					Map<String, Object> metadata = media.metadata;
					if (metadata != null) {
						String title = (String) metadata.get("title");
						if (title != null) {
							trackNameView.setText(title);
						}
						String artist = (String) metadata.get("artist");
						if (artist != null) {
							artistNameView.setText(artist);
						}
						/*
						List<Map<String, Object>> images = (List<Map<String, Object>>) metadata.get("images");
						if (images != null) {
							albumArtView.setImageURI(Uri.parse((String) images.get(0).get("url")));
						}
						*/
						//volumeBar.setProgress(mediaStatus.media.
						seekBar.setProgress((int) ((mediaStatus.currentTime / media.duration) * 100));
					}
				}
			}
		});

	}
}
