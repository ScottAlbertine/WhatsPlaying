package com.example.whatsplaying;

/**
 * duh
 *
 * @author Scott Albertine
 */
public class Utils {

	public static void runInNewThread(Runnable r) {
		new Thread(r).start();
	}

}
