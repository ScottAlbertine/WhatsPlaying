package com.example.whatsplaying.dto;

import android.net.nsd.NsdServiceInfo;

import java.net.InetAddress;
import java.nio.charset.StandardCharsets;

/**
 * @author Scott Albertine
 */
public class Chromecast {
	public String name;
	public InetAddress ip;
	public int port;

	public Chromecast(NsdServiceInfo serviceInfo) {
		name = new String(serviceInfo.getAttributes().get("fn"), StandardCharsets.UTF_8);
		ip = serviceInfo.getHost();
		port = serviceInfo.getPort();
	}

}
