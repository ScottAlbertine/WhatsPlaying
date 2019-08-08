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

import static android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON;
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
	private String lastAppId;

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
						handleStatusUpdate((Status) event.getData());
						break;
					case APPEVENT:
					case CLOSE:
					case UNKNOWN:
						break;
				}
			});
			handleStatusUpdate(chromecast.getStatus()); //initial population
		});
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
	 * Handle a status update from the chromecast, happens when someone changes which app is running, or the volume.
	 *
	 * @param status duh
	 */
	private void handleStatusUpdate(Status status) {
		runOnUiThread(() -> {
			Application currentApp = status.getRunningApp();
			String currentAppId = (currentApp == null) ? null : currentApp.id; //null safe check of running app id
			if (!Objects.equals(currentAppId, lastAppId)) { //detect app change
				//noinspection VariableNotUsedInsideIf on purpose
				if (currentAppId == null) { //app closing, turn off the screen.
					clearScreen();
				} else { //app starting, turn on the screen and query the media info for prompt response
					networkSafe(() -> showMediaStatus(chromecast.getMediaStatus()));
				}
				//save off the current app id so we don't do this again next time
				lastAppId = currentAppId;
			}
			//and of course show the new status on screen even if we're not in a new app
			volumeBar.setProgress((int) (status.volume.level * 100));
		});
	}

	private void clearScreen() {
		runOnUiThread(() -> {
			getWindow().clearFlags(FLAG_KEEP_SCREEN_ON);
			trackNameView.setText("");
			artistNameView.setText("");
			albumArtView.setImageDrawable(null);
			seekBar.setProgress(0);
		});
	}

	/**
	 * Update the UI to show the given media status.
	 *
	 * @param mediaStatus duh
	 */
	private void showMediaStatus(MediaStatus mediaStatus) {
		runOnUiThread(() -> {
			getWindow().addFlags(FLAG_KEEP_SCREEN_ON);
			if (mediaStatus != null) {
				Media media = mediaStatus.media;
				if (media != null) { //we often get partial objects, only use the parts we get.
					Double duration = media.duration;
					if (duration != null) {
						//noinspection NumericCastThatLosesPrecision On purpose
						seekBar.setProgress((int) ((mediaStatus.currentTime / duration) * 100));
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
			}
		});
	}
}
