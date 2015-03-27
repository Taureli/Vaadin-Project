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
		void receiveBroadcastRequestStatus();
		void receiveBroadcastGameUpdate(String[] board, String playerTurn);
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
	
	//Joining room
	public static synchronized void broadcastAddToRoom(Integer roomId, BroadcastListener listener){
		//First connection to room - create list
		if(rooms.get(roomId) == null){
	        LinkedList<BroadcastListener> roomListeners = new LinkedList<BroadcastListener>();
	        roomListeners.add(listener);
	        rooms.put(roomId, roomListeners);
		} else {
			//add user to room
			rooms.get(roomId).add(listener);
			
			//Check if anyone else is in the room, if so - take current game status and lock room
			if(rooms.get(roomId).size() != 0){
				broadcastRequestStatus(roomId, listener);
			}
		}
    }
	
	//Leaving room
	public static synchronized void leaveRoom(BroadcastListener listener, int roomId){
		rooms.get(roomId).remove(listener);
	}
	
	//Movement on board
	public static synchronized void broadcastMove(final int btn,final String player, final int roomId) {
		for(final BroadcastListener listener : rooms.get(roomId)) executorService.execute(new Runnable(){
			@Override
			public void run() {
				listener.receiveBroadcastMove(btn, player);
			}
		});
	}
	
	//Request current game status from other player
	public static synchronized void broadcastRequestStatus(final int roomId, final BroadcastListener listener){
		executorService.execute(new Runnable(){
			@Override
			public void run() {
				BroadcastListener otherPlayer = listener;
				for(BroadcastListener user : rooms.get(roomId)){
					if(user != listener){
						otherPlayer = user;
					}
				}
				otherPlayer.receiveBroadcastRequestStatus();
			}
		});
	}
	
	//Send game status to others in room
	public static synchronized void sendGameStatus(final int roomId, final String[] board, final String playerTurn){
		for(final BroadcastListener listener : rooms.get(roomId)) executorService.execute(new Runnable(){
			@Override
			public void run() {
				listener.receiveBroadcastGameUpdate(board, playerTurn);
			}
		});
	}

}
