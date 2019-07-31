package com.example.whatsplaying;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class FullscreenActivity extends AppCompatActivity {
	private TextView artistNameView;
	private TextView albumNameView;
	private TextView trackNameView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_fullscreen);

		artistNameView = findViewById(R.id.artistName);
		albumNameView = findViewById(R.id.albumName);
		trackNameView = findViewById(R.id.trackName);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);
		//go fullscreen
		ActionBar actionBar = getSupportActionBar();
		if (actionBar != null) {
			actionBar.hide();
		}
		artistNameView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
											 | View.SYSTEM_UI_FLAG_FULLSCREEN
											 | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
											 | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
											 | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
											 | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
	}
}
