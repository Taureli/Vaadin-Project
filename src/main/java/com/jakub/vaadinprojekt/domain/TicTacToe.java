package com.jakub.vaadinprojekt.domain;

public class TicTacToe {

	String playerX;
	String playerO;
	char[][] board = new char[3][3];
	boolean winX = false;
	boolean winO = false;
	char playerTurn = 'x';
	
	public TicTacToe(String player1, String player2){
		this.playerX = player1;
		this.playerO = player2;
		
		//Filling board with "null"
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				board[i][j] = 'n';
			}
		}
	}
	
	void makeMove(int y, int x){
		if(board[y][x] != 'n'){
			board[y][x] = playerTurn;
			nextTurn();
		}
	}
	
	boolean checkWin(){
		//calls checkers for all win conditions
		if((checkRowsWin() || checkColumnsWin() || checkDiagonalsWin())){
			return true;
		}
		
		return false;
	}

	//-----------Win conditions checkers------------//
    private boolean checkRowsWin() {
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
    private boolean checkRowCol(char c1, char c2, char c3) {
        return ((c1 != 'n') && (c1 == c2) && (c2 == c3));
    }
    //---------------------------------------------//
	
	boolean checkFullBoard(){
		int emptyCounter = 0;
		for(int i = 0; i < 3; i++){
			for(int j = 0; j < 3; j++){
				if(board[i][j] == 'n'){
					emptyCounter++;
				}
			}
		}
		
		if(emptyCounter == 0){
			return true;
		}
		return false;
	}
	
	void nextTurn(){
		if(playerTurn == 'x')
			playerTurn = 'o';
		else
			playerTurn = 'x';
	}
	
}
