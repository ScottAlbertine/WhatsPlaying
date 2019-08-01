package com.example.whatsplaying.service;

import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import com.example.whatsplaying.activity.ListChromecastsActivity;
import com.example.whatsplaying.dto.Chromecast;

/**
 * @author Scott Albertine
 */
public class ChromecastDiscoveryService implements NsdManager.DiscoveryListener {

	private ListChromecastsActivity parent;
	private NsdManager nsdManager;

	public ChromecastDiscoveryService(ListChromecastsActivity parent) {
		this.parent = parent;
		nsdManager = parent.getApplicationContext().getSystemService(NsdManager.class);
	}

	public void discoverChromecasts() {
		nsdManager.discoverServices("_googlecast._tcp.", NsdManager.PROTOCOL_DNS_SD, this);
	}

	public void onServiceFound(NsdServiceInfo serviceInfo) {
		//ugh, you apparently can't re-use resolve listeners, so we have to create a new one here, and an AIC is the cleanest way
		nsdManager.resolveService(serviceInfo, new NsdManager.ResolveListener() {

			public void onServiceResolved(NsdServiceInfo serviceInfo) {
				parent.addChromecast(new Chromecast(serviceInfo));
			}

			public void onResolveFailed(NsdServiceInfo serviceInfo, int errorCode) {
			}
		});
	}

	public void onStartDiscoveryFailed(String serviceType, int errorCode) {

	}

	public void onStopDiscoveryFailed(String serviceType, int errorCode) {

	}

	public void onDiscoveryStarted(String serviceType) {

	}

	public void onDiscoveryStopped(String serviceType) {

	}

	public void onServiceLost(NsdServiceInfo serviceInfo) {

	}
}
