package com.jakub.vaadinprojekt;

import java.util.LinkedList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameBroadcaster {

	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	public interface BroadcastListener {
		void receiveBroadcastMove(int btn, String player);
		void receiveBroadcastStart();
	}

	private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

	public static synchronized void register(BroadcastListener listener) {
		listeners.add(listener);
		//If there are 2 players, enable board
		if(listeners.size() >= 2){
			broadcastStart();
		}
	}

	public static synchronized void unregister(BroadcastListener listener) {
		listeners.remove(listener);
	}

	//Movement on board
	public static synchronized void broadcastMove(final int btn,final String player) {
		for (final BroadcastListener listener : listeners) executorService.execute(new Runnable() {
			@Override
			public void run() {
				listener.receiveBroadcastMove(btn, player);
			}
		});
	}
	
	//Game start
	public static synchronized void broadcastStart(){
		for(final BroadcastListener listener:listeners) executorService.execute(new Runnable(){
			@Override
			public void run(){
				listener.receiveBroadcastStart();
			}
		});
	}

}
