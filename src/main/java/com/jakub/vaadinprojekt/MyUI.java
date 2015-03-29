package com.jakub.vaadinprojekt;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.servlet.annotation.WebServlet;

import com.jakub.vaadinprojekt.GameBroadcaster.BroadcastListener;
import com.jakub.vaadinprojekt.domain.Person;
import com.jakub.vaadinprojekt.domain.TicTacToe;
import com.vaadin.annotations.Push;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.annotations.Widgetset;
import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractTextField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

@Push
@Theme("mytheme")
@Widgetset("com.jakub.vaadinprojekt.MyAppWidgetset")
public class MyUI extends UI implements BroadcastListener {

	private static final long serialVersionUID = 1L;

	Person person = new Person();
	BeanFieldGroup<Person> item = new BeanFieldGroup<Person>(Person.class);
	
	//Board buttons
	private List<Button> buttons = new ArrayList<Button>();
	//Rooms
	private List<Button> roomButtons = new ArrayList<Button>();

	//Layouts
    final VerticalLayout loginLayout = new VerticalLayout();
    final HorizontalLayout lobbyLayout = new HorizontalLayout();
    final HorizontalLayout lobbyRoomsLayout = new HorizontalLayout();
    final VerticalLayout lobbySecondaryLayout = new VerticalLayout();
    final VerticalLayout lobbyNameLayout = new VerticalLayout();
    final VerticalLayout gameVertLayout = new VerticalLayout();
    final HorizontalLayout gameLayout = new HorizontalLayout();
    final VerticalLayout boardSymbolLayout = new VerticalLayout();
    final VerticalLayout playersLayout = new VerticalLayout();
    GridLayout boardLayout = new GridLayout(3, 3);
    
    //For broadcasts
    MyUI thisUser;
    int curRoom;	//Current room user is in
    public String nickname;
    String mySymbol;
    
    //For displaying player's symbol
	public Label symbolLabel = new Label();
    
    //Copy of a game
    final TicTacToe game = new TicTacToe();
    
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		getPage().setTitle("Tic Tac Toe Online!");
		
		thisUser = this;
		
    	GameBroadcaster.register(this);
        item.setItemDataSource(person);
        
        //Preparing layouts
		loginLayout.setMargin(true);
        loginLayout.addComponent(loginForm());  
        prepareLobbyLayout();
        prepareGameLayout();      

