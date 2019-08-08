package com.example.whatsplaying.util;

/**
 * duh
 *
 * @author Scott Albertine
 */
public enum Utils {
	;

	/**
	 * Android won't let us do anything involving network on the main thread, so we use this to conveniently wrap all network-touching operations.
	 *
	 * @param r duh
	 */
	public static void runInBackground(RiskyRunnable r) {
		new Thread(() -> {
			try {
				r.run();
			} catch (Exception e) {
				//noinspection CallToPrintStackTrace On purpose
				e.printStackTrace();
			}
		}).start();
	}

}
