package com.example.whatsplaying.activity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
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

import static com.example.whatsplaying.util.Utils.MILLISECONDS_PER_SECOND;
import static com.example.whatsplaying.util.Utils.runInBackground;

/**
 * Shows what's playing on a given chromecast.
 * Must be launched with the index of the chromecast (in the ChromeCasts singleton's list of chromecasts) passed in the intent, using the {@link #INDEX_KEY} extra.
 *
 * @author Scott Albertine
 */
public class NowPlayingActivity extends AppCompatActivity implements ChromeCastSpontaneousEventListener {

	/** The key to use, in the intent's extras, to pass the numeric index of the chromecast we want to show. */
	public static final String INDEX_KEY = "com.example.whatsplaying.ccIndex";
	/** Number of stops on the progress bar. */
	private static final int SEEK_BAR_RESOLUTION = 1000;
	/** Number of milliseconds to wait between seek bar updates. */
	private static final int SEEK_BAR_UPDATE_FREQUENCY = 250;

	private ImageView albumArtView;
	private TextView artistNameView;
	private TextView trackNameView;
	private ProgressBar seekBar;
	private ProgressBar volumeBar;

	private ChromeCast chromecast;
	private String lastAppId;
	private MediaStatus.PlayerState playerState;
	private double duration;
	private double currentTime;

	private Handler seekBarUpdater;

	private PowerManager.WakeLock wakeLock;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		PowerManager powerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
		//noinspection deprecation There isn't a non-deprecated way to do this, and it's been deprecated for 11 api versions and not removed.
		wakeLock = powerManager.newWakeLock(PowerManager.FULL_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "whatsplaying:wakeup");

		//get the appropriate chromecast, safely.
		chromecast = ChromeCasts.get().get(Objects.requireNonNull(getIntent().getExtras()).getInt(INDEX_KEY));

		setContentView(R.layout.now_playing);

		albumArtView = findViewById(R.id.albumArt);
		artistNameView = findViewById(R.id.artistName);
		trackNameView = findViewById(R.id.trackName);
		seekBar = findViewById(R.id.seekBar);
		volumeBar = findViewById(R.id.volumeBar);

		goFullscreen();

		seekBarUpdater = new Handler();
		updateSeekBar(); //start the seek bar updating, it'll keep going on its own

		runInBackground(() -> {
			chromecast.connect();
			chromecast.registerListener(this);
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
	 * If the chromecast is playing, move the seek bar on our side without having to check on its progress constantly.
	 * Update it at the given frequency, by 1/frequency seconds.
	 * Each update queues up the next one, at the appropriate time, you only ever need to call this once, and it'll keep going forever.
	 */
	private void updateSeekBar() {
		if (playerState == MediaStatus.PlayerState.PLAYING) {
			currentTime += SEEK_BAR_UPDATE_FREQUENCY / MILLISECONDS_PER_SECOND; //increase it by however long it's been since we last ran.
			showDuration();
		}
		seekBarUpdater.postDelayed(this::updateSeekBar, SEEK_BAR_UPDATE_FREQUENCY);
	}

	public void spontaneousEventReceived(ChromeCastSpontaneousEvent event) {
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
	}

	/**
	 * Handle a status update from the chromecast, happens when someone changes which app is running, or the volume.
	 *
	 * @param status duh
	 */
	@SuppressLint("WakelockTimeout")
	private void handleStatusUpdate(Status status) {
		Application currentApp = status.getRunningApp();
		String currentAppId = (currentApp == null) ? null : currentApp.id; //null safe check of running app id
		if (!Objects.equals(currentAppId, lastAppId)) { //detect app change
			//noinspection VariableNotUsedInsideIf on purpose
			if (currentAppId != null) { //app starting, query and show the media info, and keep the screen on
				wakeLock.acquire();
				runInBackground(() -> showMediaStatus(chromecast.getMediaStatus()));
			} else { //app closing, clear the screen and let it turn off
				clearScreen();
				wakeLock.release();
			}
			//save off the current app id so we don't do this again next time
			lastAppId = currentAppId;
		}
		//and of course show the new status on screen even if we're not in a new app
		//noinspection NumericCastThatLosesPrecision On purpose
		runOnUiThread(() -> volumeBar.setProgress((int) (status.volume.level * 100)));
	}

	/**
	 * Clear the screen of everything but the volume knob.
	 */
	private void clearScreen() {
		runOnUiThread(() -> {
			trackNameView.setText("");
			artistNameView.setText("");
			albumArtView.setImageDrawable(null);
			seekBar.setProgress(0);
			currentTime = 0.0; //stop the seek bar updater from using old data, just in case.
			duration = 0.0;
		});
	}

	/**
	 * Update the UI to show the given media status.
	 *
	 * @param mediaStatus duh
	 */
	private void showMediaStatus(MediaStatus mediaStatus) {
		runOnUiThread(() -> {
			if (mediaStatus == null) {
				return;
			}
			playerState = mediaStatus.playerState; //always save it, even if it's null
			currentTime = mediaStatus.currentTime; //will never be null, we can always save it
			Media media = mediaStatus.media;
			if (media == null) {
				return;
			}
			Double mediaDuration = media.duration;
			if (mediaDuration != null) {
				duration = mediaDuration; //save it off so we can show it
				showDuration();
			}
			Map<String, Object> metadata = media.metadata;
			if (metadata != null) {
				showMediaMetadata(metadata);
			}
		});
	}

	/**
	 * Show the duration currently saved in this activity, on the seek bar.
	 */
	private void showDuration() {
		//noinspection NumericCastThatLosesPrecision On purpose
		seekBar.setMax((int) (duration * SEEK_BAR_RESOLUTION)); //make the seek bar have 1000 stops instead of the regular 100
		//noinspection NumericCastThatLosesPrecision On purpose
		seekBar.setProgress((int) (currentTime * SEEK_BAR_RESOLUTION));
	}

	/**
	 * Show the given media metadata.
	 *
	 * @param metadata duh
	 */
	private void showMediaMetadata(Map<String, Object> metadata) {
		CharSequence title = (CharSequence) metadata.get("title");
		//don't set it if it's set already, this causes the marquee to twitch
		if ((title != null) && !title.equals(trackNameView.getText())) {
			trackNameView.setText(title);
		}
		CharSequence artist = (CharSequence) metadata.get("artist");
		//don't set it if it's set already, this causes the marquee to twitch
		if ((artist != null) && !artist.equals(artistNameView.getText())) {
			artistNameView.setText(artist);
		}
		//noinspection unchecked
		List<Map<String, Object>> images = (List<Map<String, Object>>) metadata.get("images");
		if ((images != null) && !images.isEmpty()) {
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
