package com.jakub.vaadinprojekt;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameBroadcaster {

	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	public interface BroadcastListener {
		void receiveBroadcast(int btn, String player);
	}

	private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

	public static synchronized void register(BroadcastListener listener) {
		listeners.add(listener);
	}

	public static synchronized void unregister(BroadcastListener listener) {
		listeners.remove(listener);
	}

	public static synchronized void broadcast(final int btn,final String player) {
		for (final BroadcastListener listener : listeners) executorService.execute(new Runnable() {
			@Override
			public void run() {
				listener.receiveBroadcast(btn, player);
			}
		});
	}

}
