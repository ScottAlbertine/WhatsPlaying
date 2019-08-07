package com.example.whatsplaying.activity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.whatsplaying.R;
import com.example.whatsplaying.Utils;
import com.example.whatsplaying.adapter.ChromecastRecycleViewAdapter;
import su.litvak.chromecast.api.v2.ChromeCasts;

import java.io.IOException;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

/**
 * @author Scott Albertine
 */
public class ListChromecastsActivity extends AppCompatActivity {

	private RecyclerView chromecastRecyclerView;
	private ChromecastRecycleViewAdapter adapter;
	private RecyclerView.LayoutManager layoutManager;

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

		ChromeCasts.registerListener(adapter);

	}

	@Override
	protected void onPostCreate(Bundle savedInstanceState) {
		super.onPostCreate(savedInstanceState);

		//no networking on main thread
		Utils.runInNewThread(new Runnable() {
			public void run() {
				try {
					ChromeCasts.startDiscovery(getMyIp());
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
	}

	private InetAddress getMyIp() throws UnknownHostException {
		WifiManager wifiMgr = (WifiManager) getSystemService(WIFI_SERVICE);
		int ipAddress = wifiMgr.getConnectionInfo().getIpAddress();
		// Convert little-endian to big-endian if needed
		if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
			ipAddress = Integer.reverseBytes(ipAddress);
		}
		byte[] ipByteArray = BigInteger.valueOf(ipAddress).toByteArray();
		return InetAddress.getByAddress(ipByteArray);
	}

}
