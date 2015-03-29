package com.jakub.vaadinprojekt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class GameBroadcaster {

	static ExecutorService executorService = Executors.newSingleThreadExecutor();

	public interface BroadcastListener {
		void receiveBroadcastMove(int btn, String player);
		void receiveBroadcastRequestStatus();
		void receiveBroadcastGameUpdate(String[] board, String playerTurn);
		void receiveBroadcastGameEnded(String winner);
		void receiveBroadcastGameEndedDraw();
		void receiveBroadcastUpdatePlayers(List<String> usersRoom);
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
	        ((MyUI)listener).mySymbol = "x";
		} else {
			//add user to room
			rooms.get(roomId).add(listener);
			
			//Check if anyone else is in the room, if so - take current game status
			if(rooms.get(roomId).size() != 0){
				broadcastRequestStatus(roomId, listener);
				//Set correct symbol
				if( ((MyUI)rooms.get(roomId).get(0)).mySymbol == "x" ){
					((MyUI)listener).mySymbol = "o";
				} else {
					((MyUI)listener).mySymbol = "x";
				}
			} else {
				((MyUI)listener).mySymbol = "x";
			}
		}
		((MyUI)listener).symbolLabel.setCaption("Mój symbol - " + ((MyUI)listener).mySymbol);
		broadcastUpdatePlayers(roomId);
    }
	
	//Leaving room
	public static synchronized void leaveRoom(BroadcastListener listener, int roomId){
		rooms.get(roomId).remove(listener);
		broadcastUpdatePlayers(roomId);
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
				List<String> usersRoom = new ArrayList<String>();
				for(BroadcastListener listener : rooms.get(roomId)){
					usersRoom.add(((MyUI)listener).nickname);
				}
				listener.receiveBroadcastGameUpdate(board, playerTurn);
			}
		});
	}
	
	//Game ended
	public static synchronized void broadcastGameEnd(final int roomId, final String winner){
		for(final BroadcastListener listener : rooms.get(roomId)) executorService.execute(new Runnable(){
			@Override
			public void run() {
				if(winner == "draw")
					listener.receiveBroadcastGameEndedDraw();
				else
					listener.receiveBroadcastGameEnded(winner);
			}
		});
	}
	
	//Update player list
	public static synchronized void broadcastUpdatePlayers(final int roomId){
		for(final BroadcastListener listener : rooms.get(roomId)) executorService.execute(new Runnable(){
			@Override
			public void run() {
				List<String> usersRoom = new ArrayList<String>();
				for(BroadcastListener player : rooms.get(roomId)){
					usersRoom.add(((MyUI)player).nickname);
				}
				listener.receiveBroadcastUpdatePlayers(usersRoom);
			}
		});
	}

}
