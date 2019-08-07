package com.example.whatsplaying.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.bumptech.glide.Glide;
import com.example.whatsplaying.R;
import com.example.whatsplaying.Utils;
import su.litvak.chromecast.api.v2.*;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.List;
import java.util.Map;

public class NowPlayingActivity extends AppCompatActivity {

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
		Utils.runInNewThread(new Runnable() {
			public void run() {
				try {
					chromecast.connect();
					chromecast.registerListener(new ChromeCastSpontaneousEventListener() {
						public void spontaneousEventReceived(ChromeCastSpontaneousEvent event) {
							switch (event.getType()) {
								case MEDIA_STATUS:
									showMediaStatus((MediaStatus) event.getData());
									break;
								case STATUS:
									showStatus((Status) event.getData());
									break;
								case APPEVENT:
									break;
								case CLOSE:
									break;
								case UNKNOWN:
									break;
							}
						}
					});
					showMediaStatus(chromecast.getMediaStatus()); //initial population
					showStatus(chromecast.getStatus());
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
						List<Map<String, Object>> images = (List<Map<String, Object>>) metadata.get("images");
						if (images != null) {
							Map<String, Object> image = images.get(0);
							if (image != null) {
								String url = (String) image.get("url");
								if (url != null) {
									Glide.with(albumArtView.getContext()).load(url).into(albumArtView);
								}
							}
						}
						seekBar.setProgress((int) ((mediaStatus.currentTime / media.duration) * 100));
					}
				}
			}
		});
	}

	public void showStatus(final Status status) {
		runOnUiThread(new Runnable() {
			public void run() {
				volumeBar.setProgress((int) (status.volume.level * 100));
			}
		});
	}
}