        //Setting content - login screen if not logged, else - lobby screen
        if(VaadinSession.getCurrent().getSession().getAttribute("user") == null)
        	setContent(loginLayout);
        else{
        	lobbyRoomsLayout.addComponent(new Label("user: "+ VaadinSession.getCurrent().getSession().getAttribute("user") ));
        	setContent(lobbyLayout);
        }
    }
	
	//-------------LOGIN-FORM----------------
	public FormLayout loginForm(){
		FormLayout form = new FormLayout();
		
		AbstractTextField name = (AbstractTextField) item.buildAndBind("Nickname", "nickname");
		name.setNullRepresentation("");
		form.addComponent(name);
		
		//Creating login button
		Button loginButton = new Button("Zaloguj");
		loginButton.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
			public void buttonClick(ClickEvent event) {
				try {
					item.commit();
					
					//set username in session
					VaadinSession.getCurrent().getSession().setAttribute("user", person.getNickname());
					lobbyNameLayout.addComponent(new Label("Nazwa użytkownika: "+ VaadinSession.getCurrent().getSession().getAttribute("user") ));
					nickname = (String) VaadinSession.getCurrent().getSession().getAttribute("user");
					setContent(lobbyLayout);
					
				} catch (CommitException e) {
					e.printStackTrace();
				}
			}
		});
		form.addComponent(loginButton);
		
		return form;
	}
	//-----------------------------------------
	
	//-----------LOBBY-LAYOUT-------------
	void prepareLobbyLayout(){
        //lobbyRoomsLayout.setMargin(true);
        //lobbyNameLayout.setMargin(true);
        lobbyLayout.setMargin(true);
        lobbyLayout.setSpacing(true);
        lobbySecondaryLayout.setSpacing(true);
        lobbySecondaryLayout.addComponent(lobbyNameLayout);
        
        //Buttons for game rooms
        for(int i = 0; i < 3; i++){
			final int j = i + 1;
			roomButtons.add(new Button("Pokój " + j));
			roomButtons.get(i).addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					GameBroadcaster.broadcastAddToRoom(j, thisUser);
					curRoom = j;
					setContent(gameLayout);
				}
			});
			lobbyRoomsLayout.addComponent(roomButtons.get(i));
		}
        
      //Logout button
        Button logoutBtn = new Button("Wyloguj");
        logoutBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(ClickEvent event) {
				VaadinSession.getCurrent().close();
                setContent(loginLayout);
            }
        });
        
        lobbySecondaryLayout.addComponent(lobbyRoomsLayout);
        lobbyLayout.addComponent(lobbySecondaryLayout);
        lobbyLayout.addComponent(logoutBtn);
	}
	//------------------------------------
	
	//----------GAME-LAYOUT----------
	void prepareGameLayout(){
        gameVertLayout.setMargin(true);
        boardSymbolLayout.setMargin(true);
        
        //create board
		for(int i = 0; i < 9; i++){
			final int j = i;
			buttons.add(new Button(""));
			buttons.get(i).addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					game.makeMove(j, j, curRoom);
				}
			});
			boardLayout.addComponent(buttons.get(i));
		}
		
		//Leaving room
		Button leaveBtn = new Button("Wyjdź z pokoju");
		leaveBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;
			@Override
	        public void buttonClick(ClickEvent event) {
				GameBroadcaster.leaveRoom(thisUser, curRoom);
	            setContent(lobbyLayout);
	            curRoom = -1;
	            clearBoard();
	            unlockBoard();
	        }
	    });
		gameVertLayout.addComponent(leaveBtn);
		gameVertLayout.addComponent(new Label("Użytkownicy w pokoju:"));
		gameVertLayout.addComponent(playersLayout);

		boardSymbolLayout.addComponent(symbolLabel);
		boardSymbolLayout.addComponent(boardLayout);
		
		gameLayout.addComponent(boardSymbolLayout);
		gameLayout.addComponent(gameVertLayout);
	}
	
	void updateBoard(String[] newBoard){
		for(int i = 0; i < 9; i++){
			if(newBoard[i] != "n"){
				buttons.get(i).setCaption(newBoard[i]);
			}
			//Check if my turn, if not - lock buttons
			if(game.playerTurn == mySymbol){
				buttons.get(i).setEnabled(true);
			} else {
				buttons.get(i).setEnabled(false);
			}
		}
	}
	
	void clearBoard(){
		for(int i = 0; i < 9; i++){
			buttons.get(i).setCaption("");
			game.board[i] = "n";
		}
		game.playerTurn = "x";
	}
	
	void unlockBoard(){
		for(int i = 0; i < 9; i++){
			buttons.get(i).setEnabled(true);
		}
	}
	//--------------------------------

	//---------------BROADCASTS----------
    @Override
    public void detach() {
        GameBroadcaster.unregister(this);
        super.detach();
    }

    //Player moves
	@Override
	public void receiveBroadcastMove(final int btn, final String playerTurn) {
        access(new Runnable() {
            @Override
            public void run() {
            	buttons.get(btn).setCaption(playerTurn);
            }
        });
	}
	
	//Receive request for current status and send it
	@Override
	public void receiveBroadcastRequestStatus() {
		access(new Runnable(){
			@Override
			public void run(){
				GameBroadcaster.sendGameStatus(curRoom, game.board, game.playerTurn);
			}
		});
	}
	
	//Receive game update
	@Override
	public void receiveBroadcastGameUpdate(final String[] newBoard, final String newPlayerTurn){
		access(new Runnable() {
            @Override
            public void run() {
            	for(int i = 0; i < 9; i++){
            		game.board[i] = newBoard[i];
            	}
            	
            	game.playerTurn = newPlayerTurn;
            	updateBoard(newBoard);
            }
        });
	}
	
	//Update player list
	@Override
	public void receiveBroadcastUpdatePlayers(final List<String> usersRoom) {
		access(new Runnable() {
            @Override
            public void run() {
            	playersLayout.removeAllComponents();
            	for(String name : usersRoom){
            		playersLayout.addComponent(new Label(name));
            	}
            }
		});
	}
	
	//Someone won
	@Override
	public void receiveBroadcastGameEnded(final String winner) {
		access(new Runnable() {
            @Override
            public void run() {
            	clearBoard();
            	
            	Window subWindow = new Window("Koniec gry");
                VerticalLayout subContent = new VerticalLayout();
                subContent.setMargin(true);
                subWindow.setContent(subContent);
                
                subContent.addComponent(new Label("Wygrywa gracz " + winner + "!"));
                
                subWindow.setHeight("100px");
                subWindow.setWidth("200px");
                subWindow.center();
                subWindow.setModal(true);
                addWindow(subWindow);
            }
        });
	}

	//Game ended with draw
	@Override
	public void receiveBroadcastGameEndedDraw() {
		access(new Runnable() {
            @Override
            public void run() {
            	clearBoard();
            	
            	Window subWindow = new Window("Koniec gry");
                VerticalLayout subContent = new VerticalLayout();
                subContent.setMargin(true);
                subWindow.setContent(subContent);
                
                subContent.addComponent(new Label("Nikt nie wygrał!"));
                
                subWindow.setHeight("100px");
                subWindow.setWidth("200px");
                subWindow.center();
                subWindow.setModal(true);
                addWindow(subWindow);
            }
        });
	}
	//-------------------------------------
	
    @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
    @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
    public static class MyUIServlet extends VaadinServlet {

		private static final long serialVersionUID = 1L;
    }

}
