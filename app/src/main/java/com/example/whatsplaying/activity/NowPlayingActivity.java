package com.example.whatsplaying.activity;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.example.whatsplaying.R;

public class NowPlayingActivity extends AppCompatActivity {

	private ImageView albumArtView;
	private TextView artistNameView;
	private TextView trackNameView;
	private ProgressBar seekBar;
	private ProgressBar volumeBar;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

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
	}
}
