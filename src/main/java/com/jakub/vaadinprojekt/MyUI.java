package com.jakub.vaadinprojekt;

import java.util.ArrayList;
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
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Push
@Theme("mytheme")
@Widgetset("com.jakub.vaadinprojekt.MyAppWidgetset")
public class MyUI extends UI implements BroadcastListener {

	private static final long serialVersionUID = 1L;

	Person person = new Person();
	BeanFieldGroup<Person> item = new BeanFieldGroup<Person>(Person.class);
	
	//Board buttons
	private List<Button> buttons = new ArrayList<Button>();

	//Layouts
    final VerticalLayout loginLayout = new VerticalLayout();
    final VerticalLayout lobbyLayout = new VerticalLayout();
    GridLayout gameLayout = new GridLayout(3, 3);
    
    //One game for everybody
    final TicTacToe game = new TicTacToe();
    
	@Override
    protected void init(VaadinRequest vaadinRequest) {
		getPage().setTitle("Tic Tac Toe Online!");
		
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
        	lobbyLayout.addComponent(new Label("user: "+ VaadinSession.getCurrent().getSession().getAttribute("user") ));
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
					lobbyLayout.addComponent(new Label("Nazwa użytkownika: "+ VaadinSession.getCurrent().getSession().getAttribute("user") ));
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
        lobbyLayout.setMargin(true);

        //Logout button
        Button gameBtn = new Button("Graj");
        gameBtn.addClickListener(new Button.ClickListener() {
			private static final long serialVersionUID = 1L;

			@Override
            public void buttonClick(ClickEvent event) {
                setContent(gameLayout);
            }
        });
        
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
        
        lobbyLayout.addComponent(gameBtn);
        lobbyLayout.addComponent(logoutBtn);
	}
	//------------------------------------
	
	//----------GAME-LAYOUT----------
	void prepareGameLayout(){
        gameLayout.setMargin(true);
        
        //create board
        //int j = 1;
		for(int i = 0; i < 9; i++){
			final int j = i + 1;
			buttons.add(new Button(""));
			//buttons.get(i).setId("" + j);
			buttons.get(i).setEnabled(false); 	//Board buttons are disabled until there are 2 players
			buttons.get(i).addClickListener(new Button.ClickListener() {
				private static final long serialVersionUID = 1L;
				@Override
				public void buttonClick(ClickEvent event) {
					game.makeMove(j, j-1);
				}
			});
			gameLayout.addComponent(buttons.get(i));
		}
	}
	//--------------------------------

	//---------------BROADCASTS----------
    @Override
    public void detach() {
        GameBroadcaster.unregister(this);
        super.detach();
    }

    //Game starts
	@Override
	public void receiveBroadcastStart() {
		access(new Runnable() {
            @Override
            public void run() {
            	for(Button btn : buttons){
            		btn.setEnabled(true);
            	}
            }
        });
	}

    //Player moves
	@Override
	public void receiveBroadcastMove(final int btn, final String playerTurn) {
        access(new Runnable() {
            @Override
            public void run() {
            	buttons.get(btn).setCaption(playerTurn);
            	//new Notification(btn.getId()).show(Page.getCurrent());
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
