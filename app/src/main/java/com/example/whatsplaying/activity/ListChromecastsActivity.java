package com.example.whatsplaying.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.whatsplaying.R;
import com.example.whatsplaying.adapter.ChromecastRecycleViewAdapter;
import com.example.whatsplaying.dto.Chromecast;
import com.example.whatsplaying.service.ChromecastDiscoveryService;

/**
 * @author Scott Albertine
 */
public class ListChromecastsActivity extends AppCompatActivity {

	private RecyclerView chromecastRecyclerView;
	private ChromecastRecycleViewAdapter adapter;
	private RecyclerView.LayoutManager layoutManager;

	private ChromecastDiscoveryService service;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_chromecasts);

		chromecastRecyclerView = findViewById(R.id.chromecasts_recycler_view);
		chromecastRecyclerView.setHasFixedSize(true);

		layoutManager = new LinearLayoutManager(this);
		chromecastRecyclerView.setLayoutManager(layoutManager);

		adapter = new ChromecastRecycleViewAdapter();
		chromecastRecyclerView.setAdapter(adapter);

		service = new ChromecastDiscoveryService(this);
	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		service.discoverChromecasts();
	}

	public void addChromecast(final Chromecast chromecast) {
		runOnUiThread(new Runnable() {
			public void run() {
				adapter.addChromecast(chromecast);
			}
		});
	}

}
