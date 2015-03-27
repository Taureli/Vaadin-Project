package com.jakub.vaadinprojekt;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameBroadcaster {

	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	public interface BroadcastListener {
		void receiveBroadcastMove(int btn, String player);
	}

	private static LinkedList<BroadcastListener> listeners = new LinkedList<BroadcastListener>();

	public static synchronized void register(BroadcastListener listener) {
		listeners.add(listener);
	}

	public static synchronized void unregister(BroadcastListener listener) {
		listeners.remove(listener);
	}

	//Rooms
	private static Map<Integer, LinkedList<BroadcastListener>> rooms = new HashMap<Integer, LinkedList<BroadcastListener>>();
	
	public static synchronized void broadcastAddToRoom(Integer roomId, BroadcastListener listener){
		//First connection to room
		if(rooms.get(roomId) == null){
	        LinkedList<BroadcastListener> roomListeners = new LinkedList<BroadcastListener>();
	        roomListeners.add(listener);
	        rooms.put(roomId, roomListeners);
	        System.out.println("NOWE");
		} else {
			//add user to room
			rooms.get(roomId).add(listener);
		}
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

}
