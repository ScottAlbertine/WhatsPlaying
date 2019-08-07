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
import su.litvak.chromecast.api.v2.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import static com.example.whatsplaying.util.Utils.networkSafe;

/**
 * Shows what's playing on a given chromecast.
 * Must be launched with the index of the chromecast (in the ChromeCasts singleton's list of chromecasts) passed in the intent, using the {@link #INDEX_KEY} extra.
 *
 * @author Scott Albertine
 */
public class NowPlayingActivity extends AppCompatActivity {

	/** The key to use, in the intent's extras, to pass the numeric index of the chromecast we want to show. */
	public static final String INDEX_KEY = "com.example.whatsplaying.ccIndex";

	private ImageView albumArtView;
	private TextView artistNameView;
	private TextView trackNameView;
	private ProgressBar seekBar;
	private ProgressBar volumeBar;

	private ChromeCast chromecast;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//get the appropriate chromecast, safely.
		chromecast = ChromeCasts.get().get(Objects.requireNonNull(getIntent().getExtras()).getInt(INDEX_KEY));

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
		goFullscreen();
		networkSafe(() -> {
			chromecast.connect();
			chromecast.registerListener((ChromeCastSpontaneousEvent event) -> {
				switch (event.getType()) {
					case MEDIA_STATUS:
						showMediaStatus((MediaStatus) event.getData());
						break;
					case STATUS:
						showStatus((Status) event.getData());
						break;
					case APPEVENT:
					case CLOSE:
					case UNKNOWN:
						break;
				}
			});
			showMediaStatus(chromecast.getMediaStatus()); //initial population
			showStatus(chromecast.getStatus());
		});

		//For now, this seems to fix the "chromecast won't notify us of media info" problem
		//TODO: figure out a better way to do this so we don't have to tap on the screen
		volumeBar.setOnClickListener((View v) -> networkSafe(() -> showMediaStatus(chromecast.getMediaStatus())));
	}

	/**
	 * Go fullscreen.
	 */
	private void goFullscreen() {
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
	}

	/**
	 * Update the UI to show the given media status.
	 *
	 * @param mediaStatus duh
	 */
	private void showMediaStatus(MediaStatus mediaStatus) {
		runOnUiThread(() -> {
			Media media = mediaStatus.media;
			if (media != null) { //we often get partial objects, only use the parts we get.
				if (media.duration != null) {
					//noinspection NumericCastThatLosesPrecision On purpose
					seekBar.setProgress((int) ((mediaStatus.currentTime / media.duration) * 100));
				}
				Map<String, Object> metadata = media.metadata;
				if (metadata != null) {
					CharSequence title = (CharSequence) metadata.get("title");
					if (title != null) {
						trackNameView.setText(title);
					}
					CharSequence artist = (CharSequence) metadata.get("artist");
					if (artist != null) {
						artistNameView.setText(artist);
					}
					//TODO: check this cast, and the others, write the really annoying if/then blocks we need
					//noinspection unchecked
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
				}
			}
		});
	}

	/**
	 * Update the UI to show the given device status.
	 *
	 * @param status duh
	 */
	private void showStatus(Status status) {
		//noinspection NumericCastThatLosesPrecision On purpose
		runOnUiThread(() -> volumeBar.setProgress((int) (status.volume.level * 100)));
	}
}
