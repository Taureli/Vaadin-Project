package com.jakub.vaadinprojekt.domain;

import com.jakub.vaadinprojekt.GameBroadcaster;

public class TicTacToe {

	String playerX;
	String playerO;
	//public String[][] board = new String[3][3];
	public String[] board = new String[9];
	boolean winX = false;
	boolean winO = false;
	public String playerTurn = "x";
	
	public TicTacToe(){
//		this.playerX = player1;
//		this.playerO = player2;
		
		//Filling board with "null"
		for(int i = 0; i < 9; i++){
			board[i] = "n";
		}
		
//		for(int i = 0; i < 3; i++){
//			for(int j = 0; j < 3; j++){
//				board[i][j] = "n";
//			}
//		}
	}
	
	public void makeMove(int z, int btn, int room){
//		int y = z/3;
//		int x = z%3 - 1;
//		if(z == 9 || z == 6 || z == 3){
//			y--;
//			x = 2;
//		}
		if(board[z] == "n"){
			board[z] = playerTurn;
			GameBroadcaster.broadcastMove(btn, playerTurn, room);
			//Check if someone won or the board is full
			if(checkWin()){
				GameBroadcaster.broadcastGameEnd(room, playerTurn);
				restartGame(room);
			} else if(checkFullBoard()){
				GameBroadcaster.broadcastGameEnd(room, "draw");
				restartGame(room);
			} else {
				nextTurn(room);
			}
		}
		
	}
	
	void restartGame(int room){
		for(int i = 0; i < 9; i++){
			board[i] = "n";
		}
		playerTurn = "x";
		GameBroadcaster.sendGameStatus(room, board, playerTurn);
	}
	
	public boolean checkWin(){
		//calls checkers for all win conditions
		if((checkRowsWin() || checkColumnsWin() || checkDiagonalsWin()))
			return true;
		else
			return false;
	}

	//-----------Win conditions checkers------------//
	public boolean checkRowsWin() {
        for (int i = 1; i < 8; i += 3) {
//            if (checkRowCol(board[i][0], board[i][1], board[i][2]) == true) {
//                return true;
//            }
        	if(checkRowCol(board[i-1], board[i], board[i+1]) == true){
        		return true;
        	}
        }
        return false;
    }

    private boolean checkColumnsWin() {
        for (int i = 0; i < 3; i++) {
//            if (checkRowCol(board[0][i], board[1][i], board[2][i]) == true) {
//                return true;
//            }
        	if(checkRowCol(board[i], board[i+3], board[i+6]) == true){
        		return true;
        	}
        }
        return false;
    }
     
    private boolean checkDiagonalsWin() {
        return ((checkRowCol(board[0], board[4], board[8]) == true) || (checkRowCol(board[2], board[4], board[6]) == true));
    }

    // Checks if all symbols are the same
    private boolean checkRowCol(String c1, String c2, String c3) {
        return ((c1 != "n") && (c1 == c2) && (c2 == c3));
    }
    //---------------------------------------------//
	
    public boolean checkFullBoard(){
		int emptyCounter = 0;
		for(int i = 0; i < 9; i++){
//			for(int j = 0; j < 3; j++){
//				if(board[i][j] == "n"){
//					emptyCounter++;
//				}
//			}
			if(board[i] == "n"){
				emptyCounter++;
			}
		}
		
		if(emptyCounter == 0)
			return true;
		else 
			return false;
	}
	
    public void nextTurn(int room){
		if(playerTurn == "x")
			playerTurn = "o";
		else
			playerTurn = "x";
		GameBroadcaster.sendGameStatus(room, board, playerTurn);
	}
	
}
