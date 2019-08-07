package com.example.whatsplaying.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.whatsplaying.R;
import com.example.whatsplaying.Utils;
import com.fasterxml.jackson.databind.ObjectMapper;
import su.litvak.chromecast.api.v2.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Map;

public class NowPlayingActivity extends AppCompatActivity {

	ObjectMapper objectMapper = Utils.createJSONMapper();

	private ImageView albumArtView;
	private TextView artistNameView;
	private TextView trackNameView;
	private ProgressBar seekBar;
	private ProgressBar volumeBar;

	private ChromeCast chromecast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		int index = getIntent().getExtras().getInt("com.example.whatsplaying.ccIndex");
		chromecast = ChromeCasts.get().get(index);

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
		Utils.runInNewThread(new Runnable() {
			public void run() {
				try {
					chromecast.connect();
					chromecast.registerListener(new ChromeCastSpontaneousEventListener() {
						public void spontaneousEventReceived(ChromeCastSpontaneousEvent event) {
							ChromeCastSpontaneousEvent.SpontaneousEventType type = event.getType();
							switch (type) {
								case MEDIA_STATUS:
									showMediaStatus((MediaStatus) event.getData());
									break;
								case STATUS:
									break;
								case APPEVENT:
									break;
								case CLOSE:
									break;
								case UNKNOWN:
									//We also get Device objects in here, though they're not parsed correctly, that's how we see volume changes, handle them accordingly
									//BetterResponse response = objectMapper.treeToValue((TreeNode) event.getData(), BetterResponse.class);
									break;
							}
						}
					});
					showMediaStatus(chromecast.getMediaStatus()); //initial population
					showVolume(chromecast.getStatus().volume);
				} catch (IOException e) {
					e.printStackTrace();
				} catch (GeneralSecurityException e) {
					e.printStackTrace();
				}
			}
		});
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
						seekBar.setProgress((int) ((mediaStatus.currentTime / media.duration) * 100));
					}
				}
			}
		});
	}

	public void showVolume(final Volume volume) {
		runOnUiThread(new Runnable() {
			public void run() {
				volumeBar.setProgress((int) (volume.level * 100));
			}
		});
	}
}
