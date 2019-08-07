package com.example.whatsplaying;

/**
 * duh
 *
 * @author Scott Albertine
 */
public enum Utils {
	;

	/**
	 * Run the given runnable in a new thread.
	 *
	 * @param r duh
	 */
	public static void runInNewThread(Runnable r) {
		new Thread(r).start();
	}

}
