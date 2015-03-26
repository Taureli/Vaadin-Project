package com.jakub.vaadinprojekt.domain;

import com.jakub.vaadinprojekt.GameBroadcaster;

public class TicTacToe {

	String playerX;
	String playerO;
	public String[][] board = new String[3][3];
	boolean winX = false;
	boolean winO = false;
	String playerTurn = "x";
	
	public TicTacToe(){
//		this.playerX = player1;
//		this.playerO = player2;
		
		//Filling board with "null"
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				board[i][j] = "n";
			}
		}
	}
	
	public void makeMove(int z, int btn){
		int y = z/3;
		int x = z%3 - 1;
		if(z == 9 || z == 6 || z == 3){
			y--;
			x = 2;
		}
		if(board[y][x] == "n"){
			GameBroadcaster.broadcastMove(btn, playerTurn);
			board[y][x] = playerTurn;
			nextTurn();
		}
		
	}
	
	public boolean checkWin(){
		//calls checkers for all win conditions
		if((checkRowsWin() || checkColumnsWin() || checkDiagonalsWin())){
			return true;
		}
		
		return false;
	}

	//-----------Win conditions checkers------------//
	public boolean checkRowsWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[i][0], board[i][1], board[i][2]) == true) {
                return true;
            }
        }
        return false;
    }

    private boolean checkColumnsWin() {
        for (int i = 0; i < 3; i++) {
            if (checkRowCol(board[0][i], board[1][i], board[2][i]) == true) {
                return true;
            }
        }
        return false;
    }
     
    private boolean checkDiagonalsWin() {
        return ((checkRowCol(board[0][0], board[1][1], board[2][2]) == true) || (checkRowCol(board[0][2], board[1][1], board[2][0]) == true));
    }

    // Checks if all symbols are the same
    private boolean checkRowCol(String c1, String c2, String c3) {
        return ((c1 != "n") && (c1 == c2) && (c2 == c3));
    }
    //---------------------------------------------//
	
    public boolean checkFullBoard(){
		int emptyCounter = 0;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(board[i][j] == "n"){
					emptyCounter++;
				}
			}
		}
		
		if(emptyCounter == 0){
			return true;
		}
		return false;
	}
	
    public void nextTurn(){
		if(playerTurn == "x")
			playerTurn = "o";
		else
			playerTurn = "x";
	}
	
}
