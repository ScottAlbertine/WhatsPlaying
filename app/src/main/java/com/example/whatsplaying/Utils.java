package com.example.whatsplaying;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * duh
 *
 * @author Scott Albertine
 */
public class Utils {

	public static ObjectMapper createJSONMapper() {
		ObjectMapper jsonMapper = new ObjectMapper();
		jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
		return jsonMapper;
	}

	public static void runInNewThread(Runnable r) {
		new Thread(r).start();
	}

}
