package com.example.whatsplaying.util;

/**
 * Like Runnable, but it can throw an exception.
 *
 * @author Scott Albertine
 */
@FunctionalInterface
public interface RiskyRunnable {
	/**
	 * duh
	 *
	 * @throws Exception duh
	 */
	@SuppressWarnings("ProhibitedExceptionDeclared")
	void run() throws Exception;
}
