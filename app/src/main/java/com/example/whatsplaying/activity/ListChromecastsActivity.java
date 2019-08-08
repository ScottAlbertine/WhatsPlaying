package com.example.whatsplaying.activity;

import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.example.whatsplaying.R;
import com.example.whatsplaying.adapter.ChromecastRecycleViewAdapter;
import su.litvak.chromecast.api.v2.ChromeCast;
import su.litvak.chromecast.api.v2.ChromeCasts;
import su.litvak.chromecast.api.v2.ChromeCastsListener;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import static com.example.whatsplaying.util.Utils.runInBackground;

/**
 * @author Scott Albertine
 */
public class ListChromecastsActivity extends AppCompatActivity implements ChromeCastsListener {

	private ChromecastRecycleViewAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.list_chromecasts);

		RecyclerView chromecastRecyclerView = findViewById(R.id.chromecasts_recycler_view);
		chromecastRecyclerView.setHasFixedSize(true);
		chromecastRecyclerView.setLayoutManager(new LinearLayoutManager(this));
		adapter = new ChromecastRecycleViewAdapter();
		chromecastRecyclerView.setAdapter(adapter);

		ChromeCasts.registerListener(this);
		runInBackground(() -> ChromeCasts.startDiscovery(getMyIp())); //no networking on main thread
	}

	public void newChromeCastDiscovered(ChromeCast chromeCast) {
		runOnUiThread(adapter::notifyDataSetChanged);
	}

	public void chromeCastRemoved(ChromeCast chromeCast) {
		runOnUiThread(adapter::notifyDataSetChanged);
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
